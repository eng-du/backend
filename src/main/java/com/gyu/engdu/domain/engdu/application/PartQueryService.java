package com.gyu.engdu.domain.engdu.application;

import com.gyu.engdu.domain.engdu.application.dto.response.EngduPartStatusResponse;
import com.gyu.engdu.domain.engdu.domain.Part;
import com.gyu.engdu.domain.engdu.domain.PartRepository;
import com.gyu.engdu.domain.engdu.domain.enums.PartType;
import com.gyu.engdu.domain.engdu.exception.PartNotFoundException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PartQueryService {

    private final PartRepository partRepository;

    public Optional<Part> findWithLock(Long engduId, PartType partType) {
        return partRepository.findByEngduIdAndPartTypeWithLock(engduId, partType);
    }

    // 폴링 전용: 락 없이 Part 조회 후 소유자 검증, DTO 변환까지 처리
    public EngduPartStatusResponse findPartStatus(Long userId, Long engduId, PartType partType) {
        Part part = partRepository.findByEngduIdAndPartType(engduId, partType)
                .orElseThrow(() -> new PartNotFoundException(engduId, partType));
        part.validateOwner(userId);
        return EngduPartStatusResponse.fromEntity(part);
    }
}
