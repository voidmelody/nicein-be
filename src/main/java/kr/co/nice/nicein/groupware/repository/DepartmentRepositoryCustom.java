package kr.co.nice.nicein.groupware.repository;

import kr.co.nice.nicein.groupware.entity.Department;
import kr.co.nice.nicein.people.dto.OrganChartResponseDto;

import java.util.List;

public interface DepartmentRepositoryCustom {
    List<OrganChartResponseDto> findOrganChartDepth1ByCompany();
    List<OrganChartResponseDto> findOrganChartDepth0(String companyId);
    OrganChartResponseDto findDeptByCompany(String companyId);
    String getCompanyIdByDeptId(String deptId);

    List<OrganChartResponseDto> findChildDepartment(String deptName);

}
