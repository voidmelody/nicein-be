package kr.co.nice.nicein.manage.repository;

import kr.co.nice.nicein.groupware.entity.History;
import kr.co.nice.nicein.manage.entity.MasterAccountHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface MasterAccountHistoryRepositoryCustom {

    Page<MasterAccountHistory> getAccountHistory(LocalDate startDate, LocalDate endDate, String searchText, Pageable pageable);
    Page<MasterAccountHistory> getLoginHistory(LocalDate startDate, LocalDate endDate, String searchText, Pageable pageable);
    Page<MasterAccountHistory> getExcelHistory(LocalDate startDate, LocalDate endDate, String searchText, Pageable pageable);


}
