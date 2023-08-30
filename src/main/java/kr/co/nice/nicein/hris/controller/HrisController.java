package kr.co.nice.nicein.hris.controller;

import kr.co.nice.nicein.auth.entity.Member;
import kr.co.nice.nicein.common.ErrorWriter;
import kr.co.nice.nicein.hris.HrisService;
import kr.co.nice.nicein.hris.dto.HrisDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/hris")
@RequiredArgsConstructor
public class HrisController {
    private final HrisService hrisService;
    private final ErrorWriter ew;

    @GetMapping("")
    public ResponseEntity getDeptId(@RequestParam String gwCode, @PageableDefault Pageable page){
        try{
            Map<String,Object> data = new LinkedHashMap<>();
            Page<HrisDto> hrisDtos = hrisService.makePositionData(gwCode, page);
            data.put("totalElements", hrisDtos.getTotalElements());
            data.put("totalPages", hrisDtos.getTotalPages());
            data.put("page", page.getPageNumber());
            data.put("emps", hrisDtos.getContent());
            return new ResponseEntity(data, HttpStatus.OK);
        }catch(Exception e){
            log.error(ew.getPrintStackTrace(e));
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
