package com.gyu.engdu.domain.message.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gyu.engdu.global.aop.MdcContext;
import com.gyu.engdu.domain.engdu.application.EngduMessagePublisher;
import com.gyu.engdu.domain.engdu.infra.dto.GenerateEngduPartMessage;
import com.gyu.engdu.domain.message.domain.Message;
import com.gyu.engdu.domain.message.domain.MessageStatus;
import com.gyu.engdu.domain.message.domain.MessageRepository;
import com.gyu.engdu.domain.message.domain.MessageType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageRelayService {

  private final MessageRepository messageRepository;
  private final EngduMessagePublisher engduMessagePublisher;
  private final ObjectMapper objectMapper;

  // 주기마다 NEW 상태의 메시지를 SQS로 발행하고 PUBLISHED로 변경합니다.
  @MdcContext(key = "suppressSql", value = "true")
  @Scheduled(fixedDelayString = "${spring.message.relay.delay}")
  @Transactional
  public void relayMessages() {
    List<Message> newMessages = messageRepository.findAllByStatus(MessageStatus.NEW);

    for (Message message : newMessages) {
      try {
        if (message.getType() == MessageType.GENERATE_ENGDU_PART) {
          GenerateEngduPartMessage event = objectMapper.treeToValue(message.getPayload(),
              GenerateEngduPartMessage.class);
          engduMessagePublisher.publish(event, message.getTraceId());
          message.markPublished(LocalDateTime.now());
        }
      } catch (JsonProcessingException e) {
        log.error("메시지 역직렬화에 실패했습니다. messageId={}", message.getId(), e);
      }
    }
  }
}
