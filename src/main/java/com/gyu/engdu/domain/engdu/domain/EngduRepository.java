package com.gyu.engdu.domain.engdu.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EngduRepository extends JpaRepository<Engdu, Long> {

}
