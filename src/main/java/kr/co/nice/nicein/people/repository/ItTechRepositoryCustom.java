package kr.co.nice.nicein.people.repository;

import java.util.List;

public interface ItTechRepositoryCustom {
    List<String> findTechsCodeByUserId(String userId);
    Long deleteByUserId(String userId);
}
