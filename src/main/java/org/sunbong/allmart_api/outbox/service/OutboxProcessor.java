package org.sunbong.allmart_api.outbox.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.sunbong.allmart_api.outbox.domain.OutboxEntity;
import org.sunbong.allmart_api.outbox.repository.OutboxRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class OutboxProcessor {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedRate = 5000) // 5초마다 실행
    public void processOutboxEvents() {
        List<OutboxEntity> unprocessedEvents = outboxRepository.findByProcessedFalse();

        for (OutboxEntity event : unprocessedEvents) {
            try {
                // Kafka로 메시지 전송
                kafkaTemplate.send("order-events", event.getPayload());

                // 상태를 처리 완료로 변경
                event.markAsProcessed();
                outboxRepository.save(event);

                log.info("Successfully processed event: {}", event.getPayload());
            } catch (Exception e) {
                // 실패 시 로깅 및 재시도
                log.error("Failed to send event: {}", event.getPayload(), e);
            }
        }
    }
}
