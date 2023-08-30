package kr.co.nice.nicein.excel.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.nice.nicein.excel.entity.ExcelRpa;

import static kr.co.nice.nicein.excel.entity.QExcelRpa.excelRpa;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ExcelRpaRepositoryImpl implements ExcelRpaRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    @Override
    public List<ExcelRpa> getExcelRpaDataList() {
        List<ExcelRpa> result = queryFactory.selectFrom(excelRpa)
                .where(excelRpa.useYn.eq("Y"))
                .orderBy(excelRpa.taskId.asc(), excelRpa.companyId.asc(), excelRpa.employeeDeptFullName.asc(), excelRpa.username.asc())
                .fetch();
        return result;
    }
}
