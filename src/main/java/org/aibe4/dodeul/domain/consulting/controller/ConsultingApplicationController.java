package org.aibe4.dodeul.domain.consulting.controller;

import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.common.model.entity.SkillTag;
import org.aibe4.dodeul.domain.common.repository.SkillTagRepository;
import org.aibe4.dodeul.domain.consulting.model.dto.ConsultingApplicationDetailResponse;
import org.aibe4.dodeul.domain.consulting.model.dto.ConsultingApplicationRequest;
import org.aibe4.dodeul.domain.consulting.model.entity.ConsultingApplication;
import org.aibe4.dodeul.domain.consulting.model.enums.ConsultingTag;
import org.aibe4.dodeul.domain.consulting.service.ConsultingApplicationService;
import org.aibe4.dodeul.domain.member.model.entity.Member;
import org.aibe4.dodeul.domain.member.model.enums.Provider;
import org.aibe4.dodeul.domain.member.model.enums.Role;
import org.aibe4.dodeul.domain.member.model.repository.MemberRepository;
import org.aibe4.dodeul.global.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@Controller
@RequestMapping("/consulting-applications")
@RequiredArgsConstructor
public class ConsultingApplicationController {

    private final ConsultingApplicationService consultingApplicationService;
    private final MemberRepository memberRepository;
    private final SkillTagRepository skillTagRepository;

    // 🔥 [서버 켜질 때] 회원 1명 + 태그 'java' 자동 생성
    @PostConstruct
    public void init() {
        try {
            // 1. 회원 생성
            if (memberRepository.count() == 0) {
                Member testMember = Member.builder()
                    .email("test@test.com").nickname("테스트유저")
                    .passwordHash("password").role(Role.MENTEE).provider(Provider.LOCAL)
                    .build();
                memberRepository.save(testMember);
                System.out.println("✅ [1번 회원] 생성 완료!");
            }
            // 2. 'java' 태그 생성
            if (skillTagRepository.findByName("java").isEmpty()) {
                SkillTag javaTag = SkillTag.builder().name("java").build();
                skillTagRepository.save(javaTag);
                System.out.println("✅ [java 태그] 생성 완료!");
            }
        } catch (Exception e) {
            System.out.println("⚠️ 데이터 생성 중 경고: " + e.getMessage());
        }
    }

    // 1. 작성 폼
    @GetMapping("/form")
    public String applicationForm(Model model) {
        model.addAttribute("request", new ConsultingApplicationRequest());
        model.addAttribute("consultingTags", ConsultingTag.values());
        model.addAttribute("formActionUrl", "/consulting-applications");
        return "consulting/application-form";
    }

    // 2. 등록 처리 (▼ 여기가 에러 잡는 핵심입니다)
    @PostMapping
    @ResponseBody // ★ 화면 이동 말고 글자로 결과 보여줘라!
    public String registerApplication(
        @Valid @ModelAttribute("request") ConsultingApplicationRequest request,
        BindingResult bindingResult,
        Model model,
        @AuthenticationPrincipal CustomUserDetails user
    ) {
        if (bindingResult.hasErrors()) {
            return "입력값 에러: " + bindingResult.getAllErrors().toString();
        }

        try {
            Long memberId = (user != null) ? user.getMemberId() : 1L;
            request.setMenteeId(memberId);

            // 저장 시도
            Long savedApplicationId = consultingApplicationService.saveApplication(request);

            // 성공 시
            return "<html><body><h1>성공! 저장되었습니다.</h1>" +
                "<a href='/consulting-applications/" + savedApplicationId + "'>[상세 페이지로 이동]</a></body></html>";

        } catch (Exception e) {
            // ★ 실패 시 에러 내용을 화면에 토해냄
            e.printStackTrace();
            return "<html><body><h1 style='color:red'>에러 발생 (이걸 알려주세요)</h1>" +
                "<h3>에러 종류: " + e.getClass().getSimpleName() + "</h3>" +
                "<p><strong>메시지:</strong> " + e.getMessage() + "</p></body></html>";
        }
    }

    // 3. 상세 조회
    @GetMapping("/{applicationId}")
    public String getApplicationDetail(@PathVariable Long applicationId, Model model) {
        ConsultingApplicationDetailResponse response =
            consultingApplicationService.getApplicationDetail(applicationId);
        model.addAttribute("application", response);
        return "consulting/application-detail";
    }

    // 4. 수정 폼
    @GetMapping("/{applicationId}/edit")
    public String editForm(
        @PathVariable Long applicationId,
        Model model,
        @AuthenticationPrincipal CustomUserDetails user
    ) {
        ConsultingApplication application = consultingApplicationService.findApplicationEntity(applicationId);
        ConsultingApplicationRequest form = new ConsultingApplicationRequest();
        form.setTitle(application.getTitle());
        form.setContent(application.getContent());
        form.setConsultingTag(application.getConsultingTag());
        form.setFileUrl(application.getFileUrl());
        String tags = application.getApplicationSkillTags().stream()
            .map(tag -> tag.getSkillTag().getName())
            .collect(Collectors.joining(", "));
        form.setTechTags(tags);
        model.addAttribute("request", form);
        model.addAttribute("consultingTags", ConsultingTag.values());
        model.addAttribute("formActionUrl", "/consulting-applications/" + applicationId + "/edit");
        return "consulting/application-form";
    }

    // 5. 수정 처리
    @PostMapping("/{applicationId}/edit")
    public String updateApplication(
        @PathVariable Long applicationId,
        @Valid @ModelAttribute("request") ConsultingApplicationRequest request,
        BindingResult bindingResult,
        Model model,
        @AuthenticationPrincipal CustomUserDetails user
    ) {
        if (bindingResult.hasErrors()) return "consulting/application-form";
        Long currentMemberId = (user != null) ? user.getMemberId() : 1L;
        consultingApplicationService.updateApplication(applicationId, currentMemberId, request);
        return "redirect:/consulting-applications/" + applicationId;
    }

    // 6. 삭제 처리
    @PostMapping("/{applicationId}/delete")
    public String deleteApplication(
        @PathVariable Long applicationId,
        @AuthenticationPrincipal CustomUserDetails user
    ) {
        Long currentMemberId = (user != null) ? user.getMemberId() : 1L;
        consultingApplicationService.deleteApplication(applicationId, currentMemberId);
        return "redirect:/";
    }
}
