package kr.co.nice.nicein.excel.repository;

import kr.co.nice.nicein.excel.entity.ExcelEmployee;

import java.util.List;

public interface ExcelEmployeeRepositoryCustom {
    List<ExcelEmployee> findAllNotNullItPosition(String companyId);
}
