package kr.co.nice.nicein.rpa.repository;

import kr.co.nice.nicein.rpa.dto.RpaTaskRequestDto;
import kr.co.nice.nicein.rpa.dto.RpaTaskResponseDto;

import java.util.List;

public interface RpaTaskRepositoryCustom {

    List<RpaTaskRequestDto> findAllRpaTasks();

    List<RpaTaskRequestDto> searchRpaTasks(String companyId, String searchText, boolean includeNoUse);
}
