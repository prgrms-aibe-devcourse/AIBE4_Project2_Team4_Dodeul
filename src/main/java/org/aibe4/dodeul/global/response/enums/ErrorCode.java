package org.aibe4.dodeul.global.response.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.global.response.BaseCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements BaseCode {

    // ====================================================
    // 400 Bad Request (잘못된 요청)
    // ====================================================

    // @Valid 유효성 검사 실패 시
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "잘못된 입력값입니다."),

    // 파라미터 타입 불일치 (예: Long에 String 입력)
    TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "입력된 타입이 올바르지 않습니다."),

    // 필수 파라미터 누락
    MISSING_PARAMETER(HttpStatus.BAD_REQUEST, "필수 파라미터가 누락되었습니다."),

    // 파일 업로드 관련 (용량 초과, 형식 오류 등)
    INVALID_FILE(HttpStatus.BAD_REQUEST, "유효하지 않은 파일입니다."),

    // ====================================================
    // 401 Unauthorized (인증 실패 - 누구세요?)
    // ====================================================

    // 토큰이 없거나, 유효하지 않거나, 만료된 경우
    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다."),

    // 로그인 실패 (아이디/비번 불일치)
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "로그인에 실패했습니다."),

    // ====================================================
    // 403 Forbidden (권한 없음 - 들어오지 마세요)
    // ====================================================

    // 인증은 되었으나 해당 리소스에 접근 권한이 없는 경우 (예: 멘티가 멘토 기능 사용 시도)
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

    // 상담방 참여자가 아닌 경우
    CONSULTATION_ROOM_ACCESS_DENIED(HttpStatus.FORBIDDEN, "해당 상담방의 참여자가 아닙니다."),

    // ====================================================
    // 404 Not Found (찾을 수 없음)
    // ====================================================

    // DB에 데이터가 없는 경우
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "대상을 찾을 수 없습니다."),

    // 존재하지 않는 URL 호출 시
    API_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 API 경로입니다."),

    // ====================================================
    // 405 Method Not Allowed (메서드 불일치)
    // ====================================================

    // POST 요청을 보내야 하는데 GET을 보낸 경우 등
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 HTTP 메서드입니다."),

    // ====================================================
    // 409 Conflict (충돌 / 로직 위반)
    // ====================================================

    // 이미 존재하는 데이터 (회원가입 중복 이메일 등)
    ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 데이터입니다."),

    // 비즈니스 로직 위반 (상담 신청 횟수 초과, 이미 마감된 상담 등)
    // *가장 자주 커스텀 메시지를 덮어씌워서 사용하게 될 코드*
    RESOURCE_CONFLICT(HttpStatus.CONFLICT, "요청을 처리할 수 없는 상태입니다."),

    // ====================================================
    // 500 Internal Server Error (서버 터짐)
    // ====================================================

    // 개발자가 예상치 못한 모든 에러의 기본값
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
