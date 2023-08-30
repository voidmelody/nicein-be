package kr.co.nice.nicein.groupware.repository;

import jakarta.transaction.Transactional;
import kr.co.nice.nicein.groupware.entity.Company;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public interface CompanyRepository extends JpaRepository<Company, String>, CompanyRepositoryCustom {
    @Override
    Optional<Company> findById(String companyId);
}

