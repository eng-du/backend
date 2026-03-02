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
     * Part를 생성하고 SQS에 INITIAL 메시지를 발행합니다.
     */
    public void createAndPublish(Long userId, Long engduId) {
        Engdu engdu = engduQueryService.findExistingEngdu(engduId);
        engdu.validateOwner(userId);

        Part lockedPart = partQueryService.findWithLock(engduId, PartType.INITIAL)
                .orElseGet(() -> Part.of(PartType.INITIAL, engdu));

        if (lockedPart.isPublishable()) {
            EngduSqsMessage message = EngduSqsMessage.of(
                    engdu.getId(),
                    userId,
                    PartType.INITIAL);

            engduMessagePublisher.publish(message);
        }
    }

    /**
     * 기존 Engdu에 대해 소유자 검증 후 SQS에 COMPLETE 메시지를 발행합니다.
     */
    public void publishNextPart(Long userId, Long engduId) {
        Engdu engdu = engduQueryService.findExistingEngdu(engduId);
        engdu.validateOwner(userId);

        Part lockedPart = partQueryService.findWithLock(engduId, PartType.COMPLETE)
                .orElseGet(() -> Part.of(PartType.COMPLETE, engdu));

        if (lockedPart.isPublishable()) {
            EngduSqsMessage message = EngduSqsMessage.of(
                    engdu.getId(),
                    userId,
                    PartType.COMPLETE);

            engduMessagePublisher.publish(message);
        }
    }
}
