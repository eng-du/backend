package com.gyu.engdu.domain.engdu.infra;

import com.gyu.engdu.domain.engdu.application.EngduMessagePublisher;
import com.gyu.engdu.domain.engdu.infra.dto.EngduSqsMessage;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EngduSqsMessagePublisher implements EngduMessagePublisher {

    private final SqsTemplate sqsTemplate;

    @Value("${spring.cloud.aws.sqs.queue-name}")
    private String queueName;

    public void publish(EngduSqsMessage message) {
        log.info("SQS 메시지 발행 engduId={}, userId={}, step={}",
                message.engduId(), message.userId(), message.step());
        sqsTemplate.send(queueName, message);
    }
}
