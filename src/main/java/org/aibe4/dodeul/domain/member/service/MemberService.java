package org.aibe4.dodeul.domain.member.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.member.model.entity.Member;
import org.aibe4.dodeul.domain.member.model.enums.Provider;
import org.aibe4.dodeul.domain.member.model.enums.Role;
import org.aibe4.dodeul.domain.member.model.repository.MemberRepository;
import org.aibe4.dodeul.global.exception.CustomException;
import org.aibe4.dodeul.global.response.enums.ErrorCode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Member getMemberOrThrow(Long memberId) {
        return memberRepository
                .findById(memberId)
                .orElseThrow(
                        () ->
                                new CustomException(
                                        ErrorCode.UNAUTHORIZED_ACCESS, "인증 정보가 유효하지 않습니다."));
    }

    /** 임시 닉네임(user_*) 여부 판단 - 최초 가입 직후 닉네임 온보딩 판단용 */
    public boolean hasTemporaryNickname(Member member) {
        String nickname = member.getNickname();
        return nickname == null || nickname.isBlank() || nickname.startsWith("user_");
    }

    @Transactional
    public Long registerLocal(String email, String rawPassword, Role role) {
        if (memberRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.ALREADY_EXISTS, "이미 가입된 이메일입니다.");
        }

        String passwordHash = passwordEncoder.encode(rawPassword);

        String tempNickname =
                "user_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);

        Member member =
                Member.builder()
                        .email(email)
                        .passwordHash(passwordHash)
                        .provider(Provider.LOCAL)
                        .providerId(null)
                        .role(role)
                        .nickname(tempNickname)
                        .build();

        return memberRepository.save(member).getId();
    }

    /** 닉네임 설정 / 변경 정책: - 2~10자 - 한글 / 영문 / 숫자만 허용 - 중복 불가 */
    @Transactional
    public void updateNickname(Long memberId, String nickname) {
        if (nickname == null || nickname.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE, "닉네임을 입력해주세요.");
        }

        String trimmed = nickname.trim();

        if (trimmed.length() < 2 || trimmed.length() > 10) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE, "닉네임은 2~10자여야 합니다.");
        }

        if (!trimmed.matches("^[a-zA-Z0-9가-힣]+$")) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE, "닉네임은 한글/영문/숫자만 가능합니다.");
        }

        Member member = getMemberOrThrow(memberId);

        if (trimmed.equals(member.getNickname())) {
            return;
        }

        if (memberRepository.existsByNickname(trimmed)) {
            throw new CustomException(ErrorCode.ALREADY_EXISTS, "이미 사용 중인 닉네임입니다.");
        }

        member.updateNickname(trimmed);
    }
}
