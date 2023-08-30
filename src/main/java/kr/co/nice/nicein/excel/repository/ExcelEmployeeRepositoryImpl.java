package kr.co.nice.nicein.excel.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.nice.nicein.excel.entity.ExcelEmployee;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static kr.co.nice.nicein.excel.entity.QExcelEmployee.excelEmployee;
import static kr.co.nice.nicein.people.entity.QMasterEmployee.masterEmployee;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class ExcelEmployeeRepositoryImpl implements ExcelEmployeeRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    @Override
    public List<ExcelEmployee> findAllNotNullItPosition(String companyId) {
        List<ExcelEmployee> result = queryFactory.selectFrom(excelEmployee)
                .where(excelEmployee.itYn.eq("Y"),
                        excelEmployee.status.eq("1"),
                        filterCompany(companyId))
                .orderBy(excelEmployee.companyId.asc(), excelEmployee.employeeDeptFullName.asc(),excelEmployee.username.asc())
                .fetch();
        return result;
    }

    private BooleanExpression filterCompany(String companyId){
        if(StringUtils.isNullOrEmpty(companyId)){
            return Expressions.asBoolean(true).isTrue();  // 기본값인 true를 반환
        }
        return excelEmployee.companyId.eq(companyId);
    }
}
