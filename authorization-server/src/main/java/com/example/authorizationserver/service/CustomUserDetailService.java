package com.example.authorizationserver.service;

import com.example.authorizationserver.domain.Member;
import com.example.authorizationserver.domain.Role;
import com.example.authorizationserver.repository.MemberRepository;
import java.util.Collection;
import java.util.List;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    public Member save(Member user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (StringUtils.hasText(user.getSnsSecretKey())) {
            user.setSnsSecretKey(passwordEncoder.encode(user.getSnsSecretKey()));
        }
        return memberRepository.save(user);
    }

    @PostConstruct
    public void init() {
        if (memberRepository.findByUsername("user").isEmpty()) {
            Member member = new Member();
            member.setUsername("user");
            member.setPassword("1234");
            member.setRole(Role.ROLE_USER);
            System.out.println(this.save(member));
        }
        if (memberRepository.findByUsername("user2").isEmpty()) {
            Member member = new Member();
            member.setUsername("user2");
            member.setRole(Role.ROLE_ADMIN);
            member.setSnsSync("google");
            member.setSnsSecretKey("hello");
            member.setPassword("socialLoginPassword");
            System.out.println(this.save(member));
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUsername(username).orElseThrow(
            () -> new UsernameNotFoundException("USER IS NOT EXISTS"));
        return new User(member.getUsername(), member.getPassword(), getAuthorities(member));
    }

    @Transactional
    public UserDetails loadUserByUsernameAndSnsSync(String username, String snsSync) {
        Member member = memberRepository.findByUsernameAndSnsSync(username, snsSync).orElseThrow(
            () -> new UsernameNotFoundException("invalid userInfo"));
        // 소셜로그인의 경우 유저 패스워드를 getSnsSecretKey() 로 설정
        return new User(member.getUsername(), member.getSnsSecretKey(), getAuthorities(member));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Member member) {
        return List.of(new SimpleGrantedAuthority(member.getRole().name()));
    }
}