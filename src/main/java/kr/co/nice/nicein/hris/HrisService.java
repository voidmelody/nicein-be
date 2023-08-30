package kr.co.nice.nicein.hris;

import jakarta.transaction.Transactional;
import kr.co.nice.nicein.groupware.repository.EmployeeRepository;
import kr.co.nice.nicein.hris.dto.HrisDto;
import kr.co.nice.nicein.people.repository.ItEmployeeRepository;
import kr.co.nice.nicein.people.repository.ItPositionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class HrisService {

    private final ItPositionRepository itPositionRepository;

    public Page<HrisDto> makePositionData(String gwCode, Pageable pageable){
        Page<HrisDto> hrisItPositionDto = itPositionRepository.getHrisItPositionDto(gwCode, pageable);
        return hrisItPositionDto;

    }

}
