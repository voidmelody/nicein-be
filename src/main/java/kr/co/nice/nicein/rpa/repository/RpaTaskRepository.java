package kr.co.nice.nicein.rpa.repository;

import kr.co.nice.nicein.groupware.entity.Company;
import kr.co.nice.nicein.rpa.entity.RpaTask;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RpaTaskRepository extends JpaRepository<RpaTask, String>, RpaTaskRepositoryCustom {
}
