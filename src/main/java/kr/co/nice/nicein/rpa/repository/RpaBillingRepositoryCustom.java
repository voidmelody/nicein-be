package kr.co.nice.nicein.rpa.repository;

import kr.co.nice.nicein.rpa.entity.RpaBilling;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface RpaBillingRepositoryCustom {

    Page<RpaBilling> searchRpaBilling(String companyId, LocalDateTime startDate, LocalDateTime endDate, String searchText, Pageable pageable);
}
