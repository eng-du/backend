package com.gyu.engdu.domain.engdu.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EngduRepository extends JpaRepository<Engdu, Long> {

  Page<Engdu> findAllByUserIdAndIsAllSolved(Long userId, Boolean isAllSovled, Pageable pageable);

  Page<Engdu> findALlByUserId(Long userId, Pageable pageable);
}
