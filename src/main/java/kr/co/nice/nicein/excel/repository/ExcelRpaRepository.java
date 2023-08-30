package kr.co.nice.nicein.excel.repository;

import jakarta.transaction.Transactional;
import kr.co.nice.nicein.excel.entity.ExcelRpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Transactional
@Repository
public interface ExcelRpaRepository extends JpaRepository<ExcelRpa, String>, ExcelRpaRepositoryCustom {
}
