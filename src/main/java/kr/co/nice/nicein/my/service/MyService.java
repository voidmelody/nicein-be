package kr.co.nice.nicein.my.service;

import jakarta.transaction.Transactional;
import kr.co.nice.nicein.auth.entity.Member;
import kr.co.nice.nicein.groupware.entity.Company;
import kr.co.nice.nicein.groupware.entity.Employee;
import kr.co.nice.nicein.groupware.repository.CompanyRepository;
import kr.co.nice.nicein.groupware.repository.EmployeeRepository;
import kr.co.nice.nicein.my.dto.MyResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
@Slf4j
public class MyService {
    private final EmployeeRepository employeeRepository;

    public MyResponseDto getMember(Member member) throws NoSuchElementException {
        Employee employee = employeeRepository.findByLoginId(member.getUsername()).orElseThrow();
        return MyResponseDto.builder()
                .companyName(employee.getCompany().getCompanyName())
                .name(member.getName())
                .username(member.getUsername())
                .build();
    }
}
