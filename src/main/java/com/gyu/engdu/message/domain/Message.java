package com.gyu.engdu.message.domain;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MessageStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private MessageType type;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "JSON", nullable = false)
    private JsonNode payload;

    @CreatedDate
    private LocalDateTime createdAt;

    private LocalDateTime publishedAt;

    private Message(MessageType type, JsonNode payload) {
        this.type = type;
        this.payload = payload;
        this.status = MessageStatus.NEW;
    }

    public static Message of(MessageType type, JsonNode payload) {
        return new Message(type, payload);
    }

    public void markPublished(LocalDateTime publishedAt) {
        this.status = MessageStatus.PUBLISHED;
        this.publishedAt = publishedAt;
    }
}
