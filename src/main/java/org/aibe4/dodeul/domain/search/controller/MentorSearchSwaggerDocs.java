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
}
