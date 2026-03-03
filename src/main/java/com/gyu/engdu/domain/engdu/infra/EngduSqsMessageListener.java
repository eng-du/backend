package com.gyu.engdu.domain.engdu.infra;

import com.gyu.engdu.domain.engdu.application.CreateEngduService;
import com.gyu.engdu.domain.engdu.application.PartQueryService;
import com.gyu.engdu.domain.engdu.domain.Part;
import com.gyu.engdu.domain.engdu.domain.enums.PartStatus;
import com.gyu.engdu.domain.engdu.infra.dto.EngduSqsMessage;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class EngduSqsMessageListener {

    private final CreateEngduService createEngduService;
    private final PartQueryService partQueryService;

    @SqsListener("${spring.cloud.aws.sqs.queue-name}")
    public void listen(EngduSqsMessage message) {
        log.info("SQS 메시지 수신 engduId={}, userId={}, step={}",
                message.engduId(), message.userId(), message.step());

        Part lockedPart = partQueryService.findExistingPartWithLock(message.engduId(),
                message.step());

        if (lockedPart.isAlreadyCreating()) {
            log.warn("이미 다른 워커가 잉듀를 만들고 있습니다. engduId={}, step={}, status={}",
                    message.engduId(), message.step(), lockedPart.getStatus());
            return;
        }

        // 파트 생성 요청하기 전에 상태를 CREATING으로 변경한다.
        lockedPart.changeStatus(PartStatus.CREATING);

        try {
            switch (message.step()) {
                case INITIAL -> createEngduService.generateInitialPart(lockedPart);
                case COMPLETE -> createEngduService.generateNextPart(lockedPart);
            }
            lockedPart.changeStatus(PartStatus.DONE);
            log.info("잉듀 파트 생성이 완료되었습니다. engduId={}, step={}", message.engduId(), message.step());
        } catch (Exception e) {
            log.error("잉듀 파트 생성에 실패했습니다. engduId={}, userId={}, step={}",
                    message.engduId(), message.userId(), message.step(), e);
            lockedPart.changeStatus(PartStatus.FAILED);
            throw e; // SQS 재시도 / DLQ 연동을 위해 rethrow
        }
    }

}
