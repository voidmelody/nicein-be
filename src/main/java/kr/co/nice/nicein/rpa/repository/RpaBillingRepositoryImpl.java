package kr.co.nice.nicein.rpa.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;

import static kr.co.nice.nicein.people.entity.QMasterEmployee.masterEmployee;
import static kr.co.nice.nicein.rpa.entity.QRpaBilling.rpaBilling;
import kr.co.nice.nicein.rpa.entity.RpaBilling;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class RpaBillingRepositoryImpl implements RpaBillingRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    @Override
    public Page<RpaBilling> searchRpaBilling(String companyId, LocalDateTime startDate, LocalDateTime endDate, String searchText, Pageable pageable) {
        List<RpaBilling> result = queryFactory.selectFrom(rpaBilling)
                .where(filterCompany(companyId).and(afterDate(startDate)).and(beforeDate(endDate)).and(containsTaskName(searchText).or(containsTaskId(searchText))))
                .orderBy(rpaBilling.startTime.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();
        Long totalCount = queryFactory.select(rpaBilling.count()).from(rpaBilling)
                .where(filterCompany(companyId).and(afterDate(startDate)).and(beforeDate(endDate)).and(containsTaskName(searchText).or(containsTaskId(searchText))))
                .fetchOne();

        return new PageImpl<>(result, pageable, totalCount);
    }


    private BooleanExpression filterCompany(String companyId){
        if(StringUtils.isNullOrEmpty(companyId) || companyId.equals("-1")){
            return Expressions.asBoolean(true).isTrue();  // 기본값인 true를 반환
        }
        return rpaBilling.companyId.eq(companyId);
    }

    private BooleanExpression afterDate(LocalDateTime startDate){
        if(startDate == null){
            return Expressions.asBoolean(true).isTrue();
        }
        return rpaBilling.startTime.goe(startDate);
    }

    private BooleanExpression beforeDate(LocalDateTime endDate){
        if(endDate == null){
            return Expressions.asBoolean(true).isTrue();
        }
        return rpaBilling.endTime.loe(endDate);
    }

    private BooleanExpression containsTaskName(String searchText){
        if(StringUtils.isNullOrEmpty(searchText)){
            return Expressions.asBoolean(true).isTrue();  // 기본값인 true를 반환
        }
        return rpaBilling.taskName.containsIgnoreCase(searchText);
    }
    private BooleanExpression containsTaskId(String searchText){
        if(StringUtils.isNullOrEmpty(searchText)){
            return Expressions.asBoolean(true).isTrue();  // 기본값인 true를 반환
        }
        return rpaBilling.taskId.containsIgnoreCase(searchText);
    }
}
