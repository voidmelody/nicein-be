package kr.co.nice.nicein.people.repository;

import jakarta.transaction.Transactional;
import kr.co.nice.nicein.groupware.entity.Employee;
import kr.co.nice.nicein.people.entity.ItEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Transactional
@Repository
public interface ItEmployeeRepository extends JpaRepository<ItEmployee, String>, ItEmployeeRepositoryCustom {

}
