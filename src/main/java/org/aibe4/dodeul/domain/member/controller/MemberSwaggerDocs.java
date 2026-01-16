package org.aibe4.dodeul.domain.member.controller;

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

public interface MemberSwaggerDocs {

    // ---------- 공통 에러(인증/권한) ----------

    @Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses({
        @ApiResponse(responseCode = "401", description = "인증 실패",
            content = @Content(
                schema = @Schema(implementation = CommonResponse.class),
                examples = @ExampleObject(
                    name = "Unauthorized",
                    summary = "인증 정보 무효",
                    value = "{\"code\":401,\"message\":\"인증 정보가 유효하지 않습니다.\",\"data\":null}"
                )
            )),
        @ApiResponse(responseCode = "403", description = "권한 없음",
            content = @Content(
                schema = @Schema(implementation = CommonResponse.class),
                examples = @ExampleObject(
                    name = "Forbidden",
                    summary = "접근 권한 없음",
                    value = "{\"code\":403,\"message\":\"접근 권한이 없습니다.\",\"data\":null}"
                )
            ))
    })
    @interface AuthErrors {
    }

    // ---------- 회원가입 ----------

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "회원가입 성공",
            content = @Content(
                schema = @Schema(implementation = CommonResponse.class),
                examples = @ExampleObject(
                    value = "{\"code\":200,\"message\":\"SIGNUP_SUCCESS\",\"data\":1}"
                )
            )),
        @ApiResponse(responseCode = "400", description = "잘못된 요청(검증 실패)",
            content = @Content(
                schema = @Schema(implementation = CommonResponse.class),
                examples = {
                    @ExampleObject(name = "PasswordMismatch", summary = "비밀번호 불일치",
                        value = "{\"code\":400,\"message\":\"비밀번호가 일치하지 않습니다.\",\"data\":null}"),
                    @ExampleObject(name = "RoleRequired", summary = "역할 누락",
                        value = "{\"code\":400,\"message\":\"역할 선택이 필요합니다.\",\"data\":null}")
                }
            )),
        @ApiResponse(responseCode = "409", description = "중복(이미 존재)",
            content = @Content(
                schema = @Schema(implementation = CommonResponse.class),
                examples = @ExampleObject(
                    name = "EmailExists",
                    summary = "이메일 중복",
                    value = "{\"code\":409,\"message\":\"이미 가입된 이메일입니다.\",\"data\":null}"
                )
            ))
    })
    @interface Register {
    }

    // ---------- 닉네임 설정 ----------

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @AuthErrors
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "닉네임 변경 성공",
            content = @Content(
                schema = @Schema(implementation = CommonResponse.class),
                examples = @ExampleObject(
                    value = "{\"code\":200,\"message\":\"UPDATE_SUCCESS\",\"data\":null}"
                )
            )),
        @ApiResponse(responseCode = "400", description = "입력값 오류",
            content = @Content(
                schema = @Schema(implementation = CommonResponse.class),
                examples = {
                    @ExampleObject(name = "Length", summary = "길이 제한",
                        value = "{\"code\":400,\"message\":\"닉네임은 2~10자여야 합니다.\",\"data\":null}"),
                    @ExampleObject(name = "Pattern", summary = "형식 제한",
                        value = "{\"code\":400,\"message\":\"닉네임은 한글/영문/숫자만 가능합니다.\",\"data\":null}")
                }
            )),
        @ApiResponse(responseCode = "409", description = "중복 닉네임",
            content = @Content(
                schema = @Schema(implementation = CommonResponse.class),
                examples = @ExampleObject(
                    name = "NicknameExists",
                    summary = "닉네임 중복",
                    value = "{\"code\":409,\"message\":\"이미 사용 중인 닉네임입니다.\",\"data\":null}"
                )
            ))
    })
    @interface UpdateNickname {
    }

    // ---------- 대시보드 ----------

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @AuthErrors
    @ApiResponse(responseCode = "200", description = "대시보드 조회 성공",
        content = @Content(
            schema = @Schema(implementation = CommonResponse.class),
            examples = @ExampleObject(
                value = "{\"code\":200,\"message\":\"대시보드 조회 성공\",\"data\":{}}"
            )
        ))
    @interface Dashboard {
    }

    // ---------- 프로필 조회/수정 ----------

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @AuthErrors
    @ApiResponse(responseCode = "200", description = "프로필 조회 성공",
        content = @Content(
            schema = @Schema(implementation = CommonResponse.class),
            examples = @ExampleObject(
                value = "{\"code\":200,\"message\":\"멘토 프로필 조회 성공\",\"data\":{}}"
            )
        ))
    @interface GetProfile {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @AuthErrors
    @ApiResponse(responseCode = "200", description = "프로필 수정 성공",
        content = @Content(
            schema = @Schema(implementation = CommonResponse.class),
            examples = @ExampleObject(
                value = "{\"code\":200,\"message\":\"멘토 프로필 수정 성공\",\"data\":null}"
            )
        ))
    @interface UpdateProfile {
    }

    // ---------- 상담 가능 토글 ----------

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @AuthErrors
    @ApiResponse(responseCode = "200", description = "상담 상태 변경 성공",
        content = @Content(
            schema = @Schema(implementation = CommonResponse.class),
            examples = @ExampleObject(
                value = "{\"code\":200,\"message\":\"상담 상태 변경 성공\",\"data\":null}"
            )
        ))
    @interface ToggleConsultationEnabled {
    }

    // ---------- 프로필 이미지 업로드 ----------

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @AuthErrors
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "프로필 이미지 업로드 성공",
            content = @Content(
                schema = @Schema(implementation = CommonResponse.class),
                examples = @ExampleObject(
                    value = "{\"code\":200,\"message\":\"멘토 프로필 이미지 업로드 성공\",\"data\":\"https://...\"}"
                )
            )),
        @ApiResponse(responseCode = "400", description = "업로드 실패/검증 실패",
            content = @Content(
                schema = @Schema(implementation = CommonResponse.class),
                examples = @ExampleObject(
                    value = "{\"code\":400,\"message\":\"이미지 업로드 실패\",\"data\":null}"
                )
            ))
    })
    @interface UploadProfileImage {
    }

    // ---------- 공개 프로필 ----------

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "멘토 공개 프로필 조회 성공",
            content = @Content(
                schema = @Schema(implementation = CommonResponse.class),
                examples = @ExampleObject(
                    value = "{\"code\":200,\"message\":\"멘토 공개 프로필 조회 성공\",\"data\":{}}"
                )
            )),
        @ApiResponse(responseCode = "404", description = "멘토 없음",
            content = @Content(
                schema = @Schema(implementation = CommonResponse.class),
                examples = @ExampleObject(
                    value = "{\"code\":404,\"message\":\"멘토를 찾을 수 없습니다.\",\"data\":null}"
                )
            ))
    })
    @interface PublicMentorProfile {
    }

    // ---------- role 온보딩(세션 저장) ----------

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponse(responseCode = "200", description = "역할 선택 성공(세션 저장)",
        content = @Content(
            schema = @Schema(implementation = CommonResponse.class),
            examples = @ExampleObject(
                value = "{\"code\":200,\"message\":\"SUCCESS\",\"data\":null}"
            )
        ))
    @interface SelectRole {
    }
}
