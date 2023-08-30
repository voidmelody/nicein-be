package kr.co.nice.nicein.manage.repository;

import jakarta.transaction.Transactional;
import kr.co.nice.nicein.manage.entity.MasterAccountHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Transactional
@Repository
public interface MasterAccountHistoryRepository extends JpaRepository<MasterAccountHistory,Long> , MasterAccountHistoryRepositoryCustom {

}
