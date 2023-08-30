package kr.co.nice.nicein.excel.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.nice.nicein.excel.entity.Excel;
import kr.co.nice.nicein.groupware.entity.Code;
import kr.co.nice.nicein.groupware.entity.Employee;
import kr.co.nice.nicein.groupware.repository.CodeRepository;
import kr.co.nice.nicein.rpa.entity.GroupMailUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static kr.co.nice.nicein.groupware.entity.QCompany.company;
import static kr.co.nice.nicein.groupware.entity.QEmployee.employee;
import static kr.co.nice.nicein.people.entity.QItEmployee.itEmployee;
import static kr.co.nice.nicein.people.entity.QItPosition.itPosition;
import static kr.co.nice.nicein.groupware.entity.QDepartment.department;
import static kr.co.nice.nicein.rpa.entity.QRpaTask.rpaTask;
import static kr.co.nice.nicein.rpa.entity.QGroupMailUser.groupMailUser;
import static kr.co.nice.nicein.excel.entity.QExcel.excel;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
public class ExcelRepositoryImpl implements ExcelRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Excel> findByCategoryOrderByIndex(String category) {
        List<Excel> result = queryFactory.selectFrom(excel)
                .where(excel.category.eq(category))
                .orderBy(excel.index.asc())
                .fetch();
        return result;
    }
}
