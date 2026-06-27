package com.gyu.engdu.domain.user.application.listener;

import com.gyu.engdu.domain.user.domain.event.UserRegisteredEvent;
import com.gyu.engdu.global.infra.discord.DiscordWebhookSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRegisteredEventListener {

    private final DiscordWebhookSender discordWebhookSender;

    @Async("notifyWebhookTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserRegisteredEvent(UserRegisteredEvent event) {
        log.info("새로운 사용자 가입 이벤트 수신: userId={}, nickname={}", event.userId(), event.nickname());
        discordWebhookSender.sendUserRegisteredNotification(event.userId(), event.nickname());
    }
}
