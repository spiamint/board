package hello.board.controller;

import hello.board.domain.board.Board;
import hello.board.domain.comment.Comment;
import hello.board.domain.criteria.Criteria;
import hello.board.domain.member.Member;
import hello.board.domain.paging.PageMaker;
import hello.board.repository.BoardRepository;
import hello.board.repository.CommentRepository;
import hello.board.repository.MemberRepository;
import hello.board.repository.ResultDTO;
import hello.board.RedirectDTO;
import hello.board.auth.PrincipalDetails;
import hello.board.form.MemberEditForm;
import hello.board.form.MemberSaveForm;
import hello.board.form.OAuth2MemberEditForm;
import hello.board.form.OAuth2MemberSaveForm;
import hello.board.service.ImageService;
import hello.board.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;
    private final ImageService imageService;

    // 회원가입 /add
    @GetMapping("/add")
    public String addMember(Model model) {
        model.addAttribute("member", new Member());
        return "member/addForm";
    }

    @PostMapping("/add")
    public String addMember(@Validated @ModelAttribute("member") MemberSaveForm form,
                      BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        // 검증 오류 발생
        if (bindingResult.hasErrors()) {
            log.info("/add POST bindingResult.hasError");
            return "member/addForm";
        }

        Member member = new Member(
                form.getLoginId(), form.getUsername(), form.getPassword(), form.getEmail(), "ROLE_USER"
        );

        ResultDTO result = memberService.addMember(member);

        // 가입 실패
        if (!result.isSuccess()) {
            redirectAttributes.addFlashAttribute("redirectDTO", new RedirectDTO("/member/add", result.getCustomMessage()));
            return "redirect:/alert";
        }

        redirectAttributes.addFlashAttribute("redirectDTO", new RedirectDTO("/login", "회원가입 완료"));

        return "redirect:/alert";
    }

    @GetMapping("/add/oauth2")
    public String addOauth2Member(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                  RedirectAttributes redirectAttributes, Model model) {
        Member member = principalDetails.getMember();

        // 비정상 요청
        if (!member.getRole().equals("ROLE_TEMP")) {
            redirectAttributes.addFlashAttribute("redirectDTO", new RedirectDTO(
                    "/board/list/all", "소셜가입_비정상 요청"
            ));
            return "redirect:/alert";
        }

        model.addAttribute("member", principalDetails.getMember());
        model.addAttribute("isOauth2", "true");

        return "member/addForm";
    }

    @PostMapping("/add/oauth2")
        public String addOauth2Member(@Validated @ModelAttribute("member") OAuth2MemberSaveForm form,
                                      BindingResult bindingResult,
                                      @AuthenticationPrincipal PrincipalDetails principalDetails,
                                      RedirectAttributes redirectAttributes) {
        // 검증 오류 발생
        if (bindingResult.hasErrors()) {
            log.info("/add/oauth2 POST bindingResult.hasError {}", bindingResult);
            return "member/addForm";
        }

        Member member = principalDetails.getMember();
        member.setRole("ROLE_USER");
        member.setUsername(form.getUsername());

        ResultDTO result = memberService.addMember(member);

        // 가입 실패
        if (!result.isSuccess()) {
            redirectAttributes.addFlashAttribute("redirectDTO", new RedirectDTO("/member/add", result.getCustomMessage()));
            return "redirect:/alert";
        }

        redirectAttributes.addFlashAttribute("redirectDTO", new RedirectDTO("/board/list/all", "소셜계정 회원가입 완료"));

        return "redirect:/alert";
    }

    @GetMapping("/mypage/info")
    public String infoForm(@AuthenticationPrincipal PrincipalDetails principalDetails,
                           Model model) {
        Long id = principalDetails.getMember().getId();
        model.addAttribute("member", memberService.findById(id));

        // OAuth2 로그인 유저인 경우
        if (principalDetails.getMember().getProvider() != null) {
            model.addAttribute("isOauth2", "true");
        }

        return "member/infoForm";
    }

    @PostMapping("/edit")
    public String editMember(@Validated @ModelAttribute("member") MemberEditForm memberEditForm,
                             BindingResult bindingResult, RedirectAttributes redirectAttributes,
                             @AuthenticationPrincipal PrincipalDetails principalDetails
                             ) {
        // 검증 오류 발생
        if (bindingResult.hasErrors()) {
            log.info("/edit POST bindingResult.hasError");
            return "member/infoForm";
        }
        Member currentMember = principalDetails.getMember();

        // MemberEditForm -> Member
        Member updateParam = new Member(
                memberEditForm.getLoginId(), memberEditForm.getUsername(), memberEditForm.getPassword(), memberEditForm.getEmail()
        );

        // 멤버 수정
        Map<String, Object> resultMap = memberService.editMember(currentMember, updateParam);

        // syncUsername 실패
        if (resultMap == null) {
            redirectAttributes.addFlashAttribute("redirectDTO", new RedirectDTO(
                    "/member/mypage/info", "시스템 문제로 닉네임을 바꾸는데 실패했습니다."));
            return "redirect:/alert";
        }

        // loginId, password 수정 -> 강제 로그아웃
        if ((boolean) resultMap.get("isLogout")) {
            log.info("/edit isLogout = true");

            redirectAttributes.addFlashAttribute("redirectDTO", new RedirectDTO(
                    "/logout", "로그인 정보가 변경되었습니다. 재로그인해 주세요."));
            return "redirect:/alert";
        }

        Member updatedMember = (Member) resultMap.get("updatedMember");
        // 시큐리티 세션 갱신 (보안상? 맞는진? 모르겠음)
        principalDetails.editMember(updatedMember.getUsername());

        //alert
        redirectAttributes.addFlashAttribute("redirectDTO", new RedirectDTO(
                "/member/mypage/info", "멤버 정보가 변경되었습니다."
        ));

        return "redirect:/alert";
    }

    @PostMapping("/edit/oauth2")
    public String editOAuth2Member(@Validated @ModelAttribute("member")OAuth2MemberEditForm oAuth2MemberEditForm,
                                   @AuthenticationPrincipal PrincipalDetails principalDetails,
                                   BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            log.info("/edit POST bindingResult.hasError");
            return "member/infoForm";
        }
        Member currentMember = principalDetails.getMember();

        // oAuth2MemberEditForm -> Member
        Member updateParam = new Member("", oAuth2MemberEditForm.getUsername(), "", "");
        updateParam.setProviderId(oAuth2MemberEditForm.getProviderId());

        // 수정
        Map<String, Object> resultMap = memberService.editMember(currentMember, updateParam);

        // syncUsername 실패
        if (resultMap == null) {
            redirectAttributes.addFlashAttribute("redirectDTO", new RedirectDTO(
                    "/member/mypage/info", "시스템 문제로 닉네임을 바꾸는데 실패했습니다."));
            return "redirect:/alert";
        }

        Member updatedMember = (Member) resultMap.get("updatedMember");
        // 시큐리티 세션 갱신
        principalDetails.editMember(updatedMember.getUsername());

        redirectAttributes.addFlashAttribute("redirectDTO", new RedirectDTO(
                "/member/mypage/info", "멤버 정보가 변경되었습니다."
        ));
        return "redirect:/alert";

    }

    // 내 글
    @GetMapping({"/mypage/myboard/{categoryCode}", "/mypage/myboard"})
    public String myPage(@AuthenticationPrincipal PrincipalDetails principalDetails,
                         @ModelAttribute("criteria") Criteria criteria,
                         Model model) {

        Member currentMember = principalDetails.getMember();
//        log.info(criteria.getCurrentPage() + criteria.getKeyword() + criteria.getOption() + criteria.getCategory());

        Map<String, Object> resultMap = memberService.myPage(criteria, currentMember.getId());

        // 페이징 할 정보 설정하기
        PageMaker pageMaker = new PageMaker(criteria, (Integer) resultMap.get("countTotalContent"));

        // 페이지메이커, 글 목록 모델에 넣기
        model.addAttribute("pageMaker", pageMaker);
        model.addAttribute("boardList", resultMap.get("boardList"));

        return "member/myBoard";
    }

    // 내 댓글
    @GetMapping({"/mypage/mycomment/{categoryCode}", "/mypage/mycomment"})
    public String myComment(@AuthenticationPrincipal PrincipalDetails principalDetails,
                         @ModelAttribute("criteria") Criteria criteria,
                         Model model) {

        Member currentMember = principalDetails.getMember();
//        log.info(criteria.getCurrentPage() + criteria.getKeyword() + criteria.getOption() + criteria.getCategory());

        Map<String, Object> resultMap = memberService.myComment(criteria, currentMember.getId());

        // 페이징 할 정보 설정하기
        PageMaker pageMaker = new PageMaker(criteria, (Integer) resultMap.get("countTotalContent"));

        // 페이지메이커, 글 목록 모델에 넣기
        model.addAttribute("pageMaker", pageMaker);
        model.addAttribute("commentList", resultMap.get("commentList"));
        model.addAttribute("boardList", resultMap.get("boardList"));

        return "member/myComment";
    }

    @GetMapping("/delete")
    public String deleteMember(@AuthenticationPrincipal PrincipalDetails principalDetails,
                               RedirectAttributes redirectAttributes) {
        Long currentId = principalDetails.getMember().getId();

        // 이미지 삭제
        imageService.deleteImageByMemberId(currentId);

        // 멤버 삭제
        memberService.deleteMember(currentId);
        
        // 보험
        principalDetails.getMember().setRole("ROLE_TEMP");

        redirectAttributes.addFlashAttribute("redirectDTO",
                new RedirectDTO("/logout", "회원 탈퇴 되었습니다."));

        return "redirect:/alert";
    }

    @ResponseBody
    @GetMapping("/duplicateCheck")
    public boolean duplicateCheck(@RequestParam(value = "loginId", defaultValue = "") String loginId,
                                  @RequestParam(value = "username", defaultValue = "") String username) {

        String option = loginId.equals("") ? "username" : "loginId";
        String param = option.equals("username") ? username : loginId;

        log.info("duplicateCheck(), loginId = {}, username = {}, option = {}, param = {}", loginId, username, option, param);

        return memberService.duplicateCheck(option, param);
    }


}