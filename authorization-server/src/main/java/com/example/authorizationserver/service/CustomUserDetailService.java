package com.example.authorizationserver.service;

import com.example.authorizationserver.domain.Member;
import com.example.authorizationserver.domain.Member_Role;
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

@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    public Member save(Member user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return memberRepository.save(user);
    }

    @PostConstruct
    public void init(){
        if(memberRepository.findByUsername("user").isEmpty()){
            Member member = new Member();
            member.setUsername("user");
            member.setPassword("1234");
            member.setRole(Member_Role.ROLE_USER);
            System.out.println(this.save(member));
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUsername(username).orElseThrow(
            () -> new UsernameNotFoundException("USER IS NOT EXISTS"));
        return new User(member.getUsername(), member.getPassword(), getAuthorities(member));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Member member) {
        return List.of(new SimpleGrantedAuthority(member.getRole().name()));
    }
}