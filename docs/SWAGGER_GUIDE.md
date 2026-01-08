# 📘 MentorLink API 문서화(Swagger) 가이드

팀원 여러분, 우리 팀의 **하이브리드 전략(Pattern A + B)** 및 **팀 노션 가이드**에 맞춰 작성법을 정리했습니다.
본인이 맡은 기능이 **"화면을 보여주는 것(SSR)"**인지, **"데이터만 주는 것(API)"**인지에 따라 골라서 참고하세요.

---

## 1. 어노테이션 규칙 (노션 가이드 기준)

|     적용 위치      |      어노테이션      | 설명                               | 필수 여부 |
|:--------------:|:---------------:|:---------------------------------|:-----:|
| **Controller** |     `@Tag`      | API 그룹 이름 (예: `Board`, `Member`) | ✅ 필수  |
|   **Method**   |  `@Operation`   | 기능 설명 (예: `게시글 상세 조회`)           | ✅ 필수  |
|   **Method**   | `@ApiResponses` | 응답 코드별 설명 (200, 400, 404 등)      | ⚠️ 권장 |
| **Parameter**  |  `@Parameter`   | 파라미터 상세 설명 (검색어, 페이징 등)          |  선택   |
| **DTO Field**  |    `@Schema`    | 필드 설명 및 **예시 값(example)**        | ✅ 필수  |

---

## 2. 코드 작성 예시 (복사해서 수정 후 사용하세요)

아래 예시는 **게시판(Board)** 기능을 기준으로 작성되었습니다. 본인의 도메인에 맞게 변경해서 사용하세요.

```java
// ==========================================
// ✅ [공통] 1. DTO 작성 예시
// ==========================================
@Getter
@Schema(description = "게시글 작성 요청 DTO")
public class PostCreateRequest {

    @Schema(description = "게시글 제목", example = "스프링 부트 질문있습니다.")
    private String title;

    @Schema(description = "게시글 내용", example = "JPA 매핑이 너무 어려워요...")
    private String content;
}

// ==========================================
// ✅ [Pattern A] 2. SSR 방식 (View Controller)
// 설명: 화면(HTML)을 반환하거나, Form 제출 후 Redirect 할 때 사용
// ==========================================
@Tag(name = "Board (View)", description = "게시판 화면 관련 컨트롤러")
@Controller
@RequestMapping("/board/posts")
@RequiredArgsConstructor
public class BoardViewController {

    private final BoardService boardService;

    @Operation(summary = "게시글 상세 조회 (화면)", description = "게시글 상세 페이지(HTML)를 반환합니다.")
    @GetMapping("/{postId}")
    public String viewPost(@PathVariable Long postId, Model model) {
        PostDetailDto post = boardService.getPost(postId);
        model.addAttribute("post", post);
        return "board/post-detail";
    }

    @Operation(summary = "게시글 삭제 (처리)", description = "삭제 후 목록 페이지로 이동(Redirect)합니다.")
    @PostMapping("/{postId}/delete")
    public String deletePost(@PathVariable Long postId, RedirectAttributes rttr) {
        boardService.deletePost(postId);
        rttr.addFlashAttribute("msg", "삭제되었습니다.");
        return "redirect:/board/list";
    }
}

// ==========================================
// ✅ [Pattern B] 3. API 방식 (API Controller)
// 설명: 화면 이동 없이 데이터(JSON)만 주고받을 때 사용 (팀 규칙 ApiResponse 사용)
// ==========================================
@Tag(name = "Board (API)", description = "게시판 데이터 API")
@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class BoardApiController {

    private final BoardService boardService;

    @Operation(summary = "게시글 작성 (API)", description = "게시글을 등록하고, 등록된 ID를 JSON으로 반환합니다.")
    // [추가] 노션 가이드에 따라 응답 코드를 명시하면 더 좋습니다.
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "게시글 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (입력값 누락 등)")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> writePost(@RequestBody PostCreateRequest request) {

        Long savedId = boardService.write(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(SuccessCode.CREATE_SUCCESS, savedId));
    }
}
