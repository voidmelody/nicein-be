package kr.co.nice.nicein.people.repository;

import kr.co.nice.nicein.people.entity.ItTech;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItTechRepository extends JpaRepository<ItTech, Long>, ItTechRepositoryCustom{
}
