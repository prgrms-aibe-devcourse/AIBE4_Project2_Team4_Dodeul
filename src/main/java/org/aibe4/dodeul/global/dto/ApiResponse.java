package org.aibe4.dodeul.global.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.aibe4.dodeul.global.dto.enums.ErrorCode;
import org.aibe4.dodeul.global.dto.enums.SuccessCode;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"code", "message", "data"})
public class ApiResponse<T> {

    private int code;
    private String message;
    private T data;

    /**
     * [성공 응답 1] - Enum에 정의된 기본 메시지 사용
     *
     * @param successCode 성공 코드 Enum
     * @param data        반환할 데이터 (없으면 null)
     */
    public static <T> ApiResponse<T> success(SuccessCode successCode, T data) {
        return new ApiResponse<>(
            successCode.getHttpStatus().value(), successCode.getMessage(), data);
    }

    /**
     * [성공 응답 2] - 상세 메시지 커스텀 (Overloading)
     * 예: "사용 가능한 닉네임입니다.", "상담 신청이 완료되었습니다." 등
     * 성공 상황에 대해 더 구체적인 안내가 필요할 때 사용
     *
     * @param successCode 성공 코드 Enum
     * @param data        반환할 데이터
     * @param message     덮어씌울 상세 메시지
     */
    public static <T> ApiResponse<T> success(SuccessCode successCode, T data, String message) {
        return new ApiResponse<>(
            successCode.getHttpStatus().value(),
            message,
            data
        );
    }

    /**
     * [실패 응답 1] - Enum에 정의된 기본 메시지 사용
     * 예: "잘못된 입력값입니다." 등 고정된 메시지가 필요할 때
     */
    public static <T> ApiResponse<T> fail(ErrorCode errorCode) {
        return new ApiResponse<>(
            errorCode.getHttpStatus().value(),
            errorCode.getMessage(),
            null
        );
    }

    /**
     * [실패 응답 2] - 상세 메시지 커스텀 (Overloading)
     * 예: "이미 신청한 상담입니다.", "멘토 모집이 마감되었습니다." 등
     * 같은 에러 코드라도 상황에 따라 메시지를 다르게 보내야 할 때 사용
     *
     * @param errorCode 에러 코드 Enum (HTTP Status 등 공통 속성 사용)
     * @param message   덮어씌울 상세 메시지
     */
    public static <T> ApiResponse<T> fail(ErrorCode errorCode, String message) {
        return new ApiResponse<>(
            errorCode.getHttpStatus().value(),
            message,
            null
        );
    }
}
