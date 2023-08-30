package kr.co.nice.nicein.groupware.repository;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.nice.nicein.groupware.entity.Code;
import static kr.co.nice.nicein.groupware.entity.QCode.code1;
import static kr.co.nice.nicein.groupware.entity.QHistory.history;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CodeRepositoryImpl implements CodeRepositoryCustom{
    private final JPAQueryFactory queryFactory;


    @Override
    public List<Code> getPositionList() {
        List<Code> itPosition = queryFactory.selectFrom(code1)
                .where(code1.category.eq("it_position"))
                .orderBy(code1.value.asc())
                .fetch();
        return itPosition;
    }

    @Override
    public List<Code> getTechList() {
        List<Code> itTech = queryFactory.selectFrom(code1)
                .where(code1.category.eq("it_tech"))
                .orderBy(Expressions.stringTemplate("lower({0})", code1.value).asc())
                .fetch();
        return itTech;
    }

    @Override
    public List<List<Code>> getTechListOrderByCode() {
        List<List<Code>> result = new ArrayList<>();
        List<Code> itTech = queryFactory.selectFrom(code1)
                .where(code1.category.eq("it_tech"))
                .orderBy(Expressions.stringTemplate("lower({0})", code1.value).asc())
                .fetch();
        for(int i = 1; i <= 10; i++){
            final int category = i;
            List<Code> codes = itTech.stream().filter(t -> Integer.parseInt(t.getCode()) / 100 == category).collect(Collectors.toList());
            result.add(codes);
        }
        return result;
    }

    @Override
    public List<String> getTechDescList() {
        List<String> result = new ArrayList<>();
        List<Code> itTech = queryFactory.selectFrom(code1)
                .where(code1.category.eq("it_tech"))
                .orderBy(code1.value.asc())
                .fetch();
        for(int i = 1; i <= 10; i++){
            final int category = i;
            List<Code> codes = itTech.stream().filter(t -> Integer.parseInt(t.getCode()) / 100 == category).sorted(Comparator.comparing(Code::getCode)).toList();
            result.add(codes.get(0).getDesc());
        }
        return result;
    }

    @Override
    public String convertItPositionCodeToValue(String code) {
        String itPositionValue = queryFactory.select(code1.value)
                .from(code1)
                .where(code1.category.eq("it_position"),
                        code1.code.eq(code))
                .fetchOne();
        return itPositionValue;
    }

    @Override
    public String convertAuthOptionCodeToValue(String code) {
        String authOptionValue = queryFactory.select(code1.value)
                .from(code1)
                .where(code1.category.eq("auth_option"),
                        code1.code.eq(code))
                .fetchOne();
        return authOptionValue;
    }

    @Override
    public String convertAuthRwCodeToValue(String code) {
        String authRwValue = queryFactory.select(code1.value)
                .from(code1)
                .where(code1.category.eq("auth_rw"),
                        code1.code.eq(code))
                .fetchOne();
        return authRwValue;
    }

    @Override
    public String convertTypeValueToCode(String value) {
        String typeCode = queryFactory.select(code1.code)
                .from(code1)
                .where(code1.category.eq("type"),
                        code1.value.eq(value))
                .fetchOne();
        return typeCode;
    }

    @Override
    public List<String> getCategoryList() {
        List<String> categoryList = queryFactory.selectDistinct(code1.category)
                .from(code1)
                .orderBy(code1.category.asc())
                .fetch();
        return categoryList;
    }

    @Override
    public Page<Code> getCode(String category, String searchText, Pageable pageable) {
        System.out.println(category);
        BooleanBuilder builder = new BooleanBuilder();
        if(!StringUtils.isNullOrEmpty(category)){
            builder.and(code1.category.eq(category));
        }
        if (!StringUtils.isNullOrEmpty(searchText)) {
            builder.and(code1.code.containsIgnoreCase(searchText).or(code1.value.containsIgnoreCase(searchText)));
        }
        List<Code> code = queryFactory.selectFrom(code1)
                .where(builder)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(code1.category.asc(), code1.code.asc())
                .fetch();

        Long totalCount = queryFactory.select(code1.count())
                .from(code1)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(code, pageable, totalCount);
    }
}
