package kr.co.nice.nicein.people.service;


import jakarta.transaction.Transactional;
import kr.co.nice.nicein.auth.entity.Member;
import kr.co.nice.nicein.groupware.entity.*;
import kr.co.nice.nicein.groupware.repository.*;
import kr.co.nice.nicein.people.dto.*;
import kr.co.nice.nicein.people.entity.ItEmployee;
import kr.co.nice.nicein.people.entity.ItPosition;
import kr.co.nice.nicein.people.entity.ItTech;
import kr.co.nice.nicein.people.entity.MasterEmployee;
import kr.co.nice.nicein.people.repository.ItEmployeeRepository;
import kr.co.nice.nicein.people.repository.ItPositionRepository;
import kr.co.nice.nicein.people.repository.ItTechRepository;
import kr.co.nice.nicein.people.vo.CountEmployee;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class PeopleService {
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final ItEmployeeRepository itEmployeeRepository;
    private final CodeRepository codeRepository;
    private final CompanyRepository companyRepository;
    private final ItPositionRepository itPositionRepository;
    private final ItTechRepository itTechRepository;
    private final HistoryRepository historyRepository;


    public String getCompanyDeptId(Member member){
        String companyId = null;
        if(member.getAuthOption().equals("1")){
            if(member.getTargetCompanyId() != null){
                companyId = member.getTargetCompanyId();
            }else{
                companyId = member.getEmployee().getCompany().getCompanyId();
            }
        }
        List<OrganChartResponseDto> organChartDepth0 = departmentRepository.findOrganChartDepth0(companyId);
        return organChartDepth0.get(0).getDeptId();
    }



    public String createPeople(Member member, addPeopleDto people){
        String randomUserId = generateRandomString();
        // 이미 해당 id에 유저가 존재할 경우
        if(employeeRepository.findByUserId(randomUserId).isPresent()){
            // 다시 한 번 더 만들기.
            randomUserId = generateRandomString();
        }
        Employee employee = Employee.builder()
                .userId(randomUserId)
                .addJobType("BASIC")
                .cefBusinessCategory(people.getCefBusinessCategory())
                .cellPhoneNo(people.getCellPhoneNo())
                .comPhoneNo(people.getComPhoneNo())
                .loginId(people.getUserEmail())
                .positionName(people.getPositionName())
                .status("1")
                .userEmail(people.getUserEmail())
                .username(people.getUsername())
                .company(companyRepository.findById(people.getCompanyId()).get())
                .department(departmentRepository.findById(people.getDeptId()).get())
                .itYn(convertBooleanToYn(people.getItYn()))
                .manRegYn("Y")
                .build();
        employee.setEmployeeDeptFullName(employee.getDepartment().getDeptFullName());
        employeeRepository.save(employee);
        if(people.getItYn().equals(true)){
            ItEmployee itEmployee = ItEmployee.builder()
                    .userId(randomUserId)
                    .careerStart(LocalDate.parse(people.getCareerStart()))
                    .niceStart(LocalDate.parse(people.getNiceStart()))
                    .cmpStart(LocalDate.parse(people.getCmpStart()))
                    .cmpEnd(LocalDate.parse(people.getCmpEnd()))
                    .itType(people.getType())
                    .detail(people.getDetail())
                    .license(people.getLicense())
                    .note(people.getNote())
                    .cmpEndReason(people.getCmpEndReason())
                    .build();
            itEmployeeRepository.save(itEmployee);
            savePosition(member, randomUserId, people.getPositionList());
            saveTech(randomUserId,people.getTechList());
        }

        // 신규 생성 이력 저장
        History history = History.builder()
                .employee(employee)
                .editTable("Employee")
                .updatedAt(LocalDateTime.now())
                .editorId(member.getEmployee().getUserId())
                .type(codeRepository.convertTypeValueToCode("신규"))
                .content(employee.getEmployeeDeptFullName() + " " + employee.getUsername())
                .build();
        historyRepository.save(history);
        return "OK";
    }


    public List<OrganChartResponseDto> getOrganChartDepth1(){
        List<OrganChartResponseDto> result = departmentRepository.findOrganChartDepth1ByCompany();
        return result;
    }

    public List<OrganChartResponseDto> getChildDepartmentByDeptId(String departmentId){
        if(departmentId == null){
            return null;
        }
        Department department = departmentRepository.findById(departmentId).orElseThrow();
        List<OrganChartResponseDto> childDepartment = departmentRepository.findChildDepartment(department.getDeptFullName());
        return childDepartment;
    }

    public List<OrganChartResponseDto> getChildDepartmentByCompanyId(String companyId){
        Company company = companyRepository.findById(companyId).get();
        List<OrganChartResponseDto> childDepartment = departmentRepository.findChildDepartment(company.getCompanyName());
        return childDepartment;
    }



    public Boolean updatePeopleItYn(List<peopleItYnRequestDto> peopleList){
        for(peopleItYnRequestDto people : peopleList){
            String userId = people.getUserId();
            String yn = convertBooleanToYn(people.getItYn());
            Employee employee = employeeRepository.findByUserId(userId).orElseThrow();
            if(!Objects.equals(yn, employee.getItYn())){
                History history = History.builder()
                        .employee(employee)
                        .editTable("Employee")
                        .editField("itYn")
                        .preData(employee.getItYn())
                        .chgData(yn)
                        .editorId(userId)
                        .updatedAt(LocalDateTime.now())
                        .fieldName("IT인력여부")
                        .build();
                if(employee.getItYn() == null){
                    history.setType(codeRepository.convertTypeValueToCode("신규"));
                    history.setContent("IT 인력으로 배정");
                }else{
                    history.setType(codeRepository.convertTypeValueToCode("변경"));
                    history.setContent("IT 인력에서 제외");
                }
                historyRepository.save(history);
            }
            employee.setItYn(yn);
            employeeRepository.save(employee);
        }
        return true;
    }

    public List<OrganChartResponseDto> getCompanyNames(Member member){
        String companyId = null;
        if(member != null && member.getAuthOption().equals("1")){
            if(member.getTargetCompanyId() != null){
                companyId = member.getTargetCompanyId();
            }else{
                companyId = member.getEmployee().getCompany().getCompanyId();
            }
        }
        List<OrganChartResponseDto> organChartDepth0ByCompany = departmentRepository.findOrganChartDepth0(companyId);
        return organChartDepth0ByCompany;
    }

    public PeopleResponseDto getPeopleInfo(String userId){
        Employee employee = employeeRepository.findByUserIdJoinItEmployee(userId);
        Boolean hrisYn = false;
        if(employee.getHrisId() != null){
            hrisYn = true;
        }
        PeopleResponseDto peopleResponseDto = PeopleResponseDto.builder()
                .userId(userId)
                .companyName(employee.getCompany().getCompanyName())
                .deptName(employee.getEmployeeDeptFullName())
                .positionName(employee.getPositionName())
                .username(employee.getUsername())
                .userEmail(employee.getUserEmail())
                .comPhoneNo(employee.getComPhoneNo())
                .cellPhoneNo(employee.getCellPhoneNo())
                .cefBusinessCategory(employee.getCefBusinessCategory())
                .itYn(convertYnToBoolean(employee.getItYn()))
                .hrisYn(hrisYn)
                .itEmployee(employee.getItEmployee())
                .build();
        return peopleResponseDto;
    }

    public List<PeopleStatResponseDto> getGroupMemberStat(){
        List<PeopleStatResponseDto> peopleNumbers = new ArrayList<>();
        List<OrganChartResponseDto> companyNames = getCompanyNames(null);

        PeopleStatResponseDto allGroupMemberNumber = getAllGroupMemberNumber();
        peopleNumbers.add(allGroupMemberNumber);

        for(OrganChartResponseDto company: companyNames){
            String companyId = company.getCompanyId();
            PeopleStatResponseDto companyMemberNumber = getCompanyMemberNumber(companyId);
            peopleNumbers.add(companyMemberNumber);
        }
        return peopleNumbers;
    }
    public PeopleStatResponseDto getAllGroupMemberNumber(){
        CountEmployee countEmployeeAll = employeeRepository.countEmployeeAll();
        PeopleStatResponseDto peopleNumber = PeopleStatResponseDto.builder()
                .companyName("*전체")
                .countEmployeeInGroupware(countEmployeeAll.getCountEmployeeInGroupware())
                .countEmployeeInManual(countEmployeeAll.getCountEmployeeInManual())
                .countItEmployeeInGroupware(countEmployeeAll.getCountItEmployeeInGroupware())
                .countItEmployeeInManual(countEmployeeAll.getCountItEmployeeInManual())
                .sumItPeople(countEmployeeAll.getSumItPeople())
                .build();
        return peopleNumber;
    }

    public PeopleStatResponseDto getCompanyMemberNumber(String companyId){
        CountEmployee countEmployee = employeeRepository.countEmployeeInCompanyByCompanyId(companyId);
        OrganChartResponseDto company = departmentRepository.findDeptByCompany(companyId);
        PeopleStatResponseDto peopleNumber = PeopleStatResponseDto.builder()
                .companyName(company.getCompanyName())
                .deptId(company.getDeptId())
                .countEmployeeInGroupware(countEmployee.getCountEmployeeInGroupware())
                .countEmployeeInManual(countEmployee.getCountEmployeeInManual())
                .countItEmployeeInGroupware(countEmployee.getCountItEmployeeInGroupware())
                .countItEmployeeInManual(countEmployee.getCountItEmployeeInManual())
                .sumItPeople(countEmployee.getSumItPeople())
                .build();
        return peopleNumber;
    }

    public Page<PeopleResponseDto> searchPeople(String regCode, String deptId, String text, String itYn, Pageable pageable){
        Page<PeopleResponseDto> searchResult = employeeRepository.searchPeople(regCode, deptId, text, itYn, pageable);
        for(PeopleResponseDto people : searchResult){
            String statusName = getStatusValueByCode(people.getStatus());
            people.setStatus(statusName);
        }
        return searchResult;
    }

    public List<Map<String,String>> getHrRegCodeList(){
        List<Map<String,String>> result = new ArrayList<>();
        List<Code> categoryList = codeRepository.findByCategory("man_reg_yn");
        for(Code code : categoryList){
            Map<String, String> categoryMap = new HashMap<>();
            categoryMap.put("code", code.getCode());
            categoryMap.put("value", code.getValue());
            result.add(categoryMap);
        }
        return result;
    }

    public List<Map<String,String>> getItTypeList(){
        List<Map<String,String>> result = new ArrayList<>();
        List<Code> workTypeList = codeRepository.findByCategory("it_type");
        for(Code code : workTypeList){
            Map<String, String> workTypeMap = new HashMap<>();
            workTypeMap.put("code", code.getCode());
            workTypeMap.put("value", code.getValue());
            result.add(workTypeMap);
        }
        return result;
    }
    public List<Map<String,String>> getPositionList(){
        List<Map<String,String>> result = new ArrayList<>();
        List<Code> positionList = codeRepository.getPositionList();
        for(Code position : positionList){
            Map<String, String> map = new HashMap<>();
            map.put("code", position.getCode());
            map.put("value", position.getValue());
            result.add(map);
        }
        return result;
    }

    public List<Map<String,String>> getTechList(){
        List<Map<String,String>> result = new ArrayList<>();
        List<Code> techList = codeRepository.getTechList();
        for(Code tech : techList){
            Map<String, String> map = new HashMap<>();
            map.put("code", tech.getCode());
            map.put("value", tech.getValue());
            result.add(map);
        }
        return result;
    }

    public List<List<Map<String,String>>> getTechListOrderByCode(){
        List<List<Map<String,String>>> result = new ArrayList<>();
        List<List<Code>> techList = codeRepository.getTechListOrderByCode();
        for(int i = 0; i < techList.size(); i++) {
            List<Map<String,String>> categoryTech = new ArrayList<>();
            for (Code tech : techList.get(i)) {
                Map<String, String> map = new HashMap<>();
                map.put("code", tech.getCode());
                map.put("value", tech.getValue());
                categoryTech.add(map);
            }
            result.add(categoryTech);
        }
        return result;
    }

    public List<String> getTechDescList(){
        List<String> techDescList = codeRepository.getTechDescList();
        return techDescList;
    }

    public ItPeopleStatResponseDto getCompanyItMemberNumber(String companyId){
        Long countCode1 = itEmployeeRepository.countItEmployeeByWorkTypeAndCompany("1", companyId);
        Long countCode10 = itEmployeeRepository.countItEmployeeByWorkTypeAndCompany("10", companyId);
        Long countCode21 = itEmployeeRepository.countItEmployeeByWorkTypeAndCompany("21", companyId);
        Long countCode22 = itEmployeeRepository.countItEmployeeByWorkTypeAndCompany("22", companyId);
        Long countCode23 = itEmployeeRepository.countItEmployeeByWorkTypeAndCompany("23", companyId);
        Long countCode25 = itEmployeeRepository.countItEmployeeByWorkTypeAndCompany("25", companyId);
        Long countCode26 = itEmployeeRepository.countItEmployeeByWorkTypeAndCompany("26", companyId);
        Long unClassCount = itEmployeeRepository.countUnClassItEmployeeByCompany(companyId);

        Long countAll = employeeRepository.countItEmployeeByCompany(companyId);

        ItPeopleStatResponseDto result = ItPeopleStatResponseDto.builder()
                .companyId(companyId)
                .companyName(companyRepository.findById(companyId).get().getCompanyName())
                .code1(countCode1)
                .code10(countCode10)
                .code21(countCode21)
                .code22(countCode22)
                .code23(countCode23)
                .code25(countCode25)
                .code26(countCode26)
                .subTotal(countCode21+countCode22+countCode23+countCode25+countCode26)
                .unClass(unClassCount)
                .all(countAll)
                .build();
        return result;
    }

    public ItPeopleStatResponseDto getAllItMemberNumber(){
        Long countCode1 = itEmployeeRepository.countItEmployeeByWorkType("1");
        Long countCode10 =itEmployeeRepository.countItEmployeeByWorkType("10");
        Long countCode21 = itEmployeeRepository.countItEmployeeByWorkType("21");
        Long countCode22 = itEmployeeRepository.countItEmployeeByWorkType("22");
        Long countCode23 = itEmployeeRepository.countItEmployeeByWorkType("23");
        Long countCode25 = itEmployeeRepository.countItEmployeeByWorkType("25");
        Long countCode26 = itEmployeeRepository.countItEmployeeByWorkType("26");
        Long unClassCount = itEmployeeRepository.countUnClassItEmployee();

        Long countAll = employeeRepository.countAllItEmployee();

        ItPeopleStatResponseDto result = ItPeopleStatResponseDto.builder()
                .companyName("*전체")
                .companyId(null)
                .code1(countCode1)
                .code10(countCode10)
                .code21(countCode21)
                .code22(countCode22)
                .code23(countCode23)
                .code25(countCode25)
                .code26(countCode26)
                .subTotal(countCode21+countCode22+countCode23+countCode25+countCode26)
                .unClass(unClassCount)
                .all(countAll)
                .build();
        return result;
    }

    public List<ItPeopleStatResponseDto> getGroupItMemberNumber(){
        List<ItPeopleStatResponseDto> result = new ArrayList<>();
        ItPeopleStatResponseDto allItMemberNumber = getAllItMemberNumber();
        result.add(allItMemberNumber);

        List<OrganChartResponseDto> companyNames = getCompanyNames(null);
        for(OrganChartResponseDto company : companyNames) {
            ItPeopleStatResponseDto companyItMemberNumber = getCompanyItMemberNumber(company.getCompanyId());
            result.add(companyItMemberNumber);
        }
        return result;
    }

    public Page<MasterEmployee> searchItPeople(String companyId, String type, String position, String tech, String text, Pageable pageable){
        Page<MasterEmployee> searchItPeopleList;
        searchItPeopleList = itEmployeeRepository.searchItPeople(companyId, position, tech, text, type, pageable);

        return searchItPeopleList;
    }

    public String getStatusValueByCode(String statusCode){
        List<Code> statusList = codeRepository.findByCategory("status");
        for(Code c : statusList){
            if(c.getCode().equals(statusCode)){
                return c.getValue();
            }
        }
        return null;
    }

    public String savePeopleInfo(Member member, ItPeopleSaveRequestDto dto, Boolean itYn){
        String userId = dto.getUserId();
        Employee employee = employeeRepository.findByUserId(userId).get();
        if(itYn.equals(false)){
            employee.setItYn("N");
        }else{
            employee.setItYn("Y");
            ItEmployee itEmployee;
            Optional<ItEmployee> itEmployeeOptional = itEmployeeRepository.findById(userId);
            if(itEmployeeOptional.isPresent()){
                itEmployee = itEmployeeOptional.get();
            }else{
                itEmployee = new ItEmployee();
                itEmployee.setUserId(userId);
            }
            itEmployee.setDetail(dto.getDetail());
            itEmployee.setLicense(dto.getLicense());
            itEmployee.setItType(dto.getItType());
            itEmployee.setCareerStart(dto.getCareerStart());
            itEmployee.setCmpEndReason(dto.getCmpEndReason());
            itEmployeeRepository.save(itEmployee);
            savePosition(member, userId, dto.getPositionList());
            saveTech(userId, dto.getTechList());
        }
        return "OK";
    }

    public void savePosition(Member member, String userId, List<String> positionList){
        List<String> userPositionsCode = getPeoplePositionsCode(userId);
        List<String> requestPositionsCode = positionList;

        List<String> userPositionsValue = userPositionsCode.stream().map(codeRepository::convertItPositionCodeToValue).toList();
        List<String> requestPositionValue = requestPositionsCode.stream().map(codeRepository::convertItPositionCodeToValue).toList();

        if(userPositionsCode.equals(requestPositionsCode)){
            return;
        }else{
            String preData = String.join(", ", userPositionsValue);
            String chgData = String.join(", ", requestPositionValue);
            History history = History.builder()
                    .employee(employeeRepository.findByUserId(userId).get())
                    .editTable("ItPosition")
                    .editField("code")
                    .preData(preData.isEmpty() ? null : preData)
                    .chgData(chgData)
                    .updatedAt(LocalDateTime.now())
                    .editorId(member.getEmployee().getUserId())
                    .fieldName("직무")
                    .type(codeRepository.convertTypeValueToCode("변경")) // 변경 코드
                    .build();
            if(preData.isEmpty()){
                history.setContent("직무: " + history.getChgData());
            }else {
                history.setContent("직무: " + history.getPreData() + " → " + history.getChgData());
            }

            historyRepository.save(history);

            List<ItPosition> updatePositions = new ArrayList<>();
            itPositionRepository.deleteByUserId(userId);
            for(int i = 0; i < requestPositionsCode.size(); i++){
                ItPosition itPosition = ItPosition.builder()
                        .userId(userId)
                        .code(requestPositionsCode.get(i))
                        .build();
                updatePositions.add(itPosition);
            }
            itPositionRepository.saveAll(updatePositions);
        }
    }
    public void saveTech(String userId, List<String> techList){
        List<String> peopleTechsCode = getPeopleTechsCode(userId);
        List<String> requestTechsCode = techList;

        if(peopleTechsCode.equals(requestTechsCode)){
            return;
        }else{
            List<ItTech> updateTechs = new ArrayList<>();
            itTechRepository.deleteByUserId(userId);
            for(int i = 0; i < requestTechsCode.size(); i++){
                ItTech itTech = ItTech.builder()
                        .userId(userId)
                        .code(requestTechsCode.get(i))
                        .build();
                updateTechs.add(itTech);
            }
            itTechRepository.saveAll(updateTechs);
        }
    }
    public List<String> getPeoplePositionsCode(String userId){
        List<String> userPositionsCode = itPositionRepository.findPositionsCodeByUserId(userId);
        return userPositionsCode;
    }

    public List<String> getPeopleTechsCode(String userId){
        List<String> userTechsCode = itTechRepository.findTechsCodeByUserId(userId);
        return userTechsCode;
    }

    public List<HistoryDto> getPeopleHistory(String userId){
        List<HistoryDto> historyByUserId = historyRepository.findHistoryByUserId(userId);
        return historyByUserId;
    }


    public ItPeopleStatResponseDto getCompanyRetireItMemberNumber(String companyId){
        Long countCode1 = itEmployeeRepository.countRetireItEmployeeByWorkTypeAndCompany("1", companyId);
        Long countCode10 = itEmployeeRepository.countRetireItEmployeeByWorkTypeAndCompany("10", companyId);
        Long countCode21 = itEmployeeRepository.countRetireItEmployeeByWorkTypeAndCompany("21", companyId);
        Long countCode22 = itEmployeeRepository.countRetireItEmployeeByWorkTypeAndCompany("22", companyId);
        Long countCode23 = itEmployeeRepository.countRetireItEmployeeByWorkTypeAndCompany("23", companyId);
        Long countCode25 = itEmployeeRepository.countRetireItEmployeeByWorkTypeAndCompany("25", companyId);
        Long countCode26 = itEmployeeRepository.countRetireItEmployeeByWorkTypeAndCompany("26", companyId);


        Long countAll = itEmployeeRepository.countRetireItEmployeeByCompany(companyId);

        ItPeopleStatResponseDto result = ItPeopleStatResponseDto.builder()
                .companyId(companyId)
                .companyName(companyRepository.findById(companyId).get().getCompanyName())
                .code1(countCode1)
                .code10(countCode10)
                .code21(countCode21)
                .code22(countCode22)
                .code23(countCode23)
                .code25(countCode25)
                .code26(countCode26)
                .subTotal(countCode21+countCode22+countCode23+countCode25+countCode26)
                .all(countAll)
                .build();
        return result;
    }

    public ItPeopleStatResponseDto getAllRetireItMemberNumber(){
        Long countCode1 = itEmployeeRepository.countRetireItEmployeeByWorkType("1");
        Long countCode10 =itEmployeeRepository.countRetireItEmployeeByWorkType("10");
        Long countCode21 = itEmployeeRepository.countRetireItEmployeeByWorkType("21");
        Long countCode22 = itEmployeeRepository.countRetireItEmployeeByWorkType("22");
        Long countCode23 = itEmployeeRepository.countRetireItEmployeeByWorkType("23");
        Long countCode25 = itEmployeeRepository.countRetireItEmployeeByWorkType("25");
        Long countCode26 = itEmployeeRepository.countRetireItEmployeeByWorkType("26");

        Long countAll = itEmployeeRepository.countAllRetireItEmployee();

        ItPeopleStatResponseDto result = ItPeopleStatResponseDto.builder()
                .companyName("*전체")
                .companyId("-1")
                .code1(countCode1)
                .code10(countCode10)
                .code21(countCode21)
                .code22(countCode22)
                .code23(countCode23)
                .code25(countCode25)
                .code26(countCode26)
                .subTotal(countCode21+countCode22+countCode23+countCode25+countCode26)
                .all(countAll)
                .build();
        return result;
    }

    public List<ItPeopleStatResponseDto> getGroupRetireItMemberNumber(){
        List<ItPeopleStatResponseDto> result = new ArrayList<>();
        ItPeopleStatResponseDto allItMemberNumber = getAllRetireItMemberNumber();
        result.add(allItMemberNumber);

        List<OrganChartResponseDto> companyNames = getCompanyNames(null);
        for(OrganChartResponseDto company : companyNames) {
            ItPeopleStatResponseDto companyRetireItMemberNumber = getCompanyRetireItMemberNumber(company.getCompanyId());
            result.add(companyRetireItMemberNumber);
        }
        return result;
    }


    public Page<ItPeopleRetireDto> getCompanyRetireItPeople(String companyId, Pageable pageable){
        Page<ItPeopleRetireDto> retireItEmployeeByCompany = itEmployeeRepository.getRetireItEmployeeByCompany(companyId, pageable);
        return retireItEmployeeByCompany;
    }

    public Page<ItPeopleRetireDto> searchRetireItPeople(String companyId, LocalDate startDate, LocalDate endDate, String searchText, Pageable pageable){
        Page<ItPeopleRetireDto> searchRetireItEmployee = itEmployeeRepository.searchRetireItEmployee(companyId, startDate, endDate, searchText, pageable);
        return searchRetireItEmployee;

    }

    public String saveCmpEndReason(String userId, String cmpEndReason){
        ItEmployee itEmployee = itEmployeeRepository.findById(userId).get();


        if(itEmployee.getCmpEndReason() != null && itEmployee.getCmpEndReason().equals(cmpEndReason)){
            return "NOT CHANGED";
        }
        History history = History.builder()
                .employee(employeeRepository.findByUserId(userId).get())
                .editTable("ItEmployee")
                .editField("cmpEndReason")
                .preData(itEmployee.getCmpEndReason())
                .chgData(cmpEndReason)
                .updatedAt(LocalDateTime.now())
                .editorId(userId)
                .fieldName("재직상태")
                .type(codeRepository.convertTypeValueToCode("변경"))
                .content("퇴사 사유 변경: " + itEmployee.getCmpEndReason() + " → " + cmpEndReason)
                .build();
        historyRepository.save(history);
        itEmployee.setCmpEndReason(cmpEndReason);
        return "OK";
    }

    public static String generateRandomString() {
        try {
            // 난수 생성기 생성
            SecureRandom random = new SecureRandom();

            // 난수 생성
            byte[] randomBytes = new byte[16];
            random.nextBytes(randomBytes);

            // SHA-512 해시 생성
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            byte[] hashBytes = digest.digest(randomBytes);

            // 해시값을 16진수 문자열로 변환
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            // 문자열 반환
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Boolean convertYnToBoolean(String yn){
        if(yn == null || !yn.equals("Y")){
            return false;
        }
        return true;
    }

    public String convertBooleanToYn(Boolean yn){
        if(yn == true){
            return "Y";
        }
        return null;
    }

}
