package kr.co.nice.nicein.people.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import static kr.co.nice.nicein.people.entity.QItPosition.itPosition;
import static kr.co.nice.nicein.people.entity.QMasterEmployee.masterEmployee;

import kr.co.nice.nicein.hris.dto.HrisDto;
import kr.co.nice.nicein.people.entity.ItPosition;
import kr.co.nice.nicein.people.entity.QMasterEmployee;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class ItPositionRepositoryImpl implements ItPositionRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public List<String> findPositionsCodeByUserId(String userId) {
        List<String> result = queryFactory.select(itPosition.code)
                .from(itPosition)
                .where(itPosition.userId.eq(userId))
                .fetch();
        return result;
    }

    @Override
    public Long deleteByUserId(String userId) {
        long count = queryFactory.delete(itPosition)
                .where(itPosition.userId.eq(userId))
                .execute();
        return count;
    }

    @Override
    public Page<HrisDto> getHrisItPositionDto(String gwCode, Pageable pageable){
        List<HrisDto> result = queryFactory.select(Projections.constructor(HrisDto.class,
                        masterEmployee.hrisId,
                        masterEmployee.itPosition
                )).from(masterEmployee)
                .where(masterEmployee.hrisId.isNotNull(), masterEmployee.companyId.eq(gwCode), masterEmployee.status.ne("퇴사"))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(masterEmployee.hrisId.asc())
                .fetch();
        Long totalCount = queryFactory.select(masterEmployee.count())
                .from(masterEmployee)
                .where(masterEmployee.hrisId.isNotNull(), masterEmployee.companyId.eq(gwCode), masterEmployee.status.ne("퇴사"))
                .fetchOne();
        return new PageImpl<>(result, pageable, totalCount);
    }
}
