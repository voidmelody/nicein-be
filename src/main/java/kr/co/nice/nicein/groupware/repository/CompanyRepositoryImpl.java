package kr.co.nice.nicein.groupware.repository;

import com.querydsl.core.QueryFactory;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.nice.nicein.groupware.entity.Company;
import kr.co.nice.nicein.rpa.dto.CompanyDto;
import kr.co.nice.nicein.rpa.dto.RpaTaskRequestDto;
import lombok.RequiredArgsConstructor;
import static kr.co.nice.nicein.groupware.entity.QCompany.company;
import static kr.co.nice.nicein.rpa.entity.QRpaTask.rpaTask;

import java.util.List;

@RequiredArgsConstructor
public class CompanyRepositoryImpl implements CompanyRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public List<CompanyDto> findAllCompanyIdAndCompanyNamesOrderByOrderNum() {
        List<CompanyDto> dto = queryFactory.select(convertDto())
                .from(company)
                .orderBy(company.orderNum.asc())
                .fetch();
        return dto;
    }

    public ConstructorExpression<CompanyDto> convertDto(){
        ConstructorExpression<CompanyDto> dto = Projections.constructor(CompanyDto.class,
                company.companyId,
                company.companyName
        );
        return dto;
    }
}
