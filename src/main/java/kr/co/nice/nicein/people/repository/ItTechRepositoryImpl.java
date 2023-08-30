package kr.co.nice.nicein.people.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import static kr.co.nice.nicein.people.entity.QItTech.itTech;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ItTechRepositoryImpl implements ItTechRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    @Override
    public List<String> findTechsCodeByUserId(String userId) {
        List<String> result = queryFactory.select(itTech.code)
                .from(itTech)
                .where(itTech.userId.eq(userId))
                .fetch();
        return result;
    }

    @Override
    public Long deleteByUserId(String userId) {
        long count = queryFactory.delete(itTech)
                .where(itTech.userId.eq(userId))
                .execute();
        return count;
    }
}
