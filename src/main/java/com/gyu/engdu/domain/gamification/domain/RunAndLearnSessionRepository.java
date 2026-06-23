package com.gyu.engdu.domain.gamification.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RunAndLearnSessionRepository extends JpaRepository<RunAndLearnSession, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from RunAndLearnSession r where r.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    List<RunAndLearnSession> findAllByUserId(Long userId);

}
