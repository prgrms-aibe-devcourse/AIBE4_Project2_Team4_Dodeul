package org.aibe4.dodeul.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.member.model.dto.ProfileDto;
import org.aibe4.dodeul.domain.member.model.entity.Member;
import org.aibe4.dodeul.domain.member.model.entity.Profile;
import org.aibe4.dodeul.domain.member.model.enums.Provider;
import org.aibe4.dodeul.domain.member.model.enums.Role;
import org.aibe4.dodeul.domain.member.model.repository.MemberRepository;
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

    public Member getMemberOrThrow(Long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() ->
                new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS, "인증 정보가 유효하지 않습니다."));
    }

    /**
     * 임시 닉네임(user_*) 여부 판단 - 최초 가입 직후 닉네임 온보딩 판단용
     */
    public boolean hasTemporaryNickname(Member member) {
        String nickname = member.getNickname();
        return nickname == null || nickname.isBlank() || nickname.startsWith("user_");
    }

    /**
     * 역할 분기 없이 프로필 공통 필드를 조회
     */
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
    public Long registerLocal(String email, String rawPassword, Role role) {
        if (email == null || email.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "이메일을 입력해주세요.");
        }
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "비밀번호를 입력해주세요.");
        }
        if (role == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "역할 선택이 필요합니다.");
        }

        if (memberRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.ALREADY_EXISTS, "이미 가입된 이메일입니다.");
        }

        String passwordHash = passwordEncoder.encode(rawPassword);
        String tempNickname = generateTempNickname();

        Member member = Member.builder()
            .email(email.trim())
            .passwordHash(passwordHash)
            .provider(Provider.LOCAL)
            .providerId(null)
            .role(role)
            .nickname(tempNickname)
            .build();

        return memberRepository.save(member).getId();
    }

    /**
     * 닉네임 설정 / 변경 정책:
     * - 2~10자
     * - 한글 / 영문 / 숫자만 허용
     * - 중복 불가
     */
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

    /**
     * Google OAuth2 로그인:
     * providerId(sub) 기준으로 회원을 찾거나 없으면 생성
     */
    @Transactional
    public Member findOrCreateGoogleMember(String email, String providerId, Role role) {
        if (providerId == null || providerId.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "구글 사용자 식별자(sub)가 유효하지 않습니다.");
        }

        return memberRepository.findByProviderAndProviderId(Provider.GOOGLE, providerId)
            .orElseGet(() -> createGoogleMember(email, providerId, role));
    }

    /**
     * GitHub OAuth2 로그인:
     * providerId(id) 기준으로 회원을 찾거나 없으면 생성
     * 정책:
     * - email이 제공되는 경우에만 기존 이메일과 중복이면 가입 차단
     * - email이 null이면 중복 체크 없이 진행(계정 분리 허용)
     */
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

        return memberRepository.save(member);
    }

    private Member createGithubMember(String email, String providerId, Role role) {
        if (role == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "역할 선택이 필요합니다.");
        }

        // email은 null일 수 있음
        String normalizedEmail = null;
        if (email != null && !email.isBlank()) {
            normalizedEmail = email.trim();

            // email이 제공된 경우에만 중복 이메일 차단
            if (memberRepository.existsByEmail(normalizedEmail)) {
                throw new BusinessException(
                    ErrorCode.ALREADY_EXISTS,
                    "이미 가입된 이메일입니다. 기존 로그인 방식으로 로그인해주세요."
                );
            }
        }

        String tempNickname = generateTempNickname();

        Member member = Member.builder()
            .email(normalizedEmail)     // null 가능
            .passwordHash(null)
            .provider(Provider.GITHUB)
            .providerId(providerId)
            .role(role)
            .nickname(tempNickname)
            .build();

        return memberRepository.save(member);
    }

    private String generateTempNickname() {
        return "user_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }
}
