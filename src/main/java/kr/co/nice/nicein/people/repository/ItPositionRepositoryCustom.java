package kr.co.nice.nicein.people.repository;

import kr.co.nice.nicein.hris.dto.HrisDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItPositionRepositoryCustom {
    List<String> findPositionsCodeByUserId(String userId);
    Long deleteByUserId(String userId);
    Page<HrisDto> getHrisItPositionDto(String gwCode, Pageable pageable);

}
