package kr.co.nice.nicein.groupware.repository;


import kr.co.nice.nicein.manage.dto.PeopleHistoryDto;
import kr.co.nice.nicein.people.dto.HistoryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface HistoryRepositoryCustom {

    List<HistoryDto> findHistoryByUserId(String userId);
    Page<PeopleHistoryDto> getPeopleHistory(String type, Boolean excludeGroupWare, LocalDate startDate, LocalDate endDate, String searchText, Pageable pageable);
}
