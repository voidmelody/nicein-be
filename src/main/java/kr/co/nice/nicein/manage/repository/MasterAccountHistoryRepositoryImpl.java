package kr.co.nice.nicein.manage.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.nice.nicein.groupware.entity.History;
import kr.co.nice.nicein.manage.entity.MasterAccountHistory;
import static kr.co.nice.nicein.manage.entity.QMasterAccountHistory.masterAccountHistory;
import static kr.co.nice.nicein.groupware.entity.QHistory.history;
import static kr.co.nice.nicein.people.entity.QItEmployee.itEmployee;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


@RequiredArgsConstructor
public class MasterAccountHistoryRepositoryImpl implements MasterAccountHistoryRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<MasterAccountHistory> getAccountHistory(LocalDate startDate, LocalDate endDate,String searchText, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();
        if (!StringUtils.isNullOrEmpty(searchText)) {
            builder.and(masterAccountHistory.username.containsIgnoreCase(searchText).or(masterAccountHistory.userAccount.containsIgnoreCase(searchText)));
        }
        if (!(startDate == null && endDate == null)) {
            builder.and(masterAccountHistory.timestamp.between(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX)));
        }
        List<MasterAccountHistory> authChange = queryFactory.selectFrom(masterAccountHistory)
                .where(masterAccountHistory.category.eq("auth_change"), builder)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(masterAccountHistory.timestamp.desc())
                .fetch();

        Long totalCount = queryFactory.select(masterAccountHistory.count())
                .from(masterAccountHistory)
                .where(masterAccountHistory.category.eq("auth_change"))
                .fetchOne();

        return new PageImpl<>(authChange, pageable, totalCount);
    }

    @Override
    public Page<MasterAccountHistory> getLoginHistory(LocalDate startDate, LocalDate endDate, String searchText, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();
        if (!StringUtils.isNullOrEmpty(searchText)) {
            builder.and(masterAccountHistory.username.containsIgnoreCase(searchText).or(masterAccountHistory.userAccount.containsIgnoreCase(searchText)));
        }
        if (!(startDate == null && endDate == null)) {
            builder.and(masterAccountHistory.timestamp.between(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX)));
        }

        List<MasterAccountHistory> loginHistory = queryFactory.selectFrom(masterAccountHistory)
                .where(masterAccountHistory.category.eq("login_history"), builder)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(masterAccountHistory.timestamp.desc())
                .fetch();
        Long totalCount = queryFactory.select(masterAccountHistory.count())
                .from(masterAccountHistory)
                .where(masterAccountHistory.category.eq("login_history"), builder)
                .fetchOne();

        return new PageImpl<>(loginHistory, pageable, totalCount);
    }

    @Override
    public Page<MasterAccountHistory> getExcelHistory(LocalDate startDate, LocalDate endDate, String searchText, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();
        if (!StringUtils.isNullOrEmpty(searchText)) {
            builder.and(masterAccountHistory.username.containsIgnoreCase(searchText).or(masterAccountHistory.userAccount.containsIgnoreCase(searchText)));
        }
        if (!(startDate == null && endDate == null)) {
            builder.and(masterAccountHistory.timestamp.between(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX)));
        }
        List<MasterAccountHistory> excelHistory = queryFactory.selectFrom(masterAccountHistory)
                .where(masterAccountHistory.category.eq("excel_history"), builder)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(masterAccountHistory.timestamp.desc())
                .fetch();
        Long totalCount = queryFactory.select(masterAccountHistory.count())
                .from(masterAccountHistory)
                .where(masterAccountHistory.category.eq("excel_history"), builder)
                .fetchOne();

        return new PageImpl<>(excelHistory, pageable, totalCount);
    }
}


