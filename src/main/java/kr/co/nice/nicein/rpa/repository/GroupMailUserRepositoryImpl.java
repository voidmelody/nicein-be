package kr.co.nice.nicein.rpa.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.nice.nicein.rpa.dto.GroupMailUserResponseDto;
import kr.co.nice.nicein.rpa.entity.GroupMailUser;
import kr.co.nice.nicein.rpa.entity.RpaTask;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.Supplier;

import static kr.co.nice.nicein.rpa.entity.QRpaTask.rpaTask;
import static kr.co.nice.nicein.rpa.entity.QGroupMailUser.groupMailUser;
import static kr.co.nice.nicein.groupware.entity.QEmployee.employee;

@RequiredArgsConstructor
public class GroupMailUserRepositoryImpl implements GroupMailUserRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public List<GroupMailUserResponseDto> findBySearchOption(String companyId, String text) {
        List<GroupMailUserResponseDto> result = queryFactory.select(convertDto())
                .from(employee)
                .where(filterCompany(companyId).and((containDept(text)).or(containEmail(text)).or(containName(text))).and(filterMemberStatus()))
                .fetch();
        return result;
    }

    @Override
    public List<GroupMailUser> findByRpaTask(RpaTask task) {
        List<GroupMailUser> result = queryFactory.selectFrom(groupMailUser)
                .join(rpaTask)
                .on(groupMailUser.rpaTask.eq(rpaTask))
                .where(rpaTask.eq(task))
                .fetch();
        return result;
    }

    public ConstructorExpression<GroupMailUserResponseDto> convertDto(){
        ConstructorExpression<GroupMailUserResponseDto> dto = Projections.constructor(GroupMailUserResponseDto.class,
                employee.company.companyName,
                employee.department.deptName,
                employee.username,
                employee.loginId
        );
        return dto;
    }

    private BooleanBuilder nullSafeBuilder(Supplier<BooleanExpression> f){
        try{
            return new BooleanBuilder(f.get());
        }catch(Exception e){
            return new BooleanBuilder();
        }
    }
    private BooleanBuilder filterCompany(String companyId){
        return nullSafeBuilder(()-> employee.company.companyId.eq(companyId));
    }

    private BooleanBuilder containDept(String name){
        return nullSafeBuilder(() -> employee.department.deptName.contains(name));
    }

    private BooleanBuilder containEmail(String email){
        return nullSafeBuilder(()->employee.loginId.contains(email));
    }

    private BooleanBuilder containName(String name){
        return nullSafeBuilder(()->employee.username.contains(name));
    }

    private BooleanBuilder filterMemberStatus(){
        return nullSafeBuilder(()-> employee.status.eq("1"));
    }
}
