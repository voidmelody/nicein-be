package kr.co.nice.nicein.auth.repository;

import kr.co.nice.nicein.auth.entity.Member;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Transactional
@Repository
public interface MemberRepository extends JpaRepository<Member,Long> , MemberRepositoryCustom {

    Optional<Member> findByUsername(String username);


}
