package kr.co.nice.nicein.rpa.repository;

import kr.co.nice.nicein.rpa.entity.RpaBilling;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RpaBillingRepository extends JpaRepository<RpaBilling, Long>, RpaBillingRepositoryCustom {
}
