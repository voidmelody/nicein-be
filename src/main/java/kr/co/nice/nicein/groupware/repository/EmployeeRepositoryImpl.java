package kr.co.nice.nicein.groupware.repository;


import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.*;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.nice.nicein.groupware.entity.Department;
import kr.co.nice.nicein.groupware.entity.Employee;
import kr.co.nice.nicein.groupware.entity.QDepartment;
import kr.co.nice.nicein.people.dto.PeopleResponseDto;
import kr.co.nice.nicein.people.vo.CountEmployee;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static kr.co.nice.nicein.groupware.entity.QCompany.company;
import static kr.co.nice.nicein.groupware.entity.QDepartment.department;
import static kr.co.nice.nicein.groupware.entity.QEmployee.employee;
import static kr.co.nice.nicein.people.entity.QItEmployee.itEmployee;

@RequiredArgsConstructor
public class EmployeeRepositoryImpl implements EmployeeRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private final String distinctType = "BASIC";
    private final DepartmentRepository departmentRepository;

    @Override
    public CountEmployee countEmployeeInCompanyByCompanyId(String companyId) {
        CountEmployee countEmployee = CountEmployee.builder()
                .countEmployeeInGroupware(countEmployeeInGroupware(companyId))
                .countEmployeeInManual(countEmployeeInManual(companyId))
                .countItEmployeeInGroupware(countItEmployeeInGroupware(companyId))
                .countItEmployeeInManual(countItEmployeeInManual(companyId))
                .sumItPeople(countItEmployee(companyId))
                .build();
        return countEmployee;
    }

    @Override
    public CountEmployee countEmployeeAll() {
        CountEmployee countEmployee = CountEmployee.builder()
                .countEmployeeInGroupware(countEmployeeInGroupware())
                .countEmployeeInManual(countEmployeeInManual())
                .countItEmployeeInGroupware(countItEmployeeInGroupware())
                .countItEmployeeInManual(countItEmployeeInManual())
                .sumItPeople(countItEmployee())
                .build();
        return countEmployee;
    }

    @Override
    public Page<PeopleResponseDto> searchPeople(String regCode, String deptId, String text, String itYn, Pageable pageable) {
        OrderSpecifier<Integer> managerOrder = new CaseBuilder()
                .when(employee.managerYn.eq("Y")).then(0)
                .otherwise(1)
                .asc();
        OrderSpecifier<String> deptFullNameOrder = employee.employeeDeptFullName.asc();
        OrderSpecifier<String> usernameOrder = employee.username.asc();

        List<PeopleResponseDto> searchResult = queryFactory.select(convertDto())
                .from(employee)
                .leftJoin(employee.itEmployee, itEmployee)
                .leftJoin(employee.company, company)
                .leftJoin(employee.department, department)
                .where(filterItYn(itYn),
                        employee.status.eq("1"),
                        filterRegCode(regCode),
                        containEmployeeDeptFullName(deptId),
                        (positionNameContain(text)
                                .or(userNameContain(text))
                                .or(userEmailContain(text))
                                .or(comPhoneNoContain(text))
                                .or(cellPhoneNoContain(text))
                                .or(cefBusinessCategoryContain(text))))
                .orderBy(deptFullNameOrder, managerOrder, usernameOrder)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();

        Long totalCount = queryFactory.select(employee.count())
                .from(employee)
                .leftJoin(employee.itEmployee, itEmployee)
                .leftJoin(employee.company, company)
                .leftJoin(employee.department, QDepartment.department)
                .where(filterItYn(itYn),
                        employee.status.eq("1"),
                        filterRegCode(regCode),
                        containEmployeeDeptFullName(deptId),
                        (positionNameContain(text)
                                .or(userNameContain(text))
                                .or(userEmailContain(text))
                                .or(comPhoneNoContain(text))
                                .or(cellPhoneNoContain(text))
                                .or(cefBusinessCategoryContain(text))))
                .fetchOne();

        return new PageImpl<>(searchResult, pageable, totalCount);
    }


    @Override
    public Long countAllItEmployee() {
        Long count = queryFactory.select(employee.count())
                .from(employee)
                .where(employee.itYn.eq("Y"), employee.status.eq("1"))
                .fetchOne();
        return count;
    }

    @Override
    public Long countItEmployeeByCompany(String companyId) {
        Long count = queryFactory.select(employee.count())
                .from(employee)
                .where(employee.itYn.eq("Y"),
                        employee.company.companyId.eq(companyId),
                        employee.status.eq("1"))
                .fetchOne();
        return count;
    }

    @Override
    public Employee findByUserIdJoinItEmployee(String userId) {
        Employee result = queryFactory.selectFrom(employee)
                .leftJoin(itEmployee)
                .on(employee.userId.eq(itEmployee.userId))
                .where(employee.userId.eq(userId))
                .fetchOne();
        return result;
    }

    private ConstructorExpression<PeopleResponseDto> convertDto() {
        ConstructorExpression<PeopleResponseDto> constructor = Projections.constructor(
                PeopleResponseDto.class,
                employee.userId,
                employee.company.companyName,
                employee.department.deptName,
                employee.positionName,
                employee.username,
                employee.status,
                employee.userEmail,
                employee.comPhoneNo,
                employee.cellPhoneNo,
                employee.cefBusinessCategory,
                Expressions.booleanTemplate("CASE WHEN {0} = 'Y' THEN true ELSE false END", employee.itYn),
                employee.manRegYn,
                Expressions.booleanTemplate("CASE WHEN {0} = 'Y' THEN true ELSE false END", employee.hrisId),
                employee.itEmployee
        );
        return constructor;
    }

    private BooleanExpression filterItYn(String itYn){
        if(StringUtils.isNullOrEmpty(itYn)){
            return Expressions.asBoolean(true).isTrue();
        }
        return employee.itYn.eq(itYn);
    }
    private BooleanExpression containEmployeeDeptFullName(String deptId){
        if(StringUtils.isNullOrEmpty(deptId)){
            return Expressions.asBoolean(true).isTrue();
        }
        Department employeeDepartment = departmentRepository.findById(deptId).get();
        String deptFullName = employeeDepartment.getDeptFullName();
        String companyId = employeeDepartment.getCompany().getCompanyId();
        return employee.employeeDeptFullName.containsIgnoreCase(deptFullName).and(employee.company.companyId.eq(companyId));
    }

    private BooleanExpression filterRegCode(String regCode){
        if(StringUtils.isNullOrEmpty(regCode) || regCode.equals("-1")){
            return Expressions.asBoolean(true).isTrue();
        }
        return employee.manRegYn.eq(regCode);
    }
    private BooleanExpression positionNameContain(String text){
        if(StringUtils.isNullOrEmpty(text)){
            return Expressions.asBoolean(true).isTrue();
        }
        return employee.positionName.containsIgnoreCase(text);
    }

    private BooleanExpression userNameContain(String text){
        if(StringUtils.isNullOrEmpty(text)){
            return Expressions.asBoolean(true).isTrue();
        }return employee.username.containsIgnoreCase(text);
    }

    private BooleanExpression userEmailContain(String text){
        if(StringUtils.isNullOrEmpty(text)){
            return Expressions.asBoolean(true).isTrue();
        }return employee.userEmail.containsIgnoreCase(text);
    }

    private BooleanExpression comPhoneNoContain(String text){
        if(StringUtils.isNullOrEmpty(text)){
            return Expressions.asBoolean(true).isTrue();
        }return employee.comPhoneNo.containsIgnoreCase(text);
    }

    private BooleanExpression cellPhoneNoContain(String text){
        if(StringUtils.isNullOrEmpty(text)){
            return Expressions.asBoolean(true).isTrue();
        }return employee.cellPhoneNo.containsIgnoreCase(text);
    }
    private BooleanExpression cefBusinessCategoryContain(String text){
        if(StringUtils.isNullOrEmpty(text)){
            return Expressions.asBoolean(true).isTrue();
        }
        return employee.cefBusinessCategory.containsIgnoreCase(text);
    }

    private Long countEmployeeInGroupware(){
        Long count = queryFactory.select(employee.count())
                .from(employee)
                .where(employee.manRegYn.ne("Y").or(employee.manRegYn.isNull()).and(employee.addJobType.eq(distinctType)).and(employee.status.eq("1")))
                .fetchOne();
        return count;
    }
    private Long countEmployeeInManual(){
        Long count = queryFactory.select(employee.count())
                .from(employee)
                .where(employee.manRegYn.eq("Y").and(employee.addJobType.eq(distinctType)).and(employee.status.eq("1")))
                .fetchOne();
        return count;
    }
    private Long countItEmployeeInGroupware(){
        Long count = queryFactory.select(employee.count())
                .from(employee)
                .where(employee.manRegYn.ne("Y").or(employee.manRegYn.isNull()).and(employee.itYn.eq("Y")).and(employee.addJobType.eq(distinctType)).and(employee.status.eq("1")))
                .fetchOne();
        return count;
    }
    private Long countItEmployeeInManual(){
        Long count = queryFactory.select(employee.count())
                .from(employee)
                .where(employee.manRegYn.eq("Y").and(employee.itYn.eq("Y")).and(employee.addJobType.eq(distinctType)).and(employee.status.eq("1")))
                .fetchOne();
        return count;
    }
    private Long countItEmployee(){
        Long count = queryFactory.select(employee.count())
                .from(employee)
                .where(employee.itYn.eq("Y").and(employee.addJobType.eq(distinctType)).and(employee.status.eq("1")))
                .fetchOne();
        return count;
    }
    private Long countEmployeeInGroupware(String companyId){
        Long count = queryFactory.select(employee.count())
                .from(employee)
                .where(employee.company.companyId.eq(companyId).and(employee.manRegYn.ne("Y").or(employee.manRegYn.isNull())).and(employee.addJobType.eq(distinctType)).and(employee.status.eq("1")))
                .fetchOne();
        return count;
    }
    private Long countEmployeeInManual(String companyId){
        Long count = queryFactory.select(employee.count())
                .from(employee)
                .where(employee.company.companyId.eq(companyId).and(employee.manRegYn.eq("Y")).and(employee.addJobType.eq(distinctType)).and(employee.status.eq("1")))
                .fetchOne();
        return count;
    }
    private Long countItEmployeeInGroupware(String companyId){
        Long count = queryFactory.select(employee.count())
                .from(employee)
                .where(employee.company.companyId.eq(companyId).and(employee.manRegYn.ne("Y").or(employee.manRegYn.isNull()).and(employee.itYn.eq("Y"))).and(employee.addJobType.eq(distinctType)).and(employee.status.eq("1")))
                .fetchOne();
        return count;
    }
    private Long countItEmployeeInManual(String companyId){
        Long count = queryFactory.select(employee.count())
                .from(employee)
                .where(employee.company.companyId.eq(companyId).and(employee.manRegYn.eq("Y")).and(employee.itYn.eq("Y")).and(employee.addJobType.eq(distinctType)).and(employee.status.eq("1")))
                .fetchOne();
        return count;
    }
    private Long countItEmployee(String companyId){
        Long count = queryFactory.select(employee.count())
                .from(employee)
                .where(employee.company.companyId.eq(companyId).and(employee.itYn.eq("Y")).and(employee.addJobType.eq(distinctType)).and(employee.status.eq("1")))
                .fetchOne();
        return count;
    }

}
