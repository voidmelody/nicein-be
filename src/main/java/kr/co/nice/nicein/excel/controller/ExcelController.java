package kr.co.nice.nicein.excel.controller;

import kr.co.nice.nicein.auth.entity.Member;
import kr.co.nice.nicein.common.ErrorWriter;
import kr.co.nice.nicein.excel.entity.Excel;
import kr.co.nice.nicein.excel.service.ExcelService;
import kr.co.nice.nicein.rpa.entity.GroupMailUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/excel")
@RequiredArgsConstructor
public class ExcelController {
    private final ExcelService excelService;
    private final ErrorWriter ew;

    @GetMapping("/it")
    public ResponseEntity getItExcel(@AuthenticationPrincipal Member member, @RequestParam String fileName, @RequestParam(required = false)String companyId){
        try{
            if(member.getAuthOption().equals("1")){
                if(member.getTargetCompanyId() != null){
                    companyId = member.getTargetCompanyId();
                }else{
                    companyId = member.getEmployee().getCompany().getCompanyId();
                }
            }
            Map<String, Object> data = new HashMap<>();
            List<Map<String,Object>> itData = excelService.getItData(member.getRole(),companyId);
            List<Excel> excelInfo = excelService.getItEmployeeExcelInfo();
            excelService.saveExcelDownHistory(member,fileName,companyId);
            data.put("data", itData);
            data.put("excel", excelInfo);
            return new ResponseEntity(data, HttpStatus.OK);
        }catch(Exception e){
            log.error(ew.getPrintStackTrace(e));
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/rpa")
    public ResponseEntity getRpaExcel(@AuthenticationPrincipal Member member, @RequestParam String fileName){
        try{
            Map<String, Object> data = new HashMap<>();
            List<Map<String, Object>> rpaData = excelService.getRpaData();
            List<Excel> rpaExcelInfo = excelService.getRpaExcelInfo();
            excelService.saveExcelDownHistory(member,fileName, null);
            data.put("data", rpaData);
            data.put("excel", rpaExcelInfo);
            return new ResponseEntity(data, HttpStatus.OK);
        }catch(Exception e){
            log.error(ew.getPrintStackTrace(e));
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
