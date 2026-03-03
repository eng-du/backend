package com.gyu.engdu.domain.engdu.domain;

import com.gyu.engdu.domain.engdu.domain.enums.PartType;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PartRepository extends JpaRepository<Part, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Part p WHERE p.engdu.id = :engduId AND p.partType = :partType")
    Optional<Part> findByEngduIdAndPartTypeWithLock(@Param("engduId") Long engduId,
            @Param("partType") PartType partType);

    @Query("SELECT p FROM Part p WHERE p.engdu.id = :engduId AND p.partType = :partType")
    Optional<Part> findByEngduIdAndPartType(@Param("engduId") Long engduId,
            @Param("partType") PartType partType);
}
