package kr.co.nice.nicein.groupware.repository;

import kr.co.nice.nicein.groupware.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryRepository extends JpaRepository<History, Long>, HistoryRepositoryCustom{

}
