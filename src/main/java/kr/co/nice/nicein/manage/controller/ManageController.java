package kr.co.nice.nicein.manage.controller;

import jakarta.transaction.Transactional;
import kr.co.nice.nicein.groupware.entity.Code;
import kr.co.nice.nicein.groupware.entity.History;
import kr.co.nice.nicein.manage.dto.PeopleHistoryDto;
import kr.co.nice.nicein.manage.entity.MasterAccountHistory;
import kr.co.nice.nicein.auth.entity.Member;
import kr.co.nice.nicein.common.ErrorWriter;
import kr.co.nice.nicein.manage.dto.AccountDto;
import kr.co.nice.nicein.manage.dto.AccountInfoDto;
import kr.co.nice.nicein.manage.dto.PwDto;
import kr.co.nice.nicein.manage.service.ManageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Transactional
@RestController
@RequestMapping("/manage")
@RequiredArgsConstructor
public class ManageController {
    private final ManageService manageService;
    private final ErrorWriter ew;


    @PostMapping("/changePw")
    public ResponseEntity<Boolean> changePw(@AuthenticationPrincipal Member member, @RequestBody PwDto pwDto){
        try{
            Map<String,Object> data = new HashMap<>();
            String successCode = manageService.changePw(member, pwDto);
            data.put("data", successCode);
            return new ResponseEntity(data, HttpStatus.OK);
        }catch (Exception e){
            log.error(ew.getPrintStackTrace(e));
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/account/search")
    public ResponseEntity searchAccount(@RequestParam(required = false) String searchText,
                                        @PageableDefault Pageable pageable){
        try{
            Map<String,Object> data = new HashMap<>();
            Page<AccountDto> accountDtos = manageService.searchAccount(searchText, pageable);
            data.put("data", accountDtos);
            return new ResponseEntity(data, HttpStatus.OK);
        }catch (Exception e){
            log.error(ew.getPrintStackTrace(e));
            return  new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/account")
    public ResponseEntity updateAccount(@AuthenticationPrincipal Member member, @RequestBody AccountInfoDto accountInfoDto){
        try{
            Map<String,Object> data = new HashMap<>();
            String successCode = manageService.updateAccount(member, accountInfoDto);
            data.put("data", successCode);
            return new ResponseEntity(data, HttpStatus.OK);
        }catch (Exception e){
            log.error(ew.getPrintStackTrace(e));
            return  new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/account")
    public ResponseEntity searchAccount(@RequestParam String account){
        try{
            Map<String,Object> data = new HashMap<>();
            AccountInfoDto accountInfo = manageService.getAccountInfo(account);
            data.put("data", accountInfo);
            return new ResponseEntity(data, HttpStatus.OK);
        }catch (Exception e){
            log.error(ew.getPrintStackTrace(e));
            return  new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/authChange")
    public ResponseEntity getAuthChange(@RequestParam(required = false) String startDate,
                                        @RequestParam(required = false) String endDate,
                                        @RequestParam(required = false) String searchText,
                                        @PageableDefault Pageable pageable){
        try{
            Map<String,Object> data = new HashMap<>();
            Page<MasterAccountHistory> authChangeHistory = manageService.getAuthChangeHistory(LocalDate.parse(startDate), LocalDate.parse(endDate), searchText, pageable);
            data.put("data",authChangeHistory);
            return new ResponseEntity(data, HttpStatus.OK);
        }catch (Exception e) {
            log.error(ew.getPrintStackTrace(e));
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/login")
    public ResponseEntity getLoginHistory(@RequestParam(required = false) String startDate,
                                          @RequestParam(required = false) String endDate,
                                          @RequestParam(required = false) String searchText,
                                          @PageableDefault Pageable pageable){
        try{
            Map<String,Object> data = new HashMap<>();
            Page<MasterAccountHistory> loginHistory;
            if(startDate == null && endDate == null){
                loginHistory = manageService.getLoginHistory(null, null,searchText,pageable);
            }else{
                loginHistory = manageService.getLoginHistory(LocalDate.parse(startDate), LocalDate.parse(endDate), searchText, pageable);
            }
            data.put("data", loginHistory);
            return new ResponseEntity(data,HttpStatus.OK);
        }catch (Exception e) {
            log.error(ew.getPrintStackTrace(e));
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/excel")
    public ResponseEntity getExcelHistory(@RequestParam(required = false) String startDate,
                                          @RequestParam(required = false) String endDate,
                                          @RequestParam(required = false) String searchText,
                                          @PageableDefault Pageable pageable){
        try{
            Map<String,Object> data = new HashMap<>();
            Page<MasterAccountHistory> excelHistory;
            if(startDate == null && endDate == null){
                excelHistory = manageService.getExcelHistory(null, null, searchText, pageable);
            }else{
                excelHistory = manageService.getExcelHistory(LocalDate.parse(startDate), LocalDate.parse(endDate), searchText, pageable);
            }
            data.put("data", excelHistory);
            return new ResponseEntity(data,HttpStatus.OK);
        }catch (Exception e) {
            log.error(ew.getPrintStackTrace(e));
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/people")
    public ResponseEntity getPeopleHistory(@RequestParam(required = false) String type,
                                           @RequestParam(required = false) Boolean excludeGroupWare,
                                           @RequestParam(required = false) String startDate,
                                           @RequestParam(required = false) String endDate,
                                           @RequestParam(required = false) String searchText,
                                           @PageableDefault Pageable pageable){
        try{
            Map<String,Object> data = new HashMap<>();
            Page<PeopleHistoryDto> peopleHistory;
            if(startDate == null && endDate == null){
                peopleHistory = manageService.getPeopleHistory(null, excludeGroupWare,null, null, searchText, pageable);
            }else{
                peopleHistory = manageService.getPeopleHistory(type, excludeGroupWare, LocalDate.parse(startDate), LocalDate.parse(endDate), searchText, pageable);
            }
            data.put("data", peopleHistory);
            return new ResponseEntity(data,HttpStatus.OK);
        }catch (Exception e) {
            log.error(ew.getPrintStackTrace(e));
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/typeList")
    public ResponseEntity getTypeList(){
        try{
            Map<String,Object> data = new HashMap<>();
            List<Code> typeList = manageService.getTypeList();
            data.put("data", typeList);
            return new ResponseEntity(data,HttpStatus.OK);
        }catch (Exception e) {
            log.error(ew.getPrintStackTrace(e));
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/categoryList")
    public ResponseEntity getCategoryList(){
        try{
            Map<String, Object> data = new HashMap<>();
            List<String> categoryList = manageService.getCategoryList();
            data.put("data", categoryList);
            return new ResponseEntity(data,HttpStatus.OK);
        }catch (Exception e) {
            log.error(ew.getPrintStackTrace(e));
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/code")
    public ResponseEntity getCode(@RequestParam(required = false) String category,
                                  @RequestParam(required = false) String searchText,
                                  @PageableDefault Pageable pageable){
        try{
            Map<String,Object> data = new HashMap<>();
            Page<Code> code = manageService.getCode(category, searchText, pageable);
            data.put("data", code);
            return new ResponseEntity(data,HttpStatus.OK);
        }catch (Exception e) {
            log.error(ew.getPrintStackTrace(e));
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
