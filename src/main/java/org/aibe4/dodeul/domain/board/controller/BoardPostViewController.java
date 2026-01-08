// src/main/java/org/aibe4/dodeul/domain/board/controller/BoardPostViewController.java
package org.aibe4.dodeul.domain.board.controller;

import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.board.model.dto.response.BoardPostDetailResponse;
import org.aibe4.dodeul.domain.board.service.BoardPostDetailService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardPostViewController {

    private final BoardPostDetailService boardPostDetailService;

    @GetMapping("/posts/{postId}")
    public String viewPost(@PathVariable Long postId, Model model, Principal principal) {
        if (principal == null) {
            model.addAttribute("message", "로그인이 필요합니다.");
            return "error/simple-error";
        }

        try {
            BoardPostDetailResponse post = boardPostDetailService.getDetail(postId);
            model.addAttribute("post", post);
            return "board/post-detail";
        } catch (IllegalArgumentException e) {
            model.addAttribute("message", e.getMessage());
            return "error/simple-error";
        }
    }
}
