package kr.co.nice.nicein.manage.repository;

import kr.co.nice.nicein.manage.entity.AccountHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountHistoryRepository extends JpaRepository<AccountHistory, Long> {
}
