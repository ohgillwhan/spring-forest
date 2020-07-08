package kr.sooragenius.forest.config.security;

import kr.sooragenius.forest.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final MemberService memberService;
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        MemberLogin memberLogin = memberService.findByIdForLogin(s);
        return memberLogin;
    }
}
