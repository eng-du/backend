package com.gyu.engdu.domain.engdu.application;

import com.gyu.engdu.domain.engdu.domain.Engdu;
import com.gyu.engdu.domain.engdu.domain.Part;
import com.gyu.engdu.domain.engdu.domain.enums.PartType;
import com.gyu.engdu.domain.engdu.infra.dto.EngduSqsMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CreateEngduCommandService {

    private final EngduQueryService engduQueryService;
    private final EngduMessagePublisher engduMessagePublisher;
    private final PartQueryService partQueryService;

    /**
     * 소유자 검증 후 Part를 조회하고, publishable 상태라면 SQS 메시지를 발행합니다.
     */
    public void publishPart(Long userId, Long engduId, PartType partType) {
        Engdu engdu = engduQueryService.findExistingEngdu(engduId);
        engdu.validateOwner(userId);

        Part lockedPart = partQueryService.findWithLock(engduId, partType)
                .orElseGet(() -> Part.of(partType, engdu));

        if (lockedPart.isPublishable()) {
            EngduSqsMessage message = EngduSqsMessage.of(
                engdu.getId(),
                userId,
                partType);

            engduMessagePublisher.publish(message);
        }
    }
}
