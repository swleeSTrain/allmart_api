package org.sunbong.allmart_api.outbox.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.sunbong.allmart_api.delivery.service.DeliveryService;
import org.sunbong.allmart_api.outbox.domain.OutboxEntity;
import org.sunbong.allmart_api.outbox.repository.OutboxRepository;

@Service
@RequiredArgsConstructor
@Log4j2
public class OutboxEventListener {

    private final ObjectMapper objectMapper; // DI로 ObjectMapper 주입
    private final OutboxRepository outboxRepository;
    private final DeliveryService deliveryService;

    @KafkaListener(topics = "jindb.jindb.tbl_outbox", groupId = "delivery-service")
    @Transactional
    public void handleOutboxEvent(String message) {
        try {
            // Kafka 메시지 JSON 파싱
            JsonNode root = objectMapper.readTree(message);
            Long outboxId = root.get("id").asLong();
            String eventType = root.get("event_type").asText();
            String payload = root.get("payload").asText();

            // Payload 내부 JSON 파싱
            JsonNode payloadNode = objectMapper.readTree(payload);
            Long orderId = payloadNode.get("orderId").asLong();
            String customerId = payloadNode.get("customerId").asText();
            String paymentType = payloadNode.has("paymentType") ? payloadNode.get("paymentType").asText() : "UNKNOWN";

            // 이벤트 타입이 ORDER_COMPLETED인 경우 처리
            if ("ORDER_COMPLETED".equals(eventType)) {
                log.info("Processing Outbox event: ID: {}, Type: {}, Order ID: {}", outboxId, eventType, orderId);

                // Outbox 상태를 처리 완료로 업데이트
                OutboxEntity outboxEntity = outboxRepository.findById(outboxId)
                        .orElseThrow(() -> new IllegalArgumentException("Outbox entry not found for ID: " + outboxId));
                outboxEntity.markAsProcessed();
                outboxRepository.save(outboxEntity);

                log.info("Outbox event marked as processed: Outbox ID: {}", outboxId);

                // 배달 생성 호출
                deliveryService.createDelivery(orderId, customerId, paymentType);
            }
        } catch (Exception e) {
            log.error("Error processing Outbox event: {}", message, e);
        }
    }
}



