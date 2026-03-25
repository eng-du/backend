package com.gyu.engdu.domain.engdu.application;

import com.gyu.engdu.domain.engdu.domain.Engdu;
import com.gyu.engdu.domain.engdu.domain.Part;
import com.gyu.engdu.domain.engdu.domain.enums.PartType;
import com.gyu.engdu.domain.engdu.infra.dto.GenerateEngduPartMessage;
import com.gyu.engdu.message.application.CreateMessageService;
import com.gyu.engdu.message.domain.MessageType;
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
    private final PartQueryService partQueryService;
    private final CreateMessageService createMessageService;

    /**
     * 소유자 검증 후 Part를 조회하고, publishable 상태라면 아웃박스 메시지를 저장합니다.
     */
    public void publishPart(Long userId, Long engduId, PartType partType) {
        Engdu engdu = engduQueryService.findExistingEngdu(engduId);
        engdu.validateOwner(userId);

        Part lockedPart = partQueryService.findWithLock(engduId, partType)
                .orElseGet(() -> Part.of(partType, engdu));

        if (lockedPart.isPublishable()) {
            GenerateEngduPartMessage event = GenerateEngduPartMessage.of(
                    engdu.getId(),
                    userId,
                    partType);

            createMessageService.save(MessageType.GENERATE_ENGDU_PART, event);
        }
    }
}
