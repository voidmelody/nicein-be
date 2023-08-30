package kr.co.nice.nicein.groupware.repository;


import jakarta.transaction.Transactional;
import kr.co.nice.nicein.groupware.entity.Code;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Transactional
@Repository
public interface CodeRepository extends JpaRepository<Code, Long>, CodeRepositoryCustom {

    List<Code> findByCategory(String category);
}
