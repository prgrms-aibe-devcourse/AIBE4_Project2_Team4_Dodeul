package org.aibe4.dodeul.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.aibe4.dodeul.global.response.enums.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice(annotations = Controller.class)
public class GlobalViewExceptionHandler {
    /**
     * 나머지 모든 시스템 에러. 500 에러 발생 시 처리
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {
        log.error("View System Error", e);

        model.addAttribute("errorMessage", ErrorCode.INTERNAL_SERVER_ERROR.getMessage());

        return "error/500";
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler({AuthorizationDeniedException.class, AccessDeniedException.class})
    public String handleAccessDenied(Exception e, Model model) {
        model.addAttribute("errorMessage", e.getMessage());
        model.addAttribute("nextUrl", "/home");

        return "error/access-denied";
    }
}
