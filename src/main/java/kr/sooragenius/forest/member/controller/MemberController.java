package kr.sooragenius.forest.member.controller;

import kr.sooragenius.forest.member.dto.MemberDTO;
import kr.sooragenius.forest.member.enums.MemberAuthority;
import kr.sooragenius.forest.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.security.PermitAll;

@Controller @RequestMapping("/member")
@PermitAll
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    @GetMapping("/createView")
    public String createView() {
        return "/member/createView";
    }
    @PostMapping(value = {"/", ""})
    public String create(@ModelAttribute MemberDTO.Request request) {
        request.setAuthority(MemberAuthority.ROLE_ADMIN);
        MemberDTO.Response response = memberService.saveMember(request, passwordEncoder);

        return "";
    }
}
