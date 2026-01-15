# 📘 MentorLink API 문서화(Swagger) 가이드

---

## 1. 어노테이션 규칙

|     적용 위치      |    어노테이션     | 설명                               | 필수 여부 |
|:--------------:|:------------:|:---------------------------------|:-----:|
| **Controller** |    `@Tag`    | API 그룹 이름 (예: `Board`, `Member`) | ✅ 필수  |
|   **Method**   | `@Operation` | 기능 설명 (예: `게시글 상세 조회`, `게시글 작성`) | ✅ 필수  |
| **DTO Field**  |  `@Schema`   | 필드 설명 및 **예시 값(example)**        | ✅ 필수  |

---

## 2. 코드 작성 예시 (복사해서 수정 후 사용하세요)

아래 코드는 예시입니다. 필요한 부분만 복사해서 본인의 도메인(회원, 상담 등)에 맞게 수정해서 사용하세요.

```java
// =================================================================
// ✅ 1. DTO 작성 예시
// =================================================================
@Getter
@Schema(description = "게시글 작성 요청 DTO")
public class PostCreateRequest {

    @Schema(description = "게시글 제목", example = "스프링 부트 질문있습니다.")
    private String title;

    @Schema(description = "게시글 내용", example = "JPA 매핑이 너무 어려워요...")
    private String content;
}

// =================================================================
// ✅ 2. API Controller 작성 예시
// 설명: 화면 이동 없이 데이터(JSON)만 주고받을 때 사용
// =================================================================
@Tag(name = "Board", description = "게시판 API")
@RestController // [중요] 데이터(JSON)만 반환할 때 사용
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class BoardApiController {

    private final BoardService boardService;

    @Operation(summary = "게시글 작성", description = "게시글을 등록하고, 등록된 ID를 JSON으로 반환합니다.")
    @PostMapping
    public CommonResponse<Long> writePost(@RequestBody PostCreateRequest request) {

        Long savedId = boardService.write(request);

        // 팀 규칙(CommonResponse) 사용
        return CommonResponse.success(SuccessCode.CREATE_SUCCESS, savedId, "게시글 생성 성공");
    }
}
