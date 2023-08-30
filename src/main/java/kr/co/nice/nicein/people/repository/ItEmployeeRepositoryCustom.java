package kr.co.nice.nicein.people.repository;

import kr.co.nice.nicein.groupware.entity.Employee;
import kr.co.nice.nicein.hris.dto.HrisDto;
import kr.co.nice.nicein.people.dto.ItPeopleResponseDto;
import kr.co.nice.nicein.people.dto.ItPeopleRetireDto;
import kr.co.nice.nicein.people.entity.MasterEmployee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface ItEmployeeRepositoryCustom {
    Long countItEmployeeByWorkTypeAndCompany(String type, String companyId);
    Long countItEmployeeByWorkType(String type);

    Long countUnClassItEmployee();
    Long countUnClassItEmployeeByCompany(String companyId);

    Long countRetireItEmployeeByWorkTypeAndCompany(String type, String companyId);
    Long countRetireItEmployeeByCompany(String companyId);
    Long countRetireItEmployeeByWorkType(String type);
    Long countAllRetireItEmployee();

    Page<ItPeopleRetireDto> getRetireItEmployeeByCompany(String companyId, Pageable pageable);

    Page<ItPeopleRetireDto> searchRetireItEmployee(String companyId, LocalDate startDate, LocalDate endDate, String searchText, Pageable pageable);

    Page<MasterEmployee> searchItPeople(String companyId, String position, String tech, String text, String type, Pageable pageable);

    List<Employee> getExcel();

}
