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
        if (memberRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.ALREADY_EXISTS, "이미 가입된 이메일입니다.");
        }

        String passwordHash = passwordEncoder.encode(rawPassword);
        String tempNickname = "user_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);

        Member member = Member.builder()
            .email(email)
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
     * Google OAuth2 로그인 시 providerId(sub) 기준으로 회원을 찾거나 없으면 생성
     */
    @Transactional
    public Member findOrCreateGoogleMember(String email, String providerId, Role role) {
        return memberRepository
            .findByProviderAndProviderId(Provider.GOOGLE, providerId)
            .orElseGet(() -> createGoogleMember(email, providerId, role));
    }

    private Member createGoogleMember(String email, String providerId, Role role) {
        if (role == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "역할 선택이 필요합니다.");
        }

        if (email == null || email.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "이메일 정보를 불러올 수 없습니다.");
        }

        if (memberRepository.existsByEmail(email)) {
            throw new BusinessException(
                ErrorCode.ALREADY_EXISTS,
                "이미 가입된 이메일입니다. 로컬 로그인 또는 계정 연동 정책 확인이 필요합니다."
            );
        }

        String tempNickname = "user_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);

        Member member = Member.builder()
            .email(email)
            .passwordHash(null)
            .provider(Provider.GOOGLE)
            .providerId(providerId)
            .role(role)
            .nickname(tempNickname)
            .build();

        return memberRepository.save(member);
    }
}
