package kr.co.nice.nicein.excel.service;

import jakarta.transaction.Transactional;
import kr.co.nice.nicein.auth.entity.Member;
import kr.co.nice.nicein.excel.entity.Excel;
import kr.co.nice.nicein.excel.entity.ExcelEmployee;
import kr.co.nice.nicein.excel.entity.ExcelRpa;
import kr.co.nice.nicein.excel.repository.ExcelEmployeeRepository;
import kr.co.nice.nicein.excel.repository.ExcelRepository;
import kr.co.nice.nicein.excel.repository.ExcelRpaRepository;
import kr.co.nice.nicein.groupware.entity.Employee;
import kr.co.nice.nicein.groupware.repository.CompanyRepository;
import kr.co.nice.nicein.manage.entity.AccountHistory;
import kr.co.nice.nicein.manage.repository.AccountHistoryRepository;
import kr.co.nice.nicein.people.entity.ItPosition;
import kr.co.nice.nicein.people.service.PeopleService;
import kr.co.nice.nicein.rpa.entity.GroupMailUser;
import kr.co.nice.nicein.security.Authority;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ExcelService {
    private final ExcelRepository excelRepository;
    private final ExcelEmployeeRepository excelEmployeeRepository;
    private final ExcelRpaRepository excelRpaRepository;
    private final AccountHistoryRepository accountHistoryRepository;
    private final CompanyRepository companyRepository;

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd");

    public List<Map<String,Object>> getItData(Authority memberRole, String companyId) {
        List<Map<String,Object>> result = new ArrayList<>();
        List<Excel> itEmployeeDataList = excelRepository.findByCategoryOrderByIndex("it_employee");
        List<ExcelEmployee> employeeList = excelEmployeeRepository.findAllNotNullItPosition(companyId);

        // Role이 ADMIN이 아니면 user_id 필드가 없어야함.
        if(!memberRole.equals(Authority.ROLE_ADMIN)){
            itEmployeeDataList = itEmployeeDataList.stream().filter(i -> !i.getField().equals("user_id")).toList();
        }

        for (ExcelEmployee excelEmployee : employeeList) {
            Map<String, Object> data = new LinkedHashMap<>();
            for (Excel excel : itEmployeeDataList) {
                String columnName = excel.getColumnName();
                Object fieldValue = getFieldValue(excelEmployee, columnName);
                data.put(excel.getField(), fieldValue);
            }
            result.add(data);
        }
        return result;
    }

    public List<Map<String,Object>> getRpaData(){
        List<Map<String,Object>> result = new ArrayList<>();
        List<Excel> rpaEmployeeDataList = excelRepository.findByCategoryOrderByIndex("RPA");
        List<ExcelRpa> rpaList = excelRpaRepository.getExcelRpaDataList();

        // test
        for(ExcelRpa rpa : rpaList){
            System.out.println(rpa.getUsername());
        }

        for(ExcelRpa rpa : rpaList){
            Map<String, Object> data = new LinkedHashMap<>();
            for(Excel excel : rpaEmployeeDataList){
                String columnName = excel.getColumnName();
                Object fieldValue = getFieldValue(rpa, columnName);
                data.put(excel.getField(), fieldValue);
            }
            result.add(data);
        }
        return result;
    }

    private Object getFieldValue(Object table, String columnName) {
        Object value = null;
        try {
            // table 객체에서 columnName에 해당하는 필드를 찾음
            Field field = table.getClass().getDeclaredField(columnName);
            // 접근 권한이 없는 경우 접근 가능하도록 설정
            field.setAccessible(true);
            // 해당 필드의 값을 가져옴
            value = field.get(table);
            if(value == null){
                return value;
            }
            if(field.getType().equals(Date.class)){
                value = formatter.format(value);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return value;
    }

    public List<Excel> getItEmployeeExcelInfo(){
        List<Excel> excelInfoList = excelRepository.findByCategoryOrderByIndex("it_employee");
        return excelInfoList;
    }

    public List<Excel> getRpaExcelInfo(){
        List<Excel> rpaExcelInfo = excelRepository.findByCategoryOrderByIndex("RPA");
        return rpaExcelInfo;
    }

    public void saveExcelDownHistory(Member member, String fileName, String companyId){
        if(companyId == null){
            fileName = "(취합본)그룹" + fileName;
        }else{
            fileName = "(" + companyRepository.findById(companyId).get().getCompanyName() + ")" + fileName;
        }
        AccountHistory accountHistory = AccountHistory.builder()
                .category("excel_history")
                .adminUserId(member.getEmployee().getUserId())
                .content(fileName)
                .timestamp(LocalDateTime.now())
                .build();
        accountHistoryRepository.save(accountHistory);
    }

}
