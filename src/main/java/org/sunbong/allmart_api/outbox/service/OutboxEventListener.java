package org.sunbong.allmart_api.outbox.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.sunbong.allmart_api.delivery.domain.DeliveryStatus;
import org.sunbong.allmart_api.delivery.service.DeliveryService;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class OutboxEventListener {

    private final ObjectMapper objectMapper; // JSON 처리용 ObjectMapper
    private final DeliveryService deliveryService;

    /**
     * Debezium Kafka 메시지 처리
     */
    @KafkaListener(topics = "jindb.jindb.tbl_outbox", groupId = "delivery-service", concurrency = "1")
    public void handleOutboxEvent(String message) {
        try {
            JsonNode root = objectMapper.readTree(message);

            String eventType = root.get("event_type").asText();
            String payload = root.get("payload").asText();

            log.info("Received Outbox Event: eventType={}, payload={}", eventType, payload);

            switch (eventType) {
                case "ORDER_COMPLETED" -> processOrderCompleted(payload);
                case "DELIVERY_CREATED" -> processDeliveryCreated(payload);
                case "DELIVERY_STATUS_UPDATED" -> processDeliveryStatusUpdated(payload);
                default -> log.warn("Unknown event type: {}", eventType);
            }
        } catch (Exception e) {
            log.error("Error processing Outbox event. Message: {}", message, e);
        }
    }

    private void processOrderCompleted(String payload) {
        try {
            JsonNode payloadNode = objectMapper.readTree(payload);
            Long orderId = payloadNode.get("orderId").asLong();
            String customerId = payloadNode.get("customerId").asText();

            log.info("Processing ORDER_COMPLETED event for Order ID: {}, Customer ID: {}", orderId, customerId);

            deliveryService.createDelivery(orderId, customerId, "ONLINE");
        } catch (Exception e) {
            log.error("Error processing ORDER_COMPLETED event. Payload: {}", payload, e);
            throw new RuntimeException(e);
        }
    }

    private void processDeliveryCreated(String payload) {
        log.info("Handling DELIVERY_CREATED event: Payload={}", payload);
        // 배달 생성 이벤트 처리
    }

    private void processDeliveryStatusUpdated(String payload) {
        try {
            JsonNode payloadNode = objectMapper.readTree(payload);
            Long deliveryId = payloadNode.get("deliveryId").asLong();
            String status = payloadNode.get("status").asText();

            log.info("Processing DELIVERY_STATUS_UPDATED event for Delivery ID: {}, Status: {}", deliveryId, status);

            deliveryService.updateDeliveryStatus(deliveryId, DeliveryStatus.valueOf(status));
        } catch (Exception e) {
            log.error("Error processing DELIVERY_STATUS_UPDATED event. Payload: {}", payload, e);
            throw new RuntimeException(e);
        }
    }
}
