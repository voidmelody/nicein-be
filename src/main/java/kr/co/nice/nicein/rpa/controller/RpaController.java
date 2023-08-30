package kr.co.nice.nicein.rpa.controller;

import kr.co.nice.nicein.common.ErrorWriter;
import kr.co.nice.nicein.groupware.entity.Company;
import kr.co.nice.nicein.rpa.dto.GroupMailUserRequestDto;
import kr.co.nice.nicein.rpa.dto.GroupMailUserResponseDto;
import kr.co.nice.nicein.rpa.dto.RpaTaskRequestDto;
import kr.co.nice.nicein.rpa.dto.RpaTaskResponseDto;
import kr.co.nice.nicein.rpa.entity.RpaBilling;
import kr.co.nice.nicein.rpa.service.RpaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/rpa")
@RequiredArgsConstructor
public class RpaController {
    private final RpaService rpaService;
    private final ErrorWriter ew;

    // 회사 이름 리스트 반환
    @GetMapping("/companyList")
    public ResponseEntity getCompanyNames(){
        try{
            Map<String, Object> data = new HashMap<>();
            List<Map<String, Object>> companyMap = rpaService.getAllCompany();
            data.put("data", companyMap);
            return new ResponseEntity(data, HttpStatus.OK);
        }catch(Exception e){
            log.error(ew.getPrintStackTrace(e));
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/categoryList")
    public ResponseEntity getCategoryMap(){
        try{
            Map<String, Object> data = new HashMap<>();
            List<Map<String, String>> categoryList = rpaService.getCategoryList();
            data.put("data", categoryList);
            return new ResponseEntity(data,HttpStatus.OK);
        }catch(Exception e){
            log.error(ew.getPrintStackTrace(e));
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // 전체 task 또는 검색된 task 화면 표시
    @GetMapping("/tasks")
    public ResponseEntity getRpaTaskGroup(@RequestParam(required = false) String companyId,
                                          @RequestParam(required = false) String searchText,
                                          @RequestParam(required = false) boolean includeNoUse){
        try{
            Map<String, Object> data = new HashMap<>();
            List<RpaTaskResponseDto> rpaTaskGroup = rpaService.getRpaTaskGroup(companyId, searchText, includeNoUse);
            data.put("data", rpaTaskGroup);
            return new ResponseEntity(data,HttpStatus.OK);
        }catch(Exception e){
            log.error(ew.getPrintStackTrace(e));
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 업무 추가
    @PostMapping("/task")
    public ResponseEntity addRpaTask(@RequestBody RpaTaskRequestDto request){
        try{
            Map<String, Object> data = new HashMap<>();
            Boolean isSuccess = rpaService.addRpaTask(request);
            data.put("data", isSuccess);
            return new ResponseEntity(data,HttpStatus.OK);
        }catch (Exception e){
            log.error(ew.getPrintStackTrace(e));
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 특정 업무 수정
    @PutMapping("/task")
    public ResponseEntity updateRpaTask(@RequestBody RpaTaskRequestDto request){
        try{
            Map<String, Object> data = new HashMap<>();
            Boolean isSuccess = rpaService.updateRpaTask(request);
            data.put("data", isSuccess);
            return new ResponseEntity(data,HttpStatus.OK);
        }catch (Exception e){
            log.error(ew.getPrintStackTrace(e));
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
//    // 특정 업무 삭제 -> 주석 사유 : 삭제 버튼 제거. DB가 아닌 column으로 처리.
//    @DeleteMapping("/task/{taskId}")
//    public ResponseEntity deleteRpaTask(@PathVariable String taskId){
//        try{
//            Map<String, Object> data = new HashMap<>();
//            Boolean isSuccess = rpaService.deleteRpaTask(taskId);
//            data.put("data", isSuccess);
//            return new ResponseEntity(data,HttpStatus.OK);
//        }catch (Exception e){
//            log.error(ew.getPrintStackTrace(e));
//            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    // 특정 업무 그룹메일 현황
    @GetMapping("/mails")
    public ResponseEntity getRpaTaskMails(@RequestParam String taskId){
        try{
            Map<String, Object> data = new HashMap<>();
            List<GroupMailUserResponseDto> rpaTaskMails = rpaService.getRpaTaskMails(taskId);
            data.put("data", rpaTaskMails);
            return new ResponseEntity(data,HttpStatus.OK);
        }catch(Exception e){
            log.error(ew.getPrintStackTrace(e));
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 특정 업무 그룹메일 추가 (그룹 내 & 외)
    @PostMapping("/mails")
    public ResponseEntity addGroupMails(@RequestBody List<GroupMailUserRequestDto> request){
        try{
            Map<String, Object> data = new HashMap<>();
            List<GroupMailUserResponseDto> groupMailUserResponseDtos = rpaService.addGroupMail(request);
            data.put("data", groupMailUserResponseDtos);
            return new ResponseEntity(data,HttpStatus.OK);
        }catch(Exception e){
            log.error(ew.getPrintStackTrace(e));
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 특정 업무 그룹메일 수정 (그룹 내 & 그룹 외)
    @PutMapping("/mails")
    public ResponseEntity updateGroupMail(@RequestBody GroupMailUserRequestDto request){
        try{
            Map<String, Object> data = new HashMap<>();
            Boolean isUpdate = rpaService.updateGroupMail(request);
            data.put("data", isUpdate);
            return new ResponseEntity(data,HttpStatus.OK);
        }catch(Exception e){
            log.error(ew.getPrintStackTrace(e));
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 특정 업무 그룹메일 삭제 (그룹 내 & 그룹 외)
    @DeleteMapping("/mails")
    public ResponseEntity deleteGroupMailUser(@RequestBody GroupMailUserRequestDto request){
        try{
            Map<String, Object> data = new HashMap<>();
            Boolean isDelete = rpaService.deleteGroupMail(request);
            data.put("data", isDelete);
            return new ResponseEntity(data, HttpStatus.OK);
        }catch (Exception e){
            log.error(ew.getPrintStackTrace(e));
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 그룹 내 인원 조회
    @GetMapping("/mails/search")
    public ResponseEntity searchGroupMail(@RequestParam(required = false) String companyId,
                                                          @RequestParam(required = false) String searchText){
        try{
            Map<String, Object> data = new HashMap<>();
            List<GroupMailUserResponseDto> groupMailUserResponseDtos = rpaService.searchGroupMail(companyId, searchText);
            data.put("data", groupMailUserResponseDtos);
            return new ResponseEntity(data,HttpStatus.OK);
        }catch (Exception e) {
            log.error(ew.getPrintStackTrace(e));
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //RPA 수행 결과 빌링
    @GetMapping("/billing")
    public ResponseEntity searchRpaBilling(@RequestParam(required = false)String companyId,
                                           @RequestParam(required = false)String startDate,
                                           @RequestParam(required = false)String endDate,
                                           @RequestParam(required = false)String searchText,
                                           @PageableDefault Pageable pageable){
        try{
            Map<String, Object> data = new HashMap<>();
            LocalDateTime startTime = LocalDateTime.of(LocalDate.parse(startDate), LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(LocalDate.parse(endDate), LocalTime.MAX);
            Page<RpaBilling> rpaBillings = rpaService.searchRpaBilling(companyId, startTime, endTime, searchText, pageable);
            data.put("data", rpaBillings);
            return new ResponseEntity(data,HttpStatus.OK);
        }catch (Exception e) {
            log.error(ew.getPrintStackTrace(e));
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
