package kr.co.nice.nicein.auth.repository;

import jakarta.transaction.Transactional;
import kr.co.nice.nicein.auth.entity.AuthMenu;
import kr.co.nice.nicein.security.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Transactional
@Repository
public interface AuthMenuRepository extends JpaRepository<AuthMenu, String> {
    Optional<AuthMenu> findByRole(String role);
}
