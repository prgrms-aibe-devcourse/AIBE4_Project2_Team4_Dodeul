package org.aibe4.dodeul.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.member.model.dto.response.ProfileDto;
import org.aibe4.dodeul.domain.member.model.entity.Member;
import org.aibe4.dodeul.domain.member.model.entity.MenteeProfile;
import org.aibe4.dodeul.domain.member.model.entity.MentorProfile;
import org.aibe4.dodeul.domain.member.model.entity.Profile;
import org.aibe4.dodeul.domain.member.model.enums.Provider;
import org.aibe4.dodeul.domain.member.model.enums.Role;
import org.aibe4.dodeul.domain.member.model.repository.MemberRepository;
import org.aibe4.dodeul.domain.member.model.repository.MenteeProfileRepository;
import org.aibe4.dodeul.domain.member.model.repository.MentorProfileRepository;
import org.aibe4.dodeul.global.exception.BusinessException;
import org.aibe4.dodeul.global.response.enums.ErrorCode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    private final MentorProfileRepository mentorProfileRepository;
    private final MenteeProfileRepository menteeProfileRepository;

    public Member getMemberOrThrow(Long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() ->
                new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS, "인증 정보가 유효하지 않습니다."));
    }

    public boolean hasTemporaryNickname(Member member) {
        String nickname = member.getNickname();
        return nickname == null || nickname.isBlank() || nickname.startsWith("user_");
    }

    public ProfileDto getMemberProfile(Long memberId) {
        Member member = getMemberOrThrow(memberId);

        Profile profile = member.getProfile();
        if (profile == null) {
            return ProfileDto.empty(member.getId(), member.getNickname());
        }

        return new ProfileDto(
            member.getId(),
            member.getNickname(),
            profile.getIntro(),
            profile.getProfileUrl(),
            profile.getJob()
        );
    }

    @Transactional
    public Long registerLocal(String email, String rawPassword, String confirmPassword, Role role) {
        if (email == null || email.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "이메일을 입력해주세요.");
        }
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "비밀번호를 입력해주세요.");
        }
        if (confirmPassword == null || confirmPassword.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "비밀번호 확인을 입력해주세요.");
        }
        if (!rawPassword.equals(confirmPassword)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "비밀번호가 일치하지 않습니다.");
        }
        if (role == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "역할 선택이 필요합니다.");
        }

        String normalizedEmail = email.trim();

        if (memberRepository.existsByEmail(normalizedEmail)) {
            throw new BusinessException(ErrorCode.ALREADY_EXISTS, "이미 가입된 이메일입니다.");
        }

        String passwordHash = passwordEncoder.encode(rawPassword);
        String tempNickname = generateTempNickname();

        Member member = Member.builder()
            .email(normalizedEmail)
            .passwordHash(passwordHash)
            .provider(Provider.LOCAL)
            .providerId(null)
            .role(role)
            .nickname(tempNickname)
            .build();

        Member saved = memberRepository.save(member);

        ensureProfileCreated(saved);

        return saved.getId();
    }

    @Transactional
    public Long registerLocal(String email, String rawPassword, Role role) {
        return registerLocal(email, rawPassword, rawPassword, role);
    }

    @Transactional
    public void updateNickname(Long memberId, String nickname) {
        if (nickname == null || nickname.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "닉네임을 입력해주세요.");
        }

        String trimmed = nickname.trim();

        if (trimmed.length() < 2 || trimmed.length() > 10) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "닉네임은 2~10자여야 합니다.");
        }

        if (!trimmed.matches("^[a-zA-Z0-9가-힣]+$")) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "닉네임은 한글/영문/숫자만 가능합니다.");
        }

        Member member = getMemberOrThrow(memberId);

        if (trimmed.equals(member.getNickname())) {
            return;
        }

        if (memberRepository.existsByNickname(trimmed)) {
            throw new BusinessException(ErrorCode.ALREADY_EXISTS, "이미 사용 중인 닉네임입니다.");
        }

        member.updateNickname(trimmed);
    }

    @Transactional
    public Member findOrCreateGoogleMember(String email, String providerId, Role role) {
        if (providerId == null || providerId.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "구글 사용자 식별자(sub)가 유효하지 않습니다.");
        }

        return memberRepository.findByProviderAndProviderId(Provider.GOOGLE, providerId)
            .orElseGet(() -> createGoogleMember(email, providerId, role));
    }

    @Transactional
    public Member findOrCreateGithubMember(String email, String providerId, Role role) {
        if (providerId == null || providerId.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "GitHub 사용자 식별자(id)가 유효하지 않습니다.");
        }

        return memberRepository.findByProviderAndProviderId(Provider.GITHUB, providerId)
            .orElseGet(() -> createGithubMember(email, providerId, role));
    }

    private Member createGoogleMember(String email, String providerId, Role role) {
        if (role == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "역할 선택이 필요합니다.");
        }

        if (email == null || email.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "이메일 정보를 불러올 수 없습니다.");
        }

        String normalizedEmail = email.trim();

        if (memberRepository.existsByEmail(normalizedEmail)) {
            throw new BusinessException(
                ErrorCode.ALREADY_EXISTS,
                "이미 가입된 이메일입니다. 다른 로그인 수단을 선택해 주세요."
            );
        }

        String tempNickname = generateTempNickname();

        Member member = Member.builder()
            .email(normalizedEmail)
            .passwordHash(null)
            .provider(Provider.GOOGLE)
            .providerId(providerId)
            .role(role)
            .nickname(tempNickname)
            .build();

        Member saved = memberRepository.save(member);

        ensureProfileCreated(saved);

        return saved;
    }

    private Member createGithubMember(String email, String providerId, Role role) {
        if (role == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "역할 선택이 필요합니다.");
        }

        String normalizedEmail = null;
        if (email != null && !email.isBlank()) {
            normalizedEmail = email.trim();

            if (memberRepository.existsByEmail(normalizedEmail)) {
                throw new BusinessException(
                    ErrorCode.ALREADY_EXISTS,
                    "이미 가입된 이메일입니다. 기존 로그인 방식으로 로그인해주세요."
                );
            }
        }

        String tempNickname = generateTempNickname();

        Member member = Member.builder()
            .email(normalizedEmail)
            .passwordHash(null)
            .provider(Provider.GITHUB)
            .providerId(providerId)
            .role(role)
            .nickname(tempNickname)
            .build();

        Member saved = memberRepository.save(member);
        
        ensureProfileCreated(saved);

        return saved;
    }

    /**
     * 신규 회원이 멘토 목록 검색(join member.mentorProfile)에서 누락되지 않도록
     * 역할에 맞는 프로필 row를 바로 생성합니다.
     */
    private void ensureProfileCreated(Member member) {
        if (member == null || member.getRole() == null) return;

        if (member.getRole() == Role.MENTOR) {
            // 이미 있으면 생성하지 않음
            if (!mentorProfileRepository.existsByMemberId(member.getId())) {
                mentorProfileRepository.save(MentorProfile.create(member));
            }
        } else if (member.getRole() == Role.MENTEE) {
            if (!menteeProfileRepository.existsByMemberId(member.getId())) {
                menteeProfileRepository.save(MenteeProfile.create(member));
            }
        }
    }

    private String generateTempNickname() {
        return "user_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }
}
