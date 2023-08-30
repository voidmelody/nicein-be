package kr.co.nice.nicein.groupware.repository;

import com.querydsl.core.Tuple;
import kr.co.nice.nicein.groupware.entity.Company;
import kr.co.nice.nicein.rpa.dto.CompanyDto;

import java.util.List;

public interface CompanyRepositoryCustom {
    List<CompanyDto> findAllCompanyIdAndCompanyNamesOrderByOrderNum();
}
