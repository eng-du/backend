package com.gyu.engdu.global.infra.discord;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
public class DiscordWebhookSender {

    private final RestClient restClient;
    private final String webhookUrl;

    public DiscordWebhookSender(
            @Value("${discord.webhook.signup.url:}") String webhookUrl
    ) {
        this.restClient = RestClient.create();
        this.webhookUrl = webhookUrl;
    }

    public void sendUserRegisteredNotification(Long userId, String nickname) {
        if (webhookUrl == null || webhookUrl.isBlank()) {
            log.warn("디스코드 웹훅 URL이 설정되지 않았습니다. 알림 전송을 건너뜁니다. userId={}", userId);
            return;
        }

        try {
            String message = String.format("🎉 **신규 회원 가입 알림** 🎉 회원 ID: `%d` - 닉네임: `%s`", userId,
                    nickname);
            Map<String, String> payload = Map.of("content", message);

            restClient.post()
                    .uri(webhookUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();

            log.info("디스코드 웹훅 알림을 성공적으로 전송했습니다. userId={}", userId);
        } catch (RestClientException e) {
            log.error("디스코드 웹훅 API 통신에 실패했습니다. userId={}, message={}", userId, e.getMessage(), e);
        }
    }
}
