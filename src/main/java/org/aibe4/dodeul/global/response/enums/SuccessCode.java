package org.aibe4.dodeul.global.response.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.aibe4.dodeul.global.response.BaseCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessCode implements BaseCode {

    // ==========================
    // 200 OK (일반적인 성공)
    // ==========================

    // 1. 기본/공통
    SUCCESS(HttpStatus.OK, "요청이 성공적으로 처리되었습니다."),

    // 2. CRUD 조회/수정/삭제
    SELECT_SUCCESS(HttpStatus.OK, "조회 성공"),
    UPDATE_SUCCESS(HttpStatus.OK, "수정 성공"),
    DELETE_SUCCESS(HttpStatus.OK, "삭제 성공"),

    // 3. 인증/인가 (Auth)
    LOGIN_SUCCESS(HttpStatus.OK, "로그인 성공"),
    LOGOUT_SUCCESS(HttpStatus.OK, "로그아웃 성공"),
    REISSUE_SUCCESS(HttpStatus.OK, "토큰 재발급 성공"),

    // 4. 유효성 검사 (중복 확인 등)
    CHECK_SUCCESS(HttpStatus.OK, "사용 가능한 값입니다."),
    AVAILABLE_NICKNAME(HttpStatus.OK, "사용 가능한 닉네임입니다."),
    AVAILABLE_EMAIL(HttpStatus.OK, "사용 가능한 이메일입니다."),

    // ==========================
    // 201 Created (리소스 생성)
    // ==========================

    CREATE_SUCCESS(HttpStatus.CREATED, "생성 성공"),
    SIGNUP_SUCCESS(HttpStatus.CREATED, "회원가입 성공"),

    // ==========================
    // 202 Accepted (비동기 처리)
    // ==========================

    ACCEPTED(HttpStatus.ACCEPTED, "요청이 접수되었습니다."); // 배치 처리나 알림 발송 등 즉시 완료되지 않는 작업

    private final HttpStatus httpStatus;
    private final String message;
}
