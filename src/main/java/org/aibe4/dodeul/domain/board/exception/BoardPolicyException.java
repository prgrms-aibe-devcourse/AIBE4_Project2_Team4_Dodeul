// src/main/java/org/aibe4/dodeul/domain/board/exception/BoardPolicyException.java
package org.aibe4.dodeul.domain.board.exception;

import lombok.Getter;
import org.aibe4.dodeul.global.exception.BusinessException;
import org.aibe4.dodeul.global.response.enums.ErrorCode;
import org.springframework.http.HttpStatus;

/**
 * Board 도메인 정책/인가 관련 예외
 *
 * <p>GlobalRestExceptionHandler의 BusinessException 처리로 공통 응답 포맷/상태코드 통일</p>
 */
@Getter
public class BoardPolicyException extends BusinessException {

    // 기존 코드 호환용(호출부에서 HttpStatus를 넘겨도 컴파일 깨지지 않게 유지)
    private final HttpStatus httpStatus;

    public BoardPolicyException(HttpStatus httpStatus, ErrorCode errorCode, String message) {
        super(errorCode, message);
        this.httpStatus = httpStatus;
    }

    public BoardPolicyException(ErrorCode errorCode, String message) {
        super(errorCode, message);
        this.httpStatus = errorCode.getHttpStatus();
    }

    public BoardPolicyException(ErrorCode errorCode) {
        super(errorCode);
        this.httpStatus = errorCode.getHttpStatus();
    }
}
