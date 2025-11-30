package kr.java.mongodb.controller;

import jakarta.servlet.http.HttpSession;
import kr.java.mongodb.domain.Member;
import kr.java.mongodb.service.MainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// 주요 웹 요청을 처리하는 컨트롤러입니다.
@Controller
@RequiredArgsConstructor // Lombok을 이용한 생성자 주입
public class MainController {

    private final MainService mainService;
    private final static String SESSION_KEY = "SESSION_MEMBER_ID"; // 세션 키

    /**
     * 루트 경로("/") 접근 시 메인 페이지를 보여줍니다. (CRUD - Read)
     */
    @GetMapping("/")
    public String home(HttpSession session, Model model) {
        // 내장 세션에서 Member ID를 가져옵니다.
        String memberId = (String) session.getAttribute(SESSION_KEY);

        if (memberId != null) {
            // 세션 ID가 있다면, 해당 ID로 Member 도큐먼트를 조회합니다.
            // 메모 정보가 도큐먼트에 임베딩되어 있어 한 번의 DB 조회로 모든 메모를 가져옵니다.
            Member member = mainService.memberRepository.findById(memberId).orElse(null);

            // JSP로 전달하기 위해 Model에 담습니다. (스크립트릿 사용)
            model.addAttribute("member", member);
        }

        return "index"; // JSP view name
    }

    /**
     * 회원 이름(ID 대체)을 입력받아 세션을 생성(로그인 대체)합니다.
     */
    @PostMapping("/login")
    public String login(@RequestParam String name, HttpSession session) {
        if (name == null || name.trim().isEmpty()) {
            return "redirect:/";
        }

        // 이름으로 회원을 찾거나 새로 만듭니다.
        Member member = mainService.getOrCreateMember(name.trim());

        // Member의 ID를 세션에 저장하여 로그인 상태를 유지합니다.
        session.setAttribute(SESSION_KEY, member.getId());

        return "redirect:/";
    }

    /**
     * 새 메모를 추가합니다. (CRUD - Create)
     */
    @PostMapping("/memo/add")
    public String addMemo(@RequestParam String content, HttpSession session, RedirectAttributes rttr) {
        String memberId = (String) session.getAttribute(SESSION_KEY);

        if (memberId == null || content == null || content.trim().isEmpty()) {
            rttr.addFlashAttribute("error", "로그인 상태가 아니거나 내용이 비어있습니다.");
            return "redirect:/";
        }

        try {
            mainService.addMemo(memberId, content);
        } catch (Exception e) {
            rttr.addFlashAttribute("error", "메모 추가 중 오류가 발생했습니다: " + e.getMessage());
        }

        return "redirect:/";
    }

    /**
     * 기존 메모를 수정합니다. (CRUD - Update)
     */
    @PostMapping("/memo/update")
    public String updateMemo(@RequestParam String memoId, @RequestParam String content, HttpSession session, RedirectAttributes rttr) {
        String memberId = (String) session.getAttribute(SESSION_KEY);

        if (memberId == null || memoId == null || content == null || content.trim().isEmpty()) {
            rttr.addFlashAttribute("error", "로그인 상태가 아니거나 입력 값이 유효하지 않습니다.");
            return "redirect:/";
        }

        try {
            mainService.updateMemo(memberId, memoId, content);
        } catch (Exception e) {
            rttr.addFlashAttribute("error", "메모 수정 중 오류가 발생했습니다: " + e.getMessage());
        }

        return "redirect:/";
    }

    /**
     * 메모를 삭제합니다. (CRUD - Delete)
     */
    @PostMapping("/memo/delete")
    public String deleteMemo(@RequestParam String memoId, HttpSession session, RedirectAttributes rttr) {
        String memberId = (String) session.getAttribute(SESSION_KEY);

        if (memberId == null || memoId == null) {
            rttr.addFlashAttribute("error", "로그인 상태가 아니거나 메모 ID가 유효하지 않습니다.");
            return "redirect:/";
        }

        try {
            mainService.deleteMemo(memberId, memoId);
        } catch (Exception e) {
            rttr.addFlashAttribute("error", "메모 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }

        return "redirect:/";
    }

    /**
     * 로그아웃 (세션 무효화)
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // 세션을 완전히 무효화합니다.
        return "redirect:/";
    }
}
