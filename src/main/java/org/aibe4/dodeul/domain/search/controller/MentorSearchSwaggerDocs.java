package org.aibe4.dodeul.domain.search.controller;

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

public interface MentorSearchSwaggerDocs {

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "멘토 검색 성공",
            content = @Content(
                schema = @Schema(implementation = CommonResponse.class),
                examples = @ExampleObject(value = """
                    {
                        "code": 200,
                        "message": "성공",
                        "data": {
                            "content": [
                                {
                                    "memberId": 1,
                                    "nickname": "코딩하는라이언",
                                    "profileUrl": "https://example.com/profile.jpg",
                                    "job": "Backend Developer",
                                    "careerYears": 5,
                                    "skillTags": ["Java", "Spring Boot"],
                                    "consultingTags": ["CAREER", "CODEREVIEW"],
                                    "recommendCount": 10,
                                    "completedMatchingCount": 15,
                                    "responseRate": 95.5,
                                    "status": "AVAILABLE"
                                }
                            ],
                            "pageable": {
                                "pageNumber": 0,
                                "pageSize": 10,
                                "sort": { "empty": true, "sorted": false, "unsorted": true },
                                "offset": 0,
                                "paged": true,
                                "unpaged": false
                            },
                            "last": true,
                            "totalElements": 1,
                            "totalPages": 1,
                            "size": 10,
                            "number": 0,
                            "sort": { "empty": true, "sorted": false, "unsorted": true },
                            "first": true,
                            "numberOfElements": 1,
                            "empty": false
                        }
                    }
                    """)
            ))
    })
    @interface SearchMentors {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "인기 멘토 조회 성공",
            content = @Content(
                schema = @Schema(implementation = CommonResponse.class),
                examples = @ExampleObject(value = """
                    {
                        "code": 200,
                        "message": "성공",
                        "data": [
                            {
                                "memberId": 10,
                                "nickname": "스타멘토",
                                "profileUrl": "https://example.com/profile1.jpg",
                                "job": "BACKEND",
                                "careerYears": 7,
                                "intro": "스타멘토입니다."
                                "skillTags": ["Spring Boot", "JPA"],
                                "consultingTags": ["CAREER", "CODEREVIEW"],
                                "recommendCount": 120,
                                "completedMatchingCount": 150,
                                "responseRate": 99.0,
                                "status": "AVAILABLE"
                            },
                            {
                                "memberId": 5,
                                "nickname": "AI전문가",
                                "profileUrl": null,
                                "job": "AI_ENGINEER",
                                "careerYears": 5,
                                "intro": "AI 박사입니다."
                                "skillTags": ["Python", "TensorFlow"],
                                "consultingTags": ["CAREER"],
                                "recommendCount": 98,
                                "completedMatchingCount": 100,
                                "responseRate": 95.5,
                                "status": "FULL"
                            }
                        ]
                    }
                    """)
            )),
        @ApiResponse(responseCode = "500", description = "서버 오류",
            content = @Content(
                schema = @Schema(implementation = CommonResponse.class),
                examples = @ExampleObject(
                    name = "ServerError",
                    summary = "서버 내부 오류",
                    value = "{\"code\": 500, \"message\": \"서버 내부 오류가 발생했습니다.\", \"data\": null}"
                )
            ))
    })
    @interface PopularMentors {
    }
}
