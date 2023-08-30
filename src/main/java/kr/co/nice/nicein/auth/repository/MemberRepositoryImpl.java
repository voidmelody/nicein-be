package kr.co.nice.nicein.auth.repository;

import static kr.co.nice.nicein.auth.entity.QMember.member;


import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Coalesce;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import static kr.co.nice.nicein.groupware.entity.QCode.code1;
import static kr.co.nice.nicein.manage.entity.QAccount.account;

import kr.co.nice.nicein.groupware.entity.Code;
import kr.co.nice.nicein.manage.dto.AccountDto;
import kr.co.nice.nicein.manage.dto.AccountInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<AccountDto> searchAccount(String searchText, Pageable pageable) {
        List<AccountDto> result = queryFactory.select(Projections.constructor(AccountDto.class,
                        account.companyName,
                        account.name,
                        account.loginId,
                        account.authDetails,
                        account.note
                ))
                .from(account)
                .where(containsUsername(searchText).or(containsUserEmail(searchText)))
                .orderBy(account.companyId.asc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();

        Long totalCount = queryFactory.select(account.count())
                .from(account)
                .where(containsUsername(searchText).or(containsUserEmail(searchText)))
                .fetchOne();
        return new PageImpl<>(result, pageable, totalCount);
    }

    @Override
    public AccountInfoDto getAccountInfo(String userEmail) {
        AccountInfoDto accountInfoDto = queryFactory.select(Projections.fields(AccountInfoDto.class,
                        account.targetCompanyId.coalesce(account.companyId).as("targetCompanyId"),
                        account.name.as("username"),
                        account.loginId.as("account"),
                        member.role.stringValue().as("role"),
                        account.authOption.as("optionCode"),
                        account.authRw.as("rwCode"),
                        account.note,
                        member.otpYn.as("otpYn")
                ))
                .from(account)
                .join(member).on(member.username.eq(account.loginId))
                .where(account.loginId.eq(userEmail))
                .fetchOne();

        String optionCode = queryFactory.select(code1.code)
                .from(code1)
                .where(code1.category.eq("auth_option"),
                        code1.value.eq(accountInfoDto.getOptionCode()))
                .fetchOne();
        String rwCode = queryFactory.select(code1.code)
                .from(code1)
                .where(code1.category.eq("auth_rw"),
                        code1.value.eq(accountInfoDto.getRwCode()))
                .fetchOne();

        accountInfoDto.setOptionCode(optionCode);
        accountInfoDto.setRwCode(rwCode);
        return accountInfoDto;
    }

    @Override
    public List<Code> getOptionList() {
        List<Code> auth_option = queryFactory.selectFrom(code1)
                .where(code1.category.eq("auth_option"))
                .fetch();
        return auth_option;
    }

    @Override
    public List<Code> getRwList() {
        List<Code> auth_rw = queryFactory.selectFrom(code1)
                .where(code1.category.eq("auth_rw"))
                .fetch();
        return auth_rw;
    }

    private BooleanExpression containsUsername(String username){
        if(StringUtils.isNullOrEmpty(username)){
            return Expressions.asBoolean(true).isTrue();  // 기본값인 true를 반환
        }
        return account.name.containsIgnoreCase(username);
    }

    private BooleanExpression containsUserEmail(String userEmail){
        if(StringUtils.isNullOrEmpty(userEmail)){
            return Expressions.asBoolean(true).isTrue();  // 기본값인 true를 반환
        }
        return account.loginId.containsIgnoreCase(userEmail);
    }
}
