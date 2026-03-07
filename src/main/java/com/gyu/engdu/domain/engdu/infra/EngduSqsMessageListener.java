package com.gyu.engdu.domain.engdu.infra;

import com.gyu.engdu.domain.engdu.application.CreateEngduService;
import com.gyu.engdu.domain.engdu.application.PartCommandService;
import com.gyu.engdu.domain.engdu.domain.Part;
import com.gyu.engdu.domain.engdu.infra.dto.EngduSqsMessage;
import io.awspring.cloud.sqs.annotation.SqsListener;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EngduSqsMessageListener {

    private final CreateEngduService createEngduService;
    private final PartCommandService partCommandService;

    @SqsListener("${spring.cloud.aws.sqs.queue-name}")
    public void listen(EngduSqsMessage message) {
        log.info("SQS 메시지 수신 engduId={}, userId={}, step={}",
                message.engduId(), message.userId(), message.step());

        // [TX 1] 락 획득 + CREATING 상태 즉시 커밋
        Optional<Part> partOpt = partCommandService.claimPartCreation(
                message.engduId(), message.step());

        if (partOpt.isEmpty()) {
            // 이미 파트가 만들어진 상태이거나 다른 워커가 처리 중이므로 정상 ACK 처리한다.
            log.info("이미 잉듀의 파트가 만들어진 상태이거나 다른 워커가 처리중입니다. engduId={}, step={}",
                    message.engduId(), message.step());
            return;
        }

        Long partId = partOpt.get().getId();

        try {
            // 예외 발생 시 SQS가 ACK하지 않고 자동 재시도한다. 3번 재시도 실패한다면 DLQ로 작업을 위임한다.
            createEngduService.generatePart(partId, message.step());
            log.info("잉듀 파트 생성 완료. engduId={}, step={}", message.engduId(), message.step());
        } catch (Exception e) {
            // 즉시 FAILED 기록 및 SQS 재시도를 위해 예외 반환
            partCommandService.markFailed(partId);
            log.error("잉듀 파트 생성 실패. engduId={}, step={}", message.engduId(), message.step(), e);
            throw e;
        }
    }
}
