package com.example.authorizationserver.service;

import com.example.authorizationserver.domain.Member;
import com.example.authorizationserver.domain.MemberDTO;
import com.example.authorizationserver.domain.ResponseDTO;
import com.example.authorizationserver.domain.Role;
import com.example.authorizationserver.repository.MemberRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
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

    public ResponseDTO register(MemberDTO memberDTO) {
        memberDTO.validate();
        Member member;
        Optional<Member> optionalMember = memberRepository.findByUsername(memberDTO.getUsername());
        if (optionalMember.isPresent()) {
            if (!StringUtils.hasText(memberDTO.getSnsSync())) {
                return new ResponseDTO("N", "이미 가입된 계정입니다");
            } else {
                member = optionalMember.get();
                memberDTO.setSnsSecret(passwordEncoder.encode(memberDTO.getSnsSecret()));
                member.snsSync(memberDTO);
            }
        } else {
            memberDTO.setPassword(passwordEncoder.encode(memberDTO.getPassword()));
            if (StringUtils.hasText(memberDTO.getSnsSecret())) {
                memberDTO.setSnsSecret(passwordEncoder.encode(memberDTO.getSnsSecret()));
            }
            member = new Member(memberDTO);
        }
        memberRepository.save(member);
        return new ResponseDTO("Y", memberDTO.getUsername());
    }

    public ResponseDTO registerCheck(MemberDTO memberDTO) {
        Optional<Member> optionalMember = memberRepository.findByUsername(memberDTO.getUsername());
        if (optionalMember.isEmpty()) {
            return new ResponseDTO("N", "not Register");
        } else {
            Member member = optionalMember.get();
            if (StringUtils.hasText(member.getSnsSync())) {
                return new ResponseDTO("Y", member.getSnsSync());
            }
            return new ResponseDTO("N", "not snsUser");
        }
    }

    @PostConstruct
    public void init() {
        if (memberRepository.findByUsername("user").isEmpty()) {
            MemberDTO member = new MemberDTO();
            member.setUsername("user");
            member.setPassword("1234");
            member.setRole(Role.ROLE_USER);
            System.out.println(this.register(member));
        }
        if (memberRepository.findByUsername("user2").isEmpty()) {
            MemberDTO member = new MemberDTO();
            member.setUsername("user2");
            member.setRole(Role.ROLE_ADMIN);
            member.setSnsSync("google");
            member.setSnsSecret("hello");
            member.setPassword("socialLoginPassword");
            System.out.println(this.register(member));
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUsername(username).orElseThrow(
            () -> new UsernameNotFoundException("invalid userInfo"));
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