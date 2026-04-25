package com.gyu.engdu.domain.engdu.domain;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EngduRepository extends JpaRepository<Engdu, Long> {

  // covering index를 위해 id만 조회한다.
  @Query("SELECT e.id FROM Engdu e WHERE e.userId = :userId AND e.isAllSolved = :isAllSolved")
  Page<Long> findIdsByUserIdAndIsAllSolved(
      @Param("userId") Long userId,
      @Param("isAllSolved") Boolean isAllSolved,
      Pageable pageable
  );

  // covering index를 위해 id만 조회한다.
  @Query("SELECT e.id FROM Engdu e WHERE e.userId = :userId")
  Page<Long> findIdsByUserId(@Param("userId") Long userId, Pageable pageable);

  List<Engdu> findByIdIn(List<Long> ids, Sort sort);

  boolean existsByUserId(Long userId);
}
