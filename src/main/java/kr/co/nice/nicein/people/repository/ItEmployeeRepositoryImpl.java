package kr.co.nice.nicein.people.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import static kr.co.nice.nicein.groupware.entity.QCompany.company;
import static kr.co.nice.nicein.groupware.entity.QEmployee.employee;
import static kr.co.nice.nicein.people.entity.QItEmployee.itEmployee;
import static kr.co.nice.nicein.people.entity.QItPosition.itPosition;
import static kr.co.nice.nicein.people.entity.QItTech.itTech;
import static kr.co.nice.nicein.groupware.entity.QDepartment.department;
import static kr.co.nice.nicein.groupware.entity.QHistory.history;
import static kr.co.nice.nicein.people.entity.QMasterEmployee.masterEmployee;
import static kr.co.nice.nicein.groupware.entity.QCode.code1;

import kr.co.nice.nicein.groupware.entity.Code;
import kr.co.nice.nicein.groupware.entity.Employee;
import kr.co.nice.nicein.groupware.repository.CodeRepository;
import kr.co.nice.nicein.people.dto.ItPeopleResponseDto;
import kr.co.nice.nicein.people.dto.ItPeopleRetireDto;
import kr.co.nice.nicein.people.entity.MasterEmployee;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class ItEmployeeRepositoryImpl implements ItEmployeeRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    private final CodeRepository codeRepository;
    private static final String defaultCode = "-1";
    private LocalDate now = LocalDate.now();


    @Override
    public Long countItEmployeeByWorkTypeAndCompany(String type, String companyId) {
        Long count = queryFactory.select(employee.count())
                .from(employee)
                .join(itEmployee)
                .on(employee.userId.eq(itEmployee.userId))
                .where(employee.itEmployee.itType.eq(type),
                        employee.status.eq("1"),
                        employee.itYn.eq("Y"),
                        employee.company.companyId.eq(companyId))
                .fetchOne();
        return count;
    }

    @Override
    public Long countItEmployeeByWorkType(String type) {
        Long count = queryFactory.select(employee.count())
                .from(employee)
                .join(itEmployee)
                .on(employee.userId.eq(itEmployee.userId))
                .where(employee.itEmployee.itType.eq(type),
                        employee.status.eq("1"),
                        employee.itYn.eq("Y"))
                .fetchOne();
        return count;
    }

    @Override
    public Page<MasterEmployee> searchItPeople(String companyId, String position, String tech, String text, String type, Pageable pageable) {
        OrderSpecifier<Integer> managerOrder = new CaseBuilder()
                .when(masterEmployee.managerYn.eq("Y")).then(0)
                .otherwise(1)
                .asc();
        OrderSpecifier<String> deptFullNameOrder = masterEmployee.employeeDeptFullName.asc();
        OrderSpecifier<String> usernameOrder = masterEmployee.username.asc();

        List<MasterEmployee> masterEmployees = queryFactory.selectFrom(masterEmployee)
                .where(masterEmployee.itYn.eq("Y"),
                        masterEmployee.status.eq("재직"),
                        filterCompany(companyId),
                        positionContain(position),
                        techContain(tech),
                        typeEqual(type),
                        ((userNameContain(text)).or(detailContain(text)).or(licenseContain(text)).or(noteContain(text))))
                .orderBy(deptFullNameOrder, managerOrder, usernameOrder)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();

        Long totalCount = queryFactory.select(masterEmployee.count())
                .from(masterEmployee)
                .where(masterEmployee.itYn.eq("Y"),
                        masterEmployee.status.eq("재직"),
                        filterCompany(companyId),
                        positionContain(position),
                        techContain(tech),
                        typeEqual(type),
                        ((userNameContain(text)).or(detailContain(text)).or(licenseContain(text)).or(noteContain(text))))
                .fetchOne();

        return new PageImpl<>(masterEmployees, pageable, totalCount);
    }

    @Override
    public Long countUnClassItEmployee() {
        Long count = queryFactory.select(employee.userId.count())
                .from(employee)
                .join(company).on(employee.company.eq(company))
                .leftJoin(itEmployee).on(itEmployee.userId.eq(employee.userId))
                .where(employee.itYn.eq("Y"),
                        itEmployee.userId.isNull(),
                        employee.status.eq("1"))
                .fetchOne();
        return count;
    }

    @Override
    public Long countUnClassItEmployeeByCompany(String companyId) {
        Long count = queryFactory.select(employee.userId.count())
                .from(employee)
                .join(company).on(employee.company.eq(company))
                .leftJoin(itEmployee).on(itEmployee.userId.eq(employee.userId))
                .where(employee.itYn.eq("Y"),
                        itEmployee.userId.isNull(),
                        employee.company.companyId.eq(companyId),
                        employee.status.eq("1"))
                .fetchOne();
        return count;
    }

    @Override
    public List<Employee> getExcel() {
        List<Employee> result = queryFactory.selectFrom(employee)
                .join(itEmployee)
                .on(employee.itEmployee.eq(itEmployee))
                .join(company)
                .on(employee.company.eq(company))
                .join(department)
                .on(employee.department.eq(department))
                .fetch();
        return result;
    }

    @Override
    public Long countRetireItEmployeeByWorkTypeAndCompany(String type, String companyId) {
        Long count = queryFactory.select(employee.count())
                .from(employee)
                .join(itEmployee)
                .on(employee.userId.eq(itEmployee.userId))
                .where(itEmployee.itType.eq(type),
                        employee.company.companyId.eq(companyId),
                        (employee.status.eq("0").or(itEmployee.cmpEnd.before(now.plusMonths(1)))))
                .fetchOne();
        return count;
    }

    @Override
    public Long countRetireItEmployeeByWorkType(String type) {
        Long count = queryFactory.select(employee.count())
                .from(employee)
                .join(itEmployee)
                .on(employee.userId.eq(itEmployee.userId))
                .where(itEmployee.itType.eq(type),
                        (employee.status.eq("0").or(itEmployee.cmpEnd.before(now.plusMonths(1)))))
                .fetchOne();
        return count;
    }

    @Override
    public Long countRetireItEmployeeByCompany(String companyId) {
        Long count = queryFactory.select(employee.count())
                .from(employee)
                .join(itEmployee)
                .on(employee.userId.eq(itEmployee.userId))
                .where(employee.company.companyId.eq(companyId),
                        (employee.status.eq("0").or(itEmployee.cmpEnd.before(now.plusMonths(1)))))
                .fetchOne();
        return count;
    }

    @Override
    public Long countAllRetireItEmployee() {
        Long count = queryFactory.select(employee.count())
                .from(employee)
                .join(itEmployee)
                .on(employee.userId.eq(itEmployee.userId))
                .where(employee.status.eq("0").or(itEmployee.cmpEnd.before(now.plusMonths(1))))
                .fetchOne();
        return count;
    }

    @Override
    public Page<ItPeopleRetireDto> getRetireItEmployeeByCompany(String companyId, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();
        BooleanBuilder statusOrCmpEnd = new BooleanBuilder();

        if(!StringUtils.isNullOrEmpty(companyId)){
            builder.and(company.companyId.eq(companyId));
        }
        statusOrCmpEnd.or(employee.status.eq("0"));
        statusOrCmpEnd.or(itEmployee.cmpEnd.before(now.plusMonths(1)));
        builder.and(statusOrCmpEnd);

        List<ItPeopleRetireDto> result = queryFactory.select(Projections.constructor(ItPeopleRetireDto.class,
                        employee.userId,
                        employee.company.companyName,
                        employee.department.deptName,
                        employee.username,
                        queryFactory.select(code1.value)
                                .from(code1)
                                .where(code1.category.eq("it_type"),
                                        itEmployee.itType.eq(code1.code)),
                        itEmployee.cmpEnd,
                        itEmployee.cmpEndReason
                ))
                .from(employee)
                .join(employee.itEmployee, itEmployee)
                .leftJoin(employee.company, company)
                .leftJoin(employee.department, department)
                .where(builder)
                .orderBy(itEmployee.cmpEnd.desc().nullsLast())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();

        Long totalCount = queryFactory.select(employee.count())
                .from(employee)
                .join(employee.itEmployee, itEmployee)
                .leftJoin(employee.company, company)
                .leftJoin(employee.department, department)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(result, pageable, totalCount);
    }

    @Override
    public Page<ItPeopleRetireDto> searchRetireItEmployee(String companyId, LocalDate startDate, LocalDate endDate, String searchText, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();
        BooleanBuilder statusOrCmpEnd = new BooleanBuilder();
        if(!StringUtils.isNullOrEmpty(companyId)){
            builder.and(company.companyId.eq(companyId));
        }
        if(!StringUtils.isNullOrEmpty(searchText)){
            builder.and(employee.username.containsIgnoreCase(searchText));
        }
        builder.and(itEmployee.cmpEnd.between(startDate,endDate));
        statusOrCmpEnd.or(employee.status.eq("0"));
        statusOrCmpEnd.or(itEmployee.cmpEnd.before(now.plusMonths(1)));
        builder.and(statusOrCmpEnd);

        List<ItPeopleRetireDto> result = queryFactory.select(Projections.constructor(ItPeopleRetireDto.class,
                        employee.userId,
                        employee.company.companyName,
                        employee.department.deptName,
                        employee.username,
                        queryFactory.select(code1.value)
                                .from(code1)
                                .where(code1.category.eq("it_type"),
                                        itEmployee.itType.eq(code1.code)),
                        itEmployee.cmpEnd,
                        itEmployee.cmpEndReason
                ))
                .from(employee)
                .join(employee.itEmployee, itEmployee)
                .leftJoin(employee.company, company)
                .leftJoin(employee.department, department)
                .where(builder)
                .orderBy(itEmployee.cmpEnd.desc().nullsLast())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();

        Long totalCount = queryFactory.select(employee.count())
                .from(employee)
                .join(employee.itEmployee, itEmployee)
                .leftJoin(employee.company, company)
                .leftJoin(employee.department, department)
                .where(builder)
                .fetchOne();
        return new PageImpl<>(result, pageable, totalCount);
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

    public String getTypeValueByCode(String typeCode){
        List<Code> typeList = codeRepository.findByCategory("it_type");
        for(Code c : typeList){
            if(c.getCode().equals(typeCode)){
                return c.getValue();
            }
        }
        return null;
    }

    private String getPositionValueByCode(String positionCode){
        List<Code> positionList = codeRepository.findByCategory("it_position");
        for(Code c : positionList){
            if(c.getCode().equals(positionCode)){
                return c.getValue();
            }
        }
        return null;
    }
    private String getTechValueByCode(String techCode){
        List<Code> techList = codeRepository.findByCategory("it_tech");
        for(Code c : techList){
            if(c.getCode().equals(techCode)){
                return c.getValue();
            }
        }
        return null;
    }


    private BooleanExpression filterCompany(String companyId){
        if(StringUtils.isNullOrEmpty(companyId)){
            return Expressions.asBoolean(true).isTrue();  // 기본값인 true를 반환
        }
        return masterEmployee.companyId.eq(companyId);
    }

    private BooleanExpression positionContain(String positionCode){
        if(StringUtils.isNullOrEmpty(positionCode) || positionCode.equals(defaultCode)){
            return Expressions.asBoolean(true).isTrue();  // 기본값인 true를 반환
        }
        return masterEmployee.itPosition.containsIgnoreCase(getPositionValueByCode(positionCode));
    }

    private BooleanExpression techContain(String techCode){
        if(StringUtils.isNullOrEmpty(techCode) || techCode.equals(defaultCode)){
            return Expressions.asBoolean(true).isTrue();  // 기본값인 true를 반환
        }
        return masterEmployee.itTech.containsIgnoreCase(getTechValueByCode(techCode));
    }

    private BooleanExpression userNameContain(String text) {
        if (StringUtils.isNullOrEmpty(text)) {
            return Expressions.asBoolean(true).isTrue();  // 기본값인 true를 반환
        }
        return masterEmployee.username.containsIgnoreCase(text);
    }

    private BooleanExpression detailContain(String text) {
        if (StringUtils.isNullOrEmpty(text)) {
            return Expressions.asBoolean(true).isTrue();  // 기본값인 true를 반환
        }
        return masterEmployee.detail.containsIgnoreCase(text);
    }

    private BooleanExpression licenseContain(String text) {
        if (StringUtils.isNullOrEmpty(text)) {
            return Expressions.asBoolean(true).isTrue();  // 기본값인 true를 반환
        }
        return masterEmployee.license.containsIgnoreCase(text);
    }

    private BooleanExpression noteContain(String text) {
        if (StringUtils.isNullOrEmpty(text)) {
            return Expressions.asBoolean(true).isTrue();  // 기본값인 true를 반환
        }
        return masterEmployee.note.containsIgnoreCase(text);
    }

    private BooleanExpression typeEqual(String type){
        if(StringUtils.isNullOrEmpty(type) || type.equals(defaultCode)){
            return Expressions.asBoolean(true).isTrue();
        }
        return masterEmployee.itType.eq(getTypeValueByCode(type));
    }
}
