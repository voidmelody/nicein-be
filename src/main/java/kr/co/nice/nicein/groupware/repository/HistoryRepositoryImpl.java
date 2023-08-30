package kr.co.nice.nicein.groupware.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.nice.nicein.manage.dto.PeopleHistoryDto;
import kr.co.nice.nicein.people.dto.HistoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import static kr.co.nice.nicein.groupware.entity.QHistory.history;
import static kr.co.nice.nicein.groupware.entity.QEmployee.employee;
import static kr.co.nice.nicein.groupware.entity.QCode.code1;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RequiredArgsConstructor
public class HistoryRepositoryImpl implements HistoryRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    private final CodeRepository codeRepository;

    @Override
    public List<HistoryDto> findHistoryByUserId(String userId) {
        List<HistoryDto> result = queryFactory
                .select(Projections.fields(HistoryDto.class,
                        employee.company.companyName,
                        employee.username,
                        ExpressionUtils.as(JPAExpressions.select(code1.value)
                                .from(code1)
                                .where(code1.category.eq("type").and(code1.code.eq(history.type))), "type"),
                        history.content,
                        history.updatedAt.as("date")
                ))
                .from(history)
                .join(employee)
                .on(employee.userId.eq(history.employee.userId))
                .where(employee.userId.eq(userId))
                .orderBy(history.updatedAt.desc())
                .fetch();
        return result;
    }

    @Override
    public Page<PeopleHistoryDto> getPeopleHistory(String type, Boolean excludeGroupWare, LocalDate startDate, LocalDate endDate, String searchText, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();
        if(!StringUtils.isNullOrEmpty(type)){
            builder.and(history.type.eq(type));
        }
        if (!StringUtils.isNullOrEmpty(searchText)) {
            builder.and(history.employee.username.containsIgnoreCase(searchText).or(history.editorId.containsIgnoreCase(searchText)));
        }
        if (!(startDate == null && endDate == null)) {
            builder.and(history.updatedAt.between(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX)));
        }

        if(excludeGroupWare != null && excludeGroupWare.equals(true)){
            builder.and(history.editorId.ne("그룹웨어"));
        }
        List<PeopleHistoryDto> peopleHistory = queryFactory
                .select(Projections.constructor(PeopleHistoryDto.class,
                        history.employee.userId,
                        history.employee.company.companyName,
                        history.employee.username,
                        ExpressionUtils.as(JPAExpressions.select(code1.value)
                                .from(code1)
                                .where(code1.category.eq("type").and(code1.code.eq(history.type))), "type"),
                        history.content,
                        history.updatedAt,
                        Expressions.cases()
                                .when(history.editorId.eq("그룹웨어")).then(history.editorId)
                                .otherwise(JPAExpressions.select(employee.company.companyName)
                                        .from(employee)
                                        .where(employee.userId.eq(history.editorId))),
                        Expressions.cases()
                                .when(history.editorId.eq("그룹웨어")).then(history.editorId)
                                .otherwise(JPAExpressions.select(employee.userEmail)
                                        .from(employee)
                                        .where(employee.userId.eq(history.editorId))),
                        Expressions.cases()
                                .when(history.editorId.eq("그룹웨어")).then(history.editorId)
                                .otherwise(JPAExpressions.select(employee.username)
                                        .from(employee)
                                        .where(employee.userId.eq(history.editorId)))))
                .from(history)
                .leftJoin(employee).on(employee.userId.eq(history.editorId).and(history.editorId.ne("그룹웨어")))
                .where(builder)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(history.updatedAt.desc())
                .fetch();


        Long totalCount = queryFactory.select(history.count())
                .from(history)
                .leftJoin(employee).on(employee.userId.eq(history.editorId).and(history.editorId.ne("그룹웨어")))
                .where(builder)
                .fetchOne();

        return new PageImpl<>(peopleHistory, pageable, totalCount);
    }

}
