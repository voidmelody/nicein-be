package kr.co.nice.nicein.people.dto;

import kr.co.nice.nicein.groupware.entity.Company;
import kr.co.nice.nicein.groupware.entity.Department;
import kr.co.nice.nicein.groupware.entity.Employee;
import kr.co.nice.nicein.people.entity.ItEmployee;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class TableDto {
    private Employee employee;
    private ItEmployee itEmployee;
    private Company company;
    private Department department;
}
