package com.gyu.engdu.domain.message.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findAllByStatus(MessageStatus status);

    @Modifying
    @Query("DELETE FROM Message m WHERE m.status = :status")
    int deleteAllByStatus(@Param("status") MessageStatus status);
}
