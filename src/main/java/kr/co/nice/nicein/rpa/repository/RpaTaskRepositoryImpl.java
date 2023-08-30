package kr.co.nice.nicein.rpa.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.nice.nicein.groupware.repository.CompanyRepository;
import kr.co.nice.nicein.rpa.dto.RpaTaskRequestDto;
import kr.co.nice.nicein.common.vo.TableStatus;
import lombok.RequiredArgsConstructor;
import java.util.List;

import static kr.co.nice.nicein.rpa.entity.QGroupMailUser.groupMailUser;
import static kr.co.nice.nicein.rpa.entity.QRpaTask.rpaTask;
import static kr.co.nice.nicein.groupware.entity.QCompany.company;

@RequiredArgsConstructor
public class RpaTaskRepositoryImpl implements RpaTaskRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    private final CompanyRepository companyRepository;

    @Override
    public List<RpaTaskRequestDto> findAllRpaTasks() {
        List<RpaTaskRequestDto> result = queryFactory.select(convertDto())
                .from(rpaTask)
                .where(rpaTask.company.isNotNull())   // 회사 null인 경우는 보여주지 않는다.
                .leftJoin(groupMailUser).on(rpaTask.eq(groupMailUser.rpaTask))
                .leftJoin(company).on(rpaTask.company.eq(company))
                .groupBy(rpaTask.taskId, rpaTask.company.companyId)
                .fetch();
        return result;
    }

    @Override
    public List<RpaTaskRequestDto> searchRpaTasks(String companyId, String searchText, boolean includeNoUse) {
        List<RpaTaskRequestDto> result = queryFactory.select(convertDto())
                .from(rpaTask)
                .leftJoin(groupMailUser).on(rpaTask.eq(groupMailUser.rpaTask))
                .leftJoin(company).on(rpaTask.company.eq(company))
                .where(rpaTask.company.isNotNull().and(filterCompany(companyId)).and(filterNoUse(includeNoUse))
                        .and(containsTaskId(searchText).or(containsTaskName(searchText)).or(containsBotName(searchText)).or(containsManagerName(searchText)))
                )
                .groupBy(rpaTask.taskId, rpaTask.company.companyId)
                .orderBy(rpaTask.taskId.asc())
                .fetch();
        return result;
    }

    public ConstructorExpression<RpaTaskRequestDto> convertDto(){
        ConstructorExpression<RpaTaskRequestDto> dto = Projections.constructor(RpaTaskRequestDto.class,
                rpaTask.taskId,
                rpaTask.company.companyId,
                rpaTask.taskName,
                rpaTask.taskSaveTime,
                countType("rpatype01"),
                countType("rpatype02"),
                countType("rpatype03"),
                countType("rpatype04"),
                rpaTask.manager,
                rpaTask.schedule,
                rpaTask.botName,
                rpaTask.useYn
        );
        return dto;
    }

    private NumberExpression<Integer> countType(String category){
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(groupMailUser.category.eq(category));
        builder.and(groupMailUser.activeYn.eq(TableStatus.YES.getValue()));

        NumberExpression<Integer> result = new CaseBuilder()
                .when(builder)
                .then(1)
                .otherwise(0)
                .sum();
        return result;
    }

    private BooleanExpression filterCompany(String companyId){
        if(StringUtils.isNullOrEmpty(companyId) || companyId.equals("-1")){
            return Expressions.asBoolean(true).isTrue();  // 기본값인 true를 반환
        }
        return rpaTask.company.companyId.eq(companyId);
    }

    private BooleanExpression containsTaskId(String searchText){
        if(StringUtils.isNullOrEmpty(searchText)){
            return Expressions.asBoolean(true).isTrue();  // 기본값인 true를 반환
        }
        return rpaTask.taskId.containsIgnoreCase(searchText);
    }

    private BooleanExpression containsTaskName(String searchText){
        if(StringUtils.isNullOrEmpty(searchText)){
            return Expressions.asBoolean(true).isTrue();  // 기본값인 true를 반환
        }
        return rpaTask.taskName.containsIgnoreCase(searchText);
    }

    private BooleanExpression containsManagerName(String searchText){
        if(StringUtils.isNullOrEmpty(searchText)){
            return Expressions.asBoolean(true).isTrue();  // 기본값인 true를 반환
        }
        return rpaTask.manager.containsIgnoreCase(searchText);
    }

    private BooleanExpression containsBotName(String searchText){
        if(StringUtils.isNullOrEmpty(searchText)){
            return Expressions.asBoolean(true).isTrue();  // 기본값인 true를 반환
        }
        return rpaTask.botName.containsIgnoreCase(searchText);
    }

    private BooleanExpression filterNoUse(boolean includeNoUse){
        String useYn = includeNoUse ? "all" : "Y";
        if(useYn.equals("all")){
            return Expressions.asBoolean(true).isTrue();  // 기본값인 true를 반환
        }
        return rpaTask.useYn.eq(useYn);
    }

}
