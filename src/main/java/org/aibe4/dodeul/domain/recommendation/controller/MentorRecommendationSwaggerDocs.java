package org.aibe4.dodeul.domain.recommendation.controller;

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

public interface MentorRecommendationSwaggerDocs {

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "멘토 추천 성공",
            content = @Content(
                schema = @Schema(implementation = CommonResponse.class),
                examples = @ExampleObject("""
                    {
                        "code": 200,
                        "message": "멘토 추천을 성공했습니다.",
                        "data": [
                            {
                                "mentorId": 10,
                                "nickname": "AI전문가",
                                "profileUrl": "https://example.com/ai.jpg",
                                "job": "AI_ENGINEER",
                                "careerYears": 7,
                                "skillTags": ["Python", "TensorFlow"],
                                "recommendedReviewCount": 42,
                                "completedMatchingCount": 50,
                                "responseRate": 100.0,
                                "matchScore": 98.5
                            },
                            {
                                "mentorId": 25,
                                "nickname": "데이터과학자",
                                "profileUrl": null,
                                "job": "DATA_SCIENTIST",
                                "careerYears": 4,
                                "skillTags": ["Python", "Pandas"],
                                "recommendedReviewCount": 10,
                                "completedMatchingCount": 12,
                                "responseRate": 95.0,
                                "matchScore": 85.0
                            }
                        ]
                    }
                    """)
            )),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (검증/상태 오류)",
            content = @Content(
                schema = @Schema(implementation = CommonResponse.class),
                examples = {
                    @ExampleObject(name = "NotOwner", summary = "본인 신청서 아님",
                        value = "{\"code\": 400, \"message\": \"본인의 신청서로만 매칭을 신청할 수 있습니다.\", \"data\": null}"),
                    @ExampleObject(name = "NotEnoughMentors", summary = "추천 데이터 부족",
                        value = "{\"code\": 400, \"message\": \"등록된 멘토 수가 적어 추천할 수 없습니다.\", \"data\": null}")
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
                        value = "{\"code\": 404, \"message\": \"해당 신청서를 찾을 수 없습니다: 1\", \"data\": null}")
                }
            ))
    })
    @interface RecommendMentors {
    }
}
