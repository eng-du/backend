package com.gyu.engdu.domain.learning.domain;

import java.util.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PhrasalVerbRepository extends JpaRepository<PhrasalVerb, Long> {

    @Query("SELECT MAX(pv.id) FROM PhrasalVerb pv")
    Long findMaxId();

    PhrasalVerb findFirstByIdGreaterThanEqual(Long id);

    PhrasalVerb findFirstByIdGreaterThanEqualAndIdNotIn(Long id, Collection<Long> excludedIds);
}
