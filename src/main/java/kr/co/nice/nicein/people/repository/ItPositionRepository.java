package kr.co.nice.nicein.people.repository;

import kr.co.nice.nicein.people.entity.ItPosition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItPositionRepository extends JpaRepository<ItPosition, Long>, ItPositionRepositoryCustom{

}
