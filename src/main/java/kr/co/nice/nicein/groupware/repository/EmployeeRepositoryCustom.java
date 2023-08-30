package kr.co.nice.nicein.groupware.repository;

import kr.co.nice.nicein.groupware.entity.Department;
import kr.co.nice.nicein.groupware.entity.Employee;
import kr.co.nice.nicein.people.dto.PeopleResponseDto;
import kr.co.nice.nicein.people.vo.CountEmployee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmployeeRepositoryCustom {
    CountEmployee countEmployeeInCompanyByCompanyId(String companyId);
    CountEmployee countEmployeeAll();
    Page<PeopleResponseDto> searchPeople(String regCode, String deptId, String text, String itYn, Pageable pageable);
    Long countAllItEmployee();
    Long countItEmployeeByCompany(String companyId);

    Employee findByUserIdJoinItEmployee(String userId);

}
