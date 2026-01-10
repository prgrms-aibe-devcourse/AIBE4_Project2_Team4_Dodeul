package org.aibe4.dodeul.global.exception;

import lombok.Getter;
import org.aibe4.dodeul.global.response.enums.ErrorCode;

/**
 * 비즈니스 로직 처리 중 발생하는 예외를 표현하는 공통 예외 클래스
 * (인증, 인가, 도메인 규칙 위반 등)
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String detailMessage;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.detailMessage = errorCode.getMessage();
    }

    public BusinessException(ErrorCode errorCode, String detailMessage) {
        super(detailMessage);
        this.errorCode = errorCode;
        this.detailMessage = detailMessage;
    }
}
