package kr.co.nice.nicein.people.controller;


import kr.co.nice.nicein.auth.entity.Member;
import kr.co.nice.nicein.groupware.repository.DepartmentRepository;
import kr.co.nice.nicein.people.dto.*;
import kr.co.nice.nicein.people.entity.MasterEmployee;
import kr.co.nice.nicein.people.service.PeopleService;
import kr.co.nice.nicein.common.ErrorWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/people")
@RequiredArgsConstructor
public class PeopleController {
    private final PeopleService peopleService;
    private final DepartmentRepository departmentRepository;
    private final ErrorWriter ew;


    @GetMapping("/deptId")
    public ResponseEntity getDeptId(@AuthenticationPrincipal Member member){
        try{
            Map<String,Object> data = new HashMap<>();
            String deptId = peopleService.getCompanyDeptId(member);
            data.put("data", deptId);
            return new ResponseEntity(data, HttpStatus.OK);
        }catch(Exception e){
            log.error(ew.getPrintStackTrace(e));
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/companyNames")
    public ResponseEntity getCompanyNames(@AuthenticationPrincipal Member member){
        try{
            Map<String, Object> data = new HashMap<>();
            List<OrganChartResponseDto> companyNames = peopleService.getCompanyNames(member);
            data.put("data", companyNames);
            return new ResponseEntity(data, HttpStatus.OK);
        }catch(Exception e){
            log.error(ew.getPrintStackTrace(e));
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/organ")
    public ResponseEntity getOrganChart(){
        try{
            Map<String, Object> data = new HashMap<>();
            List<OrganChartResponseDto> organChartDept1 = peopleService.getOrganChartDepth1();
            data.put("data", organChartDept1);
            return new ResponseEntity(data, HttpStatus.OK);
        }catch(Exception e){
            log.error(ew.getPrintStackTrace(e));
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/regList")
    public ResponseEntity getRegStandardList(){
        try{
            Map<String, Object> data = new HashMap<>();
            List<Map<String, String>> hrRegCodeList = peopleService.getHrRegCodeList();
            data.put("data", hrRegCodeList);
            return new ResponseEntity(data,HttpStatus.OK);
        }catch(Exception e){
            log.error(ew.getPrintStackTrace(e));
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/wt")
    public ResponseEntity getWorkTypeList(){
        try{
            Map<String, Object> data = new HashMap<>();
            List<Map<String, String>> hrWtList = peopleService.getItTypeList();
            data.put("data", hrWtList);
            return new ResponseEntity(data,HttpStatus.OK);
        }catch(Exception e){
            log.error(ew.getPrintStackTrace(e));
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/position")
    public ResponseEntity getPositionList(@RequestParam(required = false) String userId){
        try{
            Map<String, Object> data = new HashMap<>();
            List<Map<String, String>> positionList = peopleService.getPositionList();
            if(userId != null){
                List<String> peoplePositionsCode = peopleService.getPeoplePositionsCode(userId);
                data.put("data", peoplePositionsCode);
            }
            data.put("list", positionList);
            return new ResponseEntity(data,HttpStatus.OK);
        }catch(Exception e){
            log.error(ew.getPrintStackTrace(e));
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/tech")
    public ResponseEntity getTechList(@RequestParam(required = false) String userId){
        try{
            Map<String, Object> data = new HashMap<>();
            List<Map<String, String>> techList = peopleService.getTechList();
            List<List<Map<String, String>>> techListOrderByCode = peopleService.getTechListOrderByCode();
            List<String> techDescList = peopleService.getTechDescList();
            if(userId != null){
                List<String> peopleTechsCode = peopleService.getPeopleTechsCode(userId);
                data.put("data", peopleTechsCode);
            }
            data.put("list", techList);
            data.put("groupList", techListOrderByCode);
            data.put("desc", techDescList);
            return new ResponseEntity(data,HttpStatus.OK);
        }catch(Exception e){
            log.error(ew.getPrintStackTrace(e));
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/history")
    public ResponseEntity getPeopleHistory(@RequestParam String userId){
        try{
            Map<String, Object> data = new HashMap<>();
            List<HistoryDto> peopleHistory = peopleService.getPeopleHistory(userId);
            data.put("data", peopleHistory);
            return new ResponseEntity(data,HttpStatus.OK);
        }catch(Exception e){
            log.error(ew.getPrintStackTrace(e));
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/it")
    public ResponseEntity updatePeopleItYn(@RequestBody List<peopleItYnRequestDto> peopleList){
        try{
            Map<String, Object> data = new HashMap<>();
            Boolean isUpdate = peopleService.updatePeopleItYn(peopleList);
            data.put("data", isUpdate);
            return new ResponseEntity(data,HttpStatus.OK);
        }catch(Exception e) {
            log.error(ew.getPrintStackTrace(e));
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity getPeopleInfo(@RequestParam("id") String userId){
        try{
            Map<String, Object> data = new HashMap<>();
            PeopleResponseDto peopleInfo = peopleService.getPeopleInfo(userId);
            data.put("data", peopleInfo);
            return new ResponseEntity(data, HttpStatus.OK);
        }catch(Exception e){
            log.error(ew.getPrintStackTrace(e));
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity addPeople(@AuthenticationPrincipal Member member, @RequestBody addPeopleDto addPeopleDto){
        try{
            Map<String, Object> data = new HashMap<>();
            String people = peopleService.createPeople(member, addPeopleDto);
            data.put("data", people);
            return new ResponseEntity(data, HttpStatus.OK);
        }catch(Exception e){
            log.error(ew.getPrintStackTrace(e));
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/stat")
    public ResponseEntity getPeopleStat(@AuthenticationPrincipal Member member, @RequestParam(required = false) String deptId){
        try{
            String companyId = null;
            if(member.getAuthOption().equals("1")){
                if(member.getTargetCompanyId() != null){
                    companyId = member.getTargetCompanyId();
                }else{
                    companyId = member.getEmployee().getCompany().getCompanyId();
                }
            }else{
                if(deptId == null){
                    companyId = null;
                }else {
                    companyId = departmentRepository.getCompanyIdByDeptId(deptId);
                }
            }
            Map<String,Object> data = new HashMap<>();
            List<PeopleStatResponseDto> groupPeopleNumber = new ArrayList<>();
            if(companyId == null){
                groupPeopleNumber = peopleService.getGroupMemberStat();
            }else{
                PeopleStatResponseDto companyMemberNumber = peopleService.getCompanyMemberNumber(companyId);
                groupPeopleNumber.add(companyMemberNumber);
            }
            data.put("data", groupPeopleNumber);
            return new ResponseEntity(data,HttpStatus.OK);
        }catch(Exception e){
            log.error(ew.getPrintStackTrace(e));
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/it/stat")
    public ResponseEntity getItPeopleStat(@AuthenticationPrincipal Member member, @RequestParam(required = false) String companyId){
        try{
            if(companyId == null && member.getAuthOption().equals("1")){
                if(member.getTargetCompanyId() != null){
                    companyId = member.getTargetCompanyId();
                }else {
                    companyId = member.getEmployee().getCompany().getCompanyId();
                }
            }
            Map<String,Object> data = new HashMap<>();
            List<ItPeopleStatResponseDto> groupItMemberNumber = new ArrayList<>();
            if(companyId == null){
                groupItMemberNumber = peopleService.getGroupItMemberNumber();
            }else{
                ItPeopleStatResponseDto companyItMemberNumber = peopleService.getCompanyItMemberNumber(companyId);
                groupItMemberNumber.add(companyItMemberNumber);
            }
            data.put("data", groupItMemberNumber);
            return new ResponseEntity(data,HttpStatus.OK);
        }catch(Exception e){
            log.error(ew.getPrintStackTrace(e));
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/stat/all")
    public ResponseEntity getPeopleStat(){
        try{
            Map<String,Object> data = new HashMap<>();
            PeopleStatResponseDto allGroupMemberNumber = peopleService.getAllGroupMemberNumber();
            data.put("data", allGroupMemberNumber);
            return new ResponseEntity(data,HttpStatus.OK);
        }catch(Exception e){
            log.error(ew.getPrintStackTrace(e));
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/search")
    public ResponseEntity searchPeople(@RequestParam(required=false) String regCode,
                                       @RequestParam(required=false) String deptId,
                                       @RequestParam(required = false) String text,
                                       @RequestParam(required = false) String itYn,
                                       @PageableDefault Pageable pageable
                                       ){
        try{
            Map<String,Object> data = new HashMap<>();
            Page<PeopleResponseDto> searchResult = peopleService.searchPeople(regCode,deptId,text,itYn,pageable);
            List<OrganChartResponseDto> deptListInDownDept = peopleService.getChildDepartmentByDeptId(deptId);
            data.put("data", searchResult);
            data.put("list", deptListInDownDept);
            return new ResponseEntity(data,HttpStatus.OK);
        }catch(Exception e){
            log.error(ew.getPrintStackTrace(e));
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/deptList")
    public ResponseEntity getDeptList(@RequestParam String companyId){
        try{
            Map<String,Object> data = new HashMap<>();
            List<OrganChartResponseDto> childDepartmentByCompanyId = peopleService.getChildDepartmentByCompanyId(companyId);
            data.put("data", childDepartmentByCompanyId);
            return new ResponseEntity(data,HttpStatus.OK);
        }catch(Exception e){
            log.error(ew.getPrintStackTrace(e));
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/it/search")
    public ResponseEntity searchItPeople(@RequestParam(required = false) String companyId,
                                         @RequestParam(required = false) String type,
                                         @RequestParam(required = false) String position,
                                         @RequestParam(required = false) String tech,
                                         @RequestParam(required = false) String text,
                                         @PageableDefault Pageable pageable){
        try{
            Map<String,Object> data = new HashMap<>();
            Page<MasterEmployee> searchResult = peopleService.searchItPeople(companyId, type, position, tech, text, pageable);
            data.put("data", searchResult);
            return new ResponseEntity(data,HttpStatus.OK);
        }catch(Exception e){
            log.error(ew.getPrintStackTrace(e));
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/save")
    public ResponseEntity savePeopleInfo(@AuthenticationPrincipal Member member, @RequestBody ItPeopleSaveRequestDto itEmployee, @RequestParam("itYn") Boolean itYn){
        String saveResultCode = peopleService.savePeopleInfo(member, itEmployee, itYn);
        Map<String,Object> data = new HashMap<>();
        data.put("data", saveResultCode);
        return new ResponseEntity(data,HttpStatus.OK);
    }

    @GetMapping("/retire/stat")
    public ResponseEntity getRetireItPeopleStat(@AuthenticationPrincipal Member member, @RequestParam(required = false) String companyId){
        try{
            if(member.getAuthOption().equals("1")){
                if(member.getTargetCompanyId() != null){
                    companyId = member.getTargetCompanyId();
                }else{
                    companyId = member.getEmployee().getCompany().getCompanyId();
                }
            }
            Map<String,Object> data = new HashMap<>();
            List<ItPeopleStatResponseDto> groupItRetireMemberNumber = new ArrayList<>();
            if(companyId == null){
                groupItRetireMemberNumber = peopleService.getGroupRetireItMemberNumber();
            }else{
                ItPeopleStatResponseDto companyItRetireMemberNumber = peopleService.getCompanyRetireItMemberNumber(companyId);
                groupItRetireMemberNumber.add(companyItRetireMemberNumber);
            }
            data.put("data", groupItRetireMemberNumber);
            return new ResponseEntity(data,HttpStatus.OK);
        }catch(Exception e){
            log.error(ew.getPrintStackTrace(e));
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/retire")
    public ResponseEntity getRetireItPeople(@RequestParam(required = false) String companyId, @PageableDefault Pageable pageable){
        try{
            Map<String,Object> data = new HashMap<>();
            Page<ItPeopleRetireDto> companyRetireItPeople = peopleService.getCompanyRetireItPeople(companyId, pageable);
            data.put("data", companyRetireItPeople);
            return new ResponseEntity(data,HttpStatus.OK);
        }catch(Exception e){
            log.error(ew.getPrintStackTrace(e));
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/retire/search")
    public ResponseEntity searchRetireItPeople(@RequestParam(required = false) String companyId,
                                               @RequestParam String startDate,
                                               @RequestParam String endDate,
                                               @RequestParam(required = false) String searchText,
                                               @PageableDefault Pageable pageable){
        try{
            Map<String,Object> data = new HashMap<>();
            Page<ItPeopleRetireDto> searchRetireItPeople = peopleService.searchRetireItPeople(companyId, LocalDate.parse(startDate), LocalDate.parse(endDate), searchText, pageable);
            data.put("data", searchRetireItPeople);
            return new ResponseEntity(data,HttpStatus.OK);
        }catch(Exception e){
            log.error(ew.getPrintStackTrace(e));
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/retire")
    public ResponseEntity saveRetireReason(@RequestParam("userId") String userId, @RequestParam("cmpEndReason") String cmpEndReason){
        try{
            Map<String,Object> data = new HashMap<>();
            String saveResultCode = peopleService.saveCmpEndReason(userId, cmpEndReason);
            data.put("data", saveResultCode);
            return new ResponseEntity(data,HttpStatus.OK);
        }catch (Exception e){
            log.error(ew.getPrintStackTrace(e));
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
