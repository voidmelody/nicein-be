package kr.co.nice.nicein.rpa.service;

import jakarta.transaction.Transactional;
import kr.co.nice.nicein.groupware.entity.Code;
import kr.co.nice.nicein.groupware.entity.Company;
import kr.co.nice.nicein.groupware.repository.CodeRepository;
import kr.co.nice.nicein.groupware.repository.CompanyRepository;
import kr.co.nice.nicein.groupware.repository.EmployeeRepository;
import kr.co.nice.nicein.rpa.dto.*;
import kr.co.nice.nicein.rpa.entity.GroupMailUser;
import kr.co.nice.nicein.rpa.entity.RpaBilling;
import kr.co.nice.nicein.rpa.entity.RpaTask;
import kr.co.nice.nicein.rpa.repository.GroupMailUserRepository;
import kr.co.nice.nicein.rpa.repository.RpaBillingRepository;
import kr.co.nice.nicein.rpa.repository.RpaTaskRepository;
import kr.co.nice.nicein.common.vo.TableStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class RpaService {
    private final GroupMailUserRepository groupMailUserRepository;
    private final RpaTaskRepository rpaTaskRepository;
    private final CompanyRepository companyRepository;
    private final EmployeeRepository employeeRepository;
    private final CodeRepository codeRepository;
    private final RpaBillingRepository rpaBillingRepository;


    // 전체 리스트 또는 검색
    public List<RpaTaskResponseDto> getRpaTaskGroup(String companyId, String searchText, boolean includeNoUse){
        List<RpaTaskRequestDto> allRpaTasks = rpaTaskRepository.searchRpaTasks(companyId,searchText,includeNoUse);
        List<RpaTaskResponseDto> result= new ArrayList<>();
        for(RpaTaskRequestDto rpaTask : allRpaTasks){
            CompanyDto companyDto;
            if(rpaTask.getCompanyId() != null){
                Company rpaCompany = companyRepository.findById(rpaTask.getCompanyId()).orElseThrow();
                companyDto = new CompanyDto(rpaCompany.getCompanyId(), rpaCompany.getCompanyName());
            }else{
                companyDto = new CompanyDto();

            }

            RpaTaskResponseDto dto = RpaTaskResponseDto.builder()
                    .taskId(rpaTask.getTaskId())
                    .company(companyDto)
                    .taskName(rpaTask.getTaskName())
                    .taskSaveTime(rpaTask.getTaskSaveTime())
                    .rpatype01(rpaTask.getRpatype01())
                    .rpatype02(rpaTask.getRpatype02())
                    .rpatype03(rpaTask.getRpatype03())
                    .rpatype04(rpaTask.getRpatype04())
                    .manager(rpaTask.getManager())
                    .schedule(rpaTask.getSchedule())
                    .botName(rpaTask.getBotName())
                    .useYn(rpaTask.getUseYn())
                    .build();
            result.add(dto);
        }
        return result;
    }

    // Rpa Task 추가
    public Boolean addRpaTask(RpaTaskRequestDto request){
        Optional<RpaTask> existedTask = rpaTaskRepository.findById(request.getTaskId());
        if(existedTask.isPresent()){
            throw new IllegalStateException("이미 존재하는 Rpa 업무입니다.");
        }
        RpaTask rpaTask = RpaTask.builder()
                .taskId(request.getTaskId())
                .company(companyRepository.findById(request.getCompanyId()).orElseThrow())
                .taskName(request.getTaskName())
                .taskSaveTime(request.getTaskSaveTime())
                .manager(request.getManager())
                .schedule(request.getSchedule())
                .botName(request.getBotName())
                .useYn(request.getUseYn())
                .build();
        rpaTaskRepository.save(rpaTask);
        return true;
    }
    // Rpa Task 수정
    public Boolean updateRpaTask(RpaTaskRequestDto request){
        RpaTask targetRpaTask = rpaTaskRepository.findById(request.getTaskId()).get();
        RpaTask updatedRpaTask = setRpaTask(targetRpaTask, request);
        rpaTaskRepository.save(updatedRpaTask);
        return true;
    }

    // Rpa Task에 해당하는 그룹 메일 가져오기
    // active_YN의 Y에서만 가져와야함.
    public List<GroupMailUserResponseDto> getRpaTaskMails(String taskId){
        RpaTask rpaTask = rpaTaskRepository.findById(taskId).get();
        List<GroupMailUser> groupMailUsers = groupMailUserRepository.findByRpaTask(rpaTask);
        return convertResponseDto(groupMailUsers);
    }

    // 그룹 메일 그룹 추가
    public List<GroupMailUserResponseDto> addGroupMail(List<GroupMailUserRequestDto> requestList){
        List<GroupMailUserResponseDto> result = new ArrayList<>();
        for(GroupMailUserRequestDto request: requestList){
            RpaTask rpaTask = rpaTaskRepository.findById(request.getTaskId()).get();
            GroupMailUser groupMailUser = createGroupMailUser(request, rpaTask);
            groupMailUserRepository.save(groupMailUser);

            GroupMailUserResponseDto groupMailUserResponseDto = GroupMailUserResponseDto.builder()
                    .mailUserId(groupMailUser.getId())
                    .category(groupMailUser.getCategory())
                    .companyName(groupMailUser.getManualCompanyName())
                    .name(groupMailUser.getManualUserName())
                    .email(groupMailUser.getManualUserEmail())
                    .account(groupMailUser.getAccount())
                    .build();
            result.add(groupMailUserResponseDto);
        }
        return result;
    }

    // 특정 그룹메일 수정하기
    // 그룹 내 인원 && 그룹 외 인원 구분 처리
    // taskId 필터 꼭!!
    public Boolean updateGroupMail(GroupMailUserRequestDto request){
        GroupMailUser user = getGroupMailUser(request);
        GroupMailUser changed = setGroupMail(user, request);
        groupMailUserRepository.save(changed);
        return true;
    }

    // 특정 그룹메일 삭제하기
    // 그룹 내 인원 && 그룹 외 인원 구분 처리 해야함.
    public Boolean deleteGroupMail(GroupMailUserRequestDto request){
        GroupMailUser user = getGroupMailUser(request);
        user.setActiveYn(TableStatus.NO.getValue());
        groupMailUserRepository.save(user);
        return true;
    }

    // 그룹내 메일 인원 검색하기
    public List<GroupMailUserResponseDto> searchGroupMail(String companyId, String searchText){
        List<GroupMailUserResponseDto> result = groupMailUserRepository.findBySearchOption(companyId, searchText);
        return result;
    }

    private GroupMailUser getGroupMailUser(GroupMailUserRequestDto request) {
        Long mailUserId = request.getMailUserId();
        GroupMailUser groupMailUser = groupMailUserRepository.findById(mailUserId).orElseThrow();
        return groupMailUser;
    }

    public List<GroupMailUserResponseDto> convertResponseDto(List<GroupMailUser> groupMailUsers){
        List<GroupMailUserResponseDto> rpaTaskMails = new ArrayList<>();
        for(GroupMailUser user : groupMailUsers){
            if(!user.getActiveYn().equals(TableStatus.YES.getValue())){
                continue;
            }
            GroupMailUserResponseDto groupMailUserDto = getGroupMailUserResponseDto(user);
            rpaTaskMails.add(groupMailUserDto);
        }
        return rpaTaskMails.stream().sorted(Comparator.comparing(GroupMailUserResponseDto::getCategory)).toList();
    }

    private GroupMailUser createGroupMailUser(GroupMailUserRequestDto request, RpaTask rpaTask) {
        GroupMailUser groupMailUser = GroupMailUser.builder()
                .rpaTask(rpaTask)
                .category("rpatype04")
                .account(request.getAccount())
                .activeYn(TableStatus.YES.getValue())
                .build();
        // 그룹 외 인원
        if(request.getDepartmentName()==null){
            groupMailUser.setCategory(request.getCategory());
            groupMailUser.setGroupMemberYn(TableStatus.NO.getValue());
            groupMailUser.setManualUserName(request.getName());
            groupMailUser.setManualUserEmail(request.getEmail());
            groupMailUser.setManualCompanyName(request.getCompanyName());
            // 그룹 내 인원
        }else{
            groupMailUser.setGroupMemberYn(TableStatus.YES.getValue());
            groupMailUser.setEmployee(employeeRepository.findByLoginId(request.getEmail()).get());
        }
        return groupMailUser;
    }
    private GroupMailUserResponseDto getGroupMailUserResponseDto(GroupMailUser user) {
        GroupMailUserResponseDto groupMailUserResponseDto;
        groupMailUserResponseDto = GroupMailUserResponseDto.builder()
                .mailUserId(user.getId())
                .taskId(user.getRpaTask().getTaskId())
                .taskName(user.getRpaTask().getTaskName())
                .category(user.getCategory())
                .account(user.getAccount())
                .build();
        // 그룹 인원인 경우
        if(user.getGroupMemberYn() != null && user.getGroupMemberYn().equals(TableStatus.YES.getValue())){
                if(user.getEmployee() != null){
                    groupMailUserResponseDto.setCompanyName(user.getEmployee().getCompany().getCompanyName());
                    groupMailUserResponseDto.setDepartmentName(user.getEmployee().getDepartment().getDeptName());
                    groupMailUserResponseDto.setName(user.getEmployee().getUsername());
                    groupMailUserResponseDto.setEmail(user.getEmployee().getUserEmail());
                    groupMailUserResponseDto.setGroupMemberYn(user.getGroupMemberYn());
                    // 퇴사한 인원
                }else{
                    groupMailUserResponseDto.setCompanyName("그룹웨어 계정 삭제(퇴사)");
                    groupMailUserResponseDto.setGroupMemberYn(user.getGroupMemberYn());
                }
        }else{
                groupMailUserResponseDto.setCompanyName(user.getManualCompanyName());
                groupMailUserResponseDto.setDepartmentName(null);
                groupMailUserResponseDto.setName(user.getManualUserName());
                groupMailUserResponseDto.setEmail(user.getManualUserEmail());
                groupMailUserResponseDto.setGroupMemberYn(TableStatus.NO.getValue());
        }
        return groupMailUserResponseDto;
    }

    public RpaTask setRpaTask(RpaTask target, RpaTaskRequestDto request){
        if(!target.getTaskId().equals(request.getTaskId())){
            target.setTaskId(request.getTaskId());
        }
        if(!target.getCompany().getCompanyId().equals(request.getCompanyId())){
            target.setCompany(companyRepository.findById(request.getCompanyId()).orElseThrow());
        }
        if(!target.getTaskName().equals(request.getTaskName())){
            target.setTaskName(request.getTaskName());
        }
        if(target.getTaskSaveTime() == null || !target.getTaskSaveTime().equals(request.getTaskSaveTime())){
            if(request.getTaskSaveTime() == null){
                target.setTaskSaveTime(null);
            }else {
                target.setTaskSaveTime(request.getTaskSaveTime());
            }
        }
        if(target.getManager() == null || !target.getManager().equals(request.getManager())){
            if(request.getManager() == null){
                target.setManager(null);
            }else{
                target.setManager(request.getManager());
            }
        }
        if(!target.getSchedule().equals(request.getSchedule())){
            target.setSchedule(request.getSchedule());
        }
        if(!target.getBotName().equals(request.getBotName())){
            target.setBotName(request.getBotName());
        }
        if(!target.getUseYn().equals(request.getUseYn())){
            target.setUseYn(request.getUseYn());
        }
        return target;
    }

    public GroupMailUser setGroupMail(GroupMailUser target, GroupMailUserRequestDto request){
        if(!(target.getCategory()).equals(request.getCategory())){
            target.setCategory(request.getCategory());
        }
        if(target.getAccount() == null || !target.getAccount().equals(request.getAccount())){
            target.setAccount(request.getAccount());
        }
        return target;
    }

    public List<Map<String,Object>> getAllCompany(){
        List<Map<String,Object>> result = new ArrayList<>();
        List<CompanyDto> companyDtos = companyRepository.findAllCompanyIdAndCompanyNamesOrderByOrderNum();
        for(CompanyDto dto : companyDtos){
            LinkedHashMap<String, Object> companyList = new LinkedHashMap<>();
            companyList.put("companyId", dto.getCompanyId());
            companyList.put("companyName", dto.getCompanyName());
            result.add(companyList);
        }
        return result;
    }

    public List<Map<String,String>> getCategoryList(){
        List<Map<String,String>> result = new ArrayList<>();
        List<Code> categoryList = codeRepository.findByCategory("gmu_category");
        for(Code code : categoryList){
            Map<String, String> categoryMap = new HashMap<>();
            categoryMap.put("code", code.getCode());
            categoryMap.put("value", code.getValue());
            result.add(categoryMap);
        }
        return result;
    }

    public Page<RpaBilling> searchRpaBilling(String companyId, LocalDateTime startDate, LocalDateTime endDate, String searchText, Pageable pageable){
        Page<RpaBilling> rpaBillings = rpaBillingRepository.searchRpaBilling(companyId, startDate, endDate, searchText, pageable);
        return rpaBillings;
    }

}
