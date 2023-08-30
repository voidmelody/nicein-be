package kr.co.nice.nicein.excel.repository;

import jakarta.transaction.Transactional;
import kr.co.nice.nicein.excel.entity.ExcelEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ExcelEmployeeRepository extends JpaRepository<ExcelEmployee, String>, ExcelEmployeeRepositoryCustom {

}
