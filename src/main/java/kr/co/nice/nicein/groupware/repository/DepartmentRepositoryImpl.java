package kr.co.nice.nicein.groupware.repository;

import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import static kr.co.nice.nicein.groupware.entity.QDepartment.department;
import static kr.co.nice.nicein.groupware.entity.QCompany.company;
import static kr.co.nice.nicein.people.entity.QMasterEmployee.masterEmployee;

import kr.co.nice.nicein.groupware.entity.Department;
import kr.co.nice.nicein.people.dto.OrganChartResponseDto;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor

public class DepartmentRepositoryImpl implements DepartmentRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public List<OrganChartResponseDto> findChildDepartment(String deptName) {
        List<OrganChartResponseDto> result = jpaQueryFactory.select(convertDto())
                .from(department)
                .join(company)
                .on(department.company.eq(company))
                .where(department.deptFullName.containsIgnoreCase(deptName).and(department.useYn.eq("Y")).and(company.useYn.eq("Y")))
                .orderBy(department.depth.asc(), department.sortNo.asc())
                .fetch();
        return result;
    }

    @Override
    public List<OrganChartResponseDto> findOrganChartDepth1ByCompany() {
        List<OrganChartResponseDto> result = jpaQueryFactory.select(convertDto())
                .from(department)
                .join(company)
                .on(department.company.eq(company))
                .where(department.depth.eq(1).and(department.useYn.eq("Y")).and(company.useYn.eq("Y")))
                .orderBy(company.orderNum.asc(), department.sortNo.asc())
                .fetch();
        return result;
    }

    @Override
    public String getCompanyIdByDeptId (String deptId) {
        String companyId = jpaQueryFactory.select(department.company.companyId)
                .from(department)
                .where(department.deptId.eq(deptId).and(department.useYn.eq("Y")).and(company.useYn.eq("Y")))
                .fetchOne();
        return companyId;
    }

    @Override
    public OrganChartResponseDto findDeptByCompany(String companyId) {
        OrganChartResponseDto organChartResponseDto = jpaQueryFactory.select(convertDto())
                .from(department)
                .where(department.company.companyId.eq(companyId).and(department.depth.eq(0)).and(department.useYn.eq("Y")).and(company.useYn.eq("Y")))
                .fetchOne();
        return organChartResponseDto;
    }

    @Override
    public List<OrganChartResponseDto> findOrganChartDepth0(String companyId) {
        List<OrganChartResponseDto> result = jpaQueryFactory.select(convertDto())
                .from(department)
                .join(company)
                .on(department.company.eq(company))
                .where(department.depth.eq(0), filterCompany(companyId), department.useYn.eq("Y"), company.useYn.eq("Y"))
                .orderBy(company.orderNum.asc(), department.sortNo.asc())
                .fetch();
        return result;
    }


    public ConstructorExpression<OrganChartResponseDto> convertDto(){
        ConstructorExpression<OrganChartResponseDto> dto = Projections.constructor(OrganChartResponseDto.class,
                company.companyId,
                company.companyName,
                department.deptId,
                department.deptName
        );
        return dto;
    }

    private BooleanExpression filterCompany(String companyId){
        if(StringUtils.isNullOrEmpty(companyId)){
            return Expressions.asBoolean(true).isTrue();  // 기본값인 true를 반환
        }
        return company.companyId.eq(companyId);
    }
}
