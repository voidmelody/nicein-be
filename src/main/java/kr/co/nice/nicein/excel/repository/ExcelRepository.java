package kr.co.nice.nicein.excel.repository;

import jakarta.transaction.Transactional;
import kr.co.nice.nicein.excel.entity.Excel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ExcelRepository extends JpaRepository<Excel, String>, ExcelRepositoryCustom {

    List<Excel> findByCategory(String category);
}
