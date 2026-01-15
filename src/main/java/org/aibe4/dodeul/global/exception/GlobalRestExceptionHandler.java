package org.aibe4.dodeul.global.exception;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aibe4.dodeul.global.response.CommonResponse;
import org.aibe4.dodeul.global.response.enums.ErrorCode;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.NoSuchElementException;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(annotations = RestController.class)
public class GlobalRestExceptionHandler {

    // ====================================================
    // Business Logic Exceptions (개발자가 직접 발생시키는 예외)
    // ====================================================

    /*
     * [TODO] CustomException 핸들러 구현 위치
     * 비즈니스 로직 에러(CustomException) 처리가 필요할 때 이곳에 작성
     * 작성 시 @ExceptionHandler(CustomException.class) 어노테이션을 사용
     */

    /**
     * 비즈니스 로직 예외 처리 (CustomException)
     * Service 계층에서 명시적으로 발생시킨 비즈니스 예외를 처리
     * ErrorCode에 정의된 HTTP 상태 코드와 메시지를 그대로 응답에 반영
     * CommonResponse 형식으로 통일된 에러 응답을 반환
     */
    @ExceptionHandler(BusinessException.class)
    public CommonResponse<Void> handleBusinessException(
        BusinessException e,
        HttpServletResponse response
    ) {
        ErrorCode errorCode = e.getErrorCode();
        response.setStatus(errorCode.getHttpStatus().value());
        return CommonResponse.fail(errorCode, e.getDetailMessage());
    }

    /**
     * 잘못된 형식의 인자
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public CommonResponse<Void> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("IllegalArgument: {}", e.getMessage());
        return CommonResponse.fail(
            ErrorCode.INVALID_INPUT_VALUE,
            getMessageOrDefault(e, ErrorCode.INVALID_INPUT_VALUE));
    }

    /**
     * 데이터 조회 실패
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoSuchElementException.class)
    public CommonResponse<Void> handleNoSuchElement(NoSuchElementException e) {
        log.warn("NoSuchElement: {}", e.getMessage());
        return CommonResponse.fail(
            ErrorCode.RESOURCE_NOT_FOUND, getMessageOrDefault(e, ErrorCode.RESOURCE_NOT_FOUND));
    }

    /**
     * 비즈니스 로직 위반
     */
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(IllegalStateException.class)
    public CommonResponse<Void> handleIllegalState(IllegalStateException e) {
        log.warn("IllegalState: {}", e.getMessage());
        return CommonResponse.fail(
            ErrorCode.RESOURCE_CONFLICT, getMessageOrDefault(e, ErrorCode.RESOURCE_CONFLICT));
    }

    /**
     * 파일 용량 초과
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public CommonResponse<Void> handleFileSizeLimitExceeded(MaxUploadSizeExceededException e) {
        log.warn("File Size Limit Exceeded: {}", e.getMessage());
        return CommonResponse.fail(ErrorCode.INVALID_FILE, "파일 크기가 제한을 초과했습니다.");
    }

    // ====================================================
    // Validation Exceptions (Spring이 @Valid 검사 중 발생시키는 예외)
    // ====================================================

    /**
     * 유효성 검사 실패 {@code @RequestBody} {@code @Valid}
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonResponse<Void> handleValidationException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.warn("Validation Failed: {}", errorMessage);
        return CommonResponse.fail(ErrorCode.INVALID_INPUT_VALUE, errorMessage);
    }

    /**
     * 유효성 검사 실패 {@code @ModelAttribute}
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public CommonResponse<Void> handleBindException(BindException e) {
        String errorMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.warn("Bind Failed: {}", errorMessage);
        return CommonResponse.fail(ErrorCode.INVALID_INPUT_VALUE, errorMessage);
    }

    // ====================================================
    // Request / Parameter Exceptions (클라이언트 요청 오류)
    // ====================================================

    /**
     * 파라미터 타입 불일치
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public CommonResponse<Void> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        String errorMessage = String.format("파라미터의 타입이 올바르지 않습니다. (%s)", e.getName());
        log.warn("Type Mismatch: {}", errorMessage);
        return CommonResponse.fail(ErrorCode.TYPE_MISMATCH, errorMessage);
    }

    /**
     * 필수 파라미터 누락 {@code @RequestParam}
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public CommonResponse<Void> handleMissingParameter(MissingServletRequestParameterException e) {
        String errorMessage = String.format("필수 파라미터가 누락되었습니다. (%s)", e.getParameterName());
        log.warn("Missing Parameter: {}", errorMessage);
        return CommonResponse.fail(ErrorCode.MISSING_PARAMETER, errorMessage);
    }

    /**
     * 지원하지 않는 HTTP 메서드 호출
     */
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public CommonResponse<Void> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        String errorMessage = "지원하지 않는 HTTP 메서드입니다. (" + e.getMethod() + ")";
        log.warn("Method Not Supported: {}", errorMessage);
        return CommonResponse.fail(ErrorCode.METHOD_NOT_ALLOWED, errorMessage);
    }

    /**
     * JSON 파싱 오류 또는 Body 누락
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public CommonResponse<Void> handleJsonError(HttpMessageNotReadableException e) {
        log.warn("JSON Parse Error: {}", e.getMessage());
        return CommonResponse.fail(ErrorCode.INVALID_INPUT_VALUE, "JSON 형식이 올바르지 않거나 데이터가 비어있습니다.");
    }

    /**
     * 잘못된 API 경로 호출 (Spring Boot 3.2+)
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoResourceFoundException.class)
    public CommonResponse<Void> handle404(NoResourceFoundException e) {
        log.warn("API Not Found: {}", e.getResourcePath());
        return CommonResponse.fail(ErrorCode.API_NOT_FOUND);
    }

    // ====================================================
    // Security & System Exceptions (보안 및 서버 오류)
    // ====================================================

    /**
     * 권한 없음
     */
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public CommonResponse<Void> handleAccessDenied(AccessDeniedException e) {
        log.warn("Access Denied: {}", e.getMessage());
        return CommonResponse.fail(ErrorCode.ACCESS_DENIED);
    }

    /**
     * 예상치 못한 모든 에러
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public CommonResponse<Void> handleException(Exception e) {
        log.error("Internal Server Error", e);
        return CommonResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    // ====================================================
    // Helper Method
    // ====================================================

    /**
     * 예외에 메시지가 있으면 반환하고, 없으면 ErrorCode의 기본 메시지를 반환합니다.
     */
    private String getMessageOrDefault(Exception e, ErrorCode errorCode) {
        if (e.getMessage() != null && !e.getMessage().isBlank()) {
            return e.getMessage();
        }
        return errorCode.getMessage();
    }
}
