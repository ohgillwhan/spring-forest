package kr.sooragenius.forest.member.service;

import kr.sooragenius.forest.config.security.MemberLogin;
import kr.sooragenius.forest.member.domain.Member;
import kr.sooragenius.forest.member.dto.MemberDTO;
import kr.sooragenius.forest.member.infra.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sun.security.util.Password;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberDTO.Response saveMember(MemberDTO.Request request, PasswordEncoder passwordEncoder) {
        Member member = Member.of(request, passwordEncoder);

        Member save = memberRepository.save(member);

        return MemberDTO.Response.of(save);
    }
    public MemberLogin findByIdForLogin(String s) {
        Member member = memberRepository.findById(s).orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 계정입니다."));

        return MemberLogin.of(member);
    }
}
