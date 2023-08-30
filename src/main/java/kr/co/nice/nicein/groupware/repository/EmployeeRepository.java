package kr.co.nice.nicein.groupware.repository;

import jakarta.transaction.Transactional;
import kr.co.nice.nicein.groupware.entity.Department;
import kr.co.nice.nicein.groupware.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>, EmployeeRepositoryCustom{
    Optional<Employee> findByUserId(String userId);
    Optional<Employee> findByLoginId(String email);

    @Override
    List<Employee> findAll();
}

