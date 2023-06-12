package com.example.authorizationserver.repository;

import com.example.authorizationserver.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Integer> {

    Optional<Member> findByUsername(String username);

    Optional<Member> findByUsernameAndSnsSync(String username, String snsSync);
}
