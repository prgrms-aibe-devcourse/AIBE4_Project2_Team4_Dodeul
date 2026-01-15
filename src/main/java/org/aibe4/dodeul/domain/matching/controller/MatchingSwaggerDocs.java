package org.aibe4.dodeul.domain.matching.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.aibe4.dodeul.global.response.CommonResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface MatchingSwaggerDocs {
    @Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses({
        @ApiResponse(responseCode = "400", description = "잘못된 요청",
            content = @Content(
                schema = @Schema(implementation = CommonResponse.class),
                examples = @ExampleObject(
                    name = "NotOwner",
                    summary = "본인 매칭이 아닐 때",
                    value = "{\"code\": 400, \"message\": \"본인의 매칭만 요청할 수 있습니다.\", \"data\": null}"
                )
            )),
        @ApiResponse(responseCode = "404", description = "매칭 정보 없음",
            content = @Content(
                schema = @Schema(implementation = CommonResponse.class),
                examples = @ExampleObject(
                    name = "NoMatching",
                    summary = "매칭 정보 없음",
                    value = "{\"code\": 404, \"message\": \"해당 매칭 정보를 찾을 수 없습니다.\", \"data\": null}"
                )
            ))
    })
    @interface CommonErrors {
    }

    @Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponse(responseCode = "409", description = "매칭 불가",
        content = @Content(
            schema = @Schema(implementation = CommonResponse.class),
            examples = {
                @ExampleObject(name = "MenteeLimitExceeded", summary = "멘티 한도 초과",
                    value = "{\"code\": 409, \"message\": \"동시에 진행 가능한 상담 수는 최대 3개입니다. 기존의 상담을 먼저 끝내주세요.\", \"data\": null}"),
                @ExampleObject(name = "MentorDisabled", summary = "멘토 상담 불가",
                    value = "{\"code\": 409, \"message\": \"멘토가 상담을 비활성화하였습니다.\", \"data\": null}"),
                @ExampleObject(name = "MentorLimitExceeded", summary = "멘토 한도 초과",
                    value = "{\"code\": 409, \"message\": \"해당 멘토의 상담이 마감되었습니다. 다른 멘토를 선택해주세요.\", \"data\": null}")
            }
        ))
    @interface MatchingError {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @MatchingError
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "매칭 가능",
            content = @Content(
                schema = @Schema(implementation = CommonResponse.class),
                examples = @ExampleObject(
                    value = "{\"code\": 200, \"message\": \"매칭이 가능합니다.\", \"data\": null}"
                )
            )),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 멘토",
            content = @Content(
                schema = @Schema(implementation = CommonResponse.class),
                examples = @ExampleObject(
                    name = "MentorNotFound",
                    summary = "존재하지 않는 멘토",
                    value = "{\"code\": 404, \"message\": \"존재하지 않는 멘토입니다.\", \"data\": null}"
                )
            ))
    })
    @interface CheckAvailability {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @MatchingError
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "매칭 신청 성공",
            content = @Content(
                schema = @Schema(implementation = CommonResponse.class),
                examples = @ExampleObject(value = """
                    {
                        "code": 200,
                        "message": "매칭 신청을 성공했습니다.",
                        "data": {
                            "matchingId": 1,
                            "status": "WAITING"
                        }
                    }
                    """)
            )),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (검증 실패)",
            content = @Content(
                schema = @Schema(implementation = CommonResponse.class),
                examples = {
                    @ExampleObject(name = "NotOwner", summary = "본인 신청서 아님",
                        value = "{\"code\": 400, \"message\": \"본인의 신청서로만 매칭을 신청할 수 있습니다.\", \"data\": null}"),
                    @ExampleObject(name = "InvalidRole", summary = "역할(멘토/멘티) 불일치",
                        value = "{\"code\": 400, \"message\": \"멘토와 멘티의 역할이 올바르지 않습니다.\", \"data\": null}")
                }
            )),
        @ApiResponse(responseCode = "401", description = "인증 실패",
            content = @Content(
                schema = @Schema(implementation = CommonResponse.class),
                examples = @ExampleObject(
                    name = "Unauthorized",
                    summary = "인증 정보 무효",
                    value = "{\"code\": 401, \"message\": \"인증 정보가 유효하지 않습니다.\", \"data\": null}"
                )
            )),
        @ApiResponse(responseCode = "404", description = "리소스 찾을 수 없음",
            content = @Content(
                schema = @Schema(implementation = CommonResponse.class),
                examples = {
                    @ExampleObject(name = "ApplicationNotFound", summary = "신청서 없음",
                        value = "{\"code\": 404, \"message\": \"해당 신청서를 찾을 수 없습니다: 1\", \"data\": null}"),
                    @ExampleObject(name = "MentorNotFound", summary = "멘토 없음",
                        value = "{\"code\": 404, \"message\": \"존재하지 않는 멘토입니다.\", \"data\": null}")
                }
            ))
    })
    @interface CreateMatching {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @CommonErrors
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "매칭 수락 성공",
            content = @Content(
                schema = @Schema(implementation = CommonResponse.class),
                examples = @ExampleObject(value = """
                    {
                        "code": 200,
                        "message": "매칭 수락을 성공했습니다.",
                        "data": {
                            "matchingId": 1,
                            "status": "MATCHED"
                        }
                    }
                    """)
            )),
        @ApiResponse(responseCode = "409", description = "매칭 상태 충돌",
            content = @Content(
                schema = @Schema(implementation = CommonResponse.class),
                examples = @ExampleObject(
                    name = "NotWaiting",
                    summary = "매칭 대기 상태가 아닐 때",
                    value = "{\"code\": 409, \"message\": \"매칭 대기 상태에서만 수락할 수 있습니다.\", \"data\": null}"
                )
            ))
    })
    @interface AcceptError {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @CommonErrors
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "매칭 거절 성공",
            content = @Content(
                schema = @Schema(implementation = CommonResponse.class),
                examples = @ExampleObject(value = """
                    {
                        "code": 200,
                        "message": "매칭 거절을 성공했습니다.",
                        "data": {
                            "matchingId": 1,
                            "status": "REJECTED"
                        }
                    }
                    """)
            )),
        @ApiResponse(responseCode = "409", description = "매칭 상태 충돌",
            content = @Content(
                schema = @Schema(implementation = CommonResponse.class),
                examples = @ExampleObject(
                    name = "NotWaiting",
                    summary = "매칭 대기 상태가 아닐 때",
                    value = "{\"code\": 409, \"message\": \"매칭 대기 상태에서만 거절할 수 있습니다.\", \"data\": null}"
                )
            ))
    })
    @interface RejectError {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @CommonErrors
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "매칭 취소 성공",
            content = @Content(
                schema = @Schema(implementation = CommonResponse.class),
                examples = @ExampleObject(value = """
                    {
                        "code": 200,
                        "message": "매칭 취소를 성공했습니다.",
                        "data": {
                            "matchingId": 1,
                            "status": "CANCELED"
                        }
                    }
                    """)
            )),
        @ApiResponse(responseCode = "409", description = "매칭 상태 충돌",
            content = @Content(
                schema = @Schema(implementation = CommonResponse.class),
                examples = @ExampleObject(
                    name = "NotWaiting",
                    summary = "매칭 대기 상태가 아닐 때",
                    value = "{\"code\": 409, \"message\": \"이미 진행 중이거나 종료된 매칭은 취소할 수 없습니다.\", \"data\": null}"
                )
            ))
    })
    @interface CancelError {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @CommonErrors
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "상담 종료 성공",
            content = @Content(
                schema = @Schema(implementation = CommonResponse.class),
                examples = @ExampleObject(value = """
                    {
                        "code": 200,
                        "message": "상담 종료를 성공했습니다.",
                        "data": {
                            "matchingId": 1,
                            "status": "INREVIEW"
                        }
                    }
                    """)
            )),
        @ApiResponse(responseCode = "409", description = "매칭 상태 충돌",
            content = @Content(
                schema = @Schema(implementation = CommonResponse.class),
                examples = @ExampleObject(
                    name = "NotMatched",
                    summary = "진행 중인 상담이 아닐 때",
                    value = "{\"code\": 409, \"message\": \"진행 중인 상담만 종료할 수 있습니다.\", \"data\": null}"
                )
            ))
    })
    @interface FinishError {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @CommonErrors
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "매칭 최종 완료 성공",
            content = @Content(
                schema = @Schema(implementation = CommonResponse.class),
                examples = @ExampleObject(value = """
                    {
                        "code": 200,
                        "message": "매칭 최종 완료를 성공했습니다.",
                        "data": {
                            "matchingId": 1,
                            "status": "COMPLETED"
                        }
                    }
                    """)
            )),
        @ApiResponse(responseCode = "409", description = "매칭 상태 충돌",
            content = @Content(
                schema = @Schema(implementation = CommonResponse.class),
                examples = @ExampleObject(
                    name = "NotInReview",
                    summary = "리뷰 작성 대기 상태가 아닐 때",
                    value = "{\"code\": 409, \"message\": \"리뷰 작성 대기 상태가 아닙니다.\", \"data\": null}"
                )
            ))
    })
    @interface CompleteError {
    }
}
