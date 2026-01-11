package org.aibe4.dodeul.domain.board.exception;

import lombok.Getter;
import org.aibe4.dodeul.global.response.enums.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
public class BoardPolicyException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final ErrorCode errorCode;

    public BoardPolicyException(HttpStatus httpStatus, ErrorCode errorCode, String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }
}
