package com.gyu.engdu.message.application;

import com.gyu.engdu.message.domain.MessageRepository;
import com.gyu.engdu.message.domain.MessageStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageCleanupService {

    private final MessageRepository messageRepository;

    // 매일 새벽 6시에 실행되어 발행이 완료된(PUBLISHED) 아웃박스 메시지를 일괄 삭제한다.
    @Scheduled(cron = "0 0 6 * * *")
    @Transactional
    public void cleanupPublishedMessages() {
        int deletedCount = messageRepository.deleteAllByStatus(MessageStatus.PUBLISHED);
        log.info("발행 완료된 아웃박스 메시지 일괄 삭제 완료. 삭제된 건수: {}", deletedCount);
    }
}
