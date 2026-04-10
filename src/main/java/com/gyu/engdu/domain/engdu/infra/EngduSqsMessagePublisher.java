package com.gyu.engdu.domain.engdu.infra;

import com.gyu.engdu.config.MdcContext;
import com.gyu.engdu.domain.engdu.application.EngduMessagePublisher;
import com.gyu.engdu.domain.engdu.infra.dto.GenerateEngduPartMessage;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import io.micrometer.core.annotation.Timed;
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

  @MdcContext(key = "traceId", paramName = "traceId")
  @Timed("sqs")
  public void publish(GenerateEngduPartMessage message, String traceId) {
    sqsTemplate.send(to -> to
        .queue(queueName)
        .payload(message)
        .header("traceId", traceId));

    log.info("[SQS 메시지 발행] 메시지 발행에 성공했습니다. engduId={}, userId={}, step={}",
        message.engduId(), message.userId(), message.step());
  }
}
