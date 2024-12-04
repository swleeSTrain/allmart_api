package org.sunbong.allmart_api.outbox.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.sunbong.allmart_api.delivery.service.DeliveryService;
import org.sunbong.allmart_api.order.dto.OrderEvent;

@Service
@RequiredArgsConstructor
@Log4j2
public class OutboxEventListener {

    private final ObjectMapper objectMapper;
    private final DeliveryService deliveryService;

    @KafkaListener(topics = "order-events", groupId = "delivery-service-group")
    public void handleOutboxEvent(String message) {
        try {
            OrderEvent orderEvent = objectMapper.readValue(message, OrderEvent.class);
            log.info("Received Order Event: {}", orderEvent);

            // DeliveryService로 메시지 전달
            deliveryService.processOrderEvent(orderEvent);

        } catch (Exception e) {
            log.error("Failed to process Order Event: {}", message, e);
        }
    }
}
