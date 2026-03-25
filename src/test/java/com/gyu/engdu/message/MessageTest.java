package com.gyu.engdu.message;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gyu.engdu.domain.engdu.infra.dto.GenerateEngduPartMessage;
import com.gyu.engdu.domain.engdu.domain.enums.PartType;
import com.gyu.engdu.message.domain.Message;
import com.gyu.engdu.message.domain.MessageStatus;
import com.gyu.engdu.message.domain.MessageType;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MessageTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @DisplayName("메시지 생성 시 상태는 NEW다.")
    @Test
    void create_statusIsNew() {
        // given
        GenerateEngduPartMessage event = GenerateEngduPartMessage.of(1L, 1L, PartType.INITIAL);

        // when
        Message message = Message.of(MessageType.GENERATE_ENGDU_PART, objectMapper.valueToTree(event));

        // then
        assertThat(message.getStatus()).isEqualTo(MessageStatus.NEW);
        assertThat(message.getType()).isEqualTo(MessageType.GENERATE_ENGDU_PART);
        assertThat(message.getPublishedAt()).isNull();
    }

    @DisplayName("메시지를 발행하면 상태가 PUBLISHED로 변경되고 발행 시각이 기록된다.")
    @Test
    void markPublished_statusAndPublishedAt() {
        // given
        GenerateEngduPartMessage event = GenerateEngduPartMessage.of(1L, 1L, PartType.INITIAL);
        Message message = Message.of(MessageType.GENERATE_ENGDU_PART, objectMapper.valueToTree(event));
        LocalDateTime publishedAt = LocalDateTime.of(2026, 3, 20, 12, 0);

        // when
        message.markPublished(publishedAt);

        // then
        assertThat(message.getStatus()).isEqualTo(MessageStatus.PUBLISHED);
        assertThat(message.getPublishedAt()).isEqualTo(publishedAt);
    }
}
