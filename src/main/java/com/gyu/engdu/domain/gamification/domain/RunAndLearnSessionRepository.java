package com.gyu.engdu.domain.gamification.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RunAndLearnSessionRepository extends JpaRepository<RunAndLearnSession, Long> {

    void deleteByUserId(Long userId);

    List<RunAndLearnSession> findAllByUserId(Long userId);

}
