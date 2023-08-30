package kr.co.nice.nicein.groupware.repository;

import jakarta.transaction.Transactional;
import kr.co.nice.nicein.groupware.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Transactional
@Repository
public interface DepartmentRepository extends JpaRepository<Department, String>, DepartmentRepositoryCustom {
    @Override
    Optional<Department> findById(String deptId);
}

