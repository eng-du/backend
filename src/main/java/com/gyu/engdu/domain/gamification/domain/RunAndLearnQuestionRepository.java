package com.gyu.engdu.domain.gamification.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RunAndLearnQuestionRepository extends JpaRepository<RunAndLearnQuestion, Long> {

    @Query("SELECT MAX(q.id) FROM RunAndLearnQuestion q")
    Optional<Long> findMaxId();

    @Query("SELECT q.id FROM RunAndLearnQuestion q ORDER BY q.id ASC")
    List<Long> findAllIds();

}
