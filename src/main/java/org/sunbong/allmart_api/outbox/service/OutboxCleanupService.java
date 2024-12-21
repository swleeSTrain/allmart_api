package org.sunbong.allmart_api.outbox.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.sunbong.allmart_api.outbox.repository.OutboxRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Log4j2
public class OutboxCleanupService {

    private final OutboxRepository outboxRepository;

    @Scheduled(cron = "0 0 * * * ?") // 매 시간마다 실행
    public void cleanupProcessedEvents() {
        log.info("Starting cleanup of processed events...");

        int deletedCount = outboxRepository.deleteByProcessedTrueAndCreatedDateBefore(
                LocalDateTime.now().minusDays(7)); // 7일 이전 데이터 삭제

        log.info("Cleanup completed. Deleted {} processed events.", deletedCount);
    }
}