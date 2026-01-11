package org.aibe4.dodeul.domain.board.exception;

import lombok.extern.slf4j.Slf4j;
import org.aibe4.dodeul.global.response.CommonResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = "org.aibe4.dodeul.domain.board")
public class BoardApiExceptionHandler {

    @ExceptionHandler(BoardPolicyException.class)
    public ResponseEntity<CommonResponse<Void>> handleBoardPolicyException(BoardPolicyException e) {
        log.warn("BoardPolicyException: {}", e.getMessage());
        return ResponseEntity.status(e.getHttpStatus())
            .body(CommonResponse.fail(e.getErrorCode(), e.getMessage()));
    }
}
