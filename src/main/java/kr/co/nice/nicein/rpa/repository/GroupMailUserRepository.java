package kr.co.nice.nicein.rpa.repository;

import jakarta.transaction.Transactional;
import kr.co.nice.nicein.groupware.entity.Employee;
import kr.co.nice.nicein.rpa.entity.GroupMailUser;
import kr.co.nice.nicein.rpa.entity.RpaTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public interface GroupMailUserRepository extends JpaRepository<GroupMailUser, Long>, GroupMailUserRepositoryCustom{

    @Override
    Optional<GroupMailUser> findById(Long id);
    Optional<List<GroupMailUser>> findByEmployee(Employee employee);
    List<GroupMailUser> findByManualUserEmail(String email);
}
