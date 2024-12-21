

package org.sunbong.allmart_api.delivery.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.sunbong.allmart_api.delivery.domain.DeliveryEntity;
import org.sunbong.allmart_api.delivery.domain.DeliveryStatus;
import org.sunbong.allmart_api.delivery.domain.DriverEntity;
import org.sunbong.allmart_api.delivery.repository.DeliveryRepository;
import org.sunbong.allmart_api.delivery.repository.DriverRepository;
import org.sunbong.allmart_api.outbox.domain.OutboxEntity;
import org.sunbong.allmart_api.outbox.repository.OutboxRepository;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DriverRepository driverRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final OutboxRepository outboxRepository;

    public void createDelivery(Long orderId, String customerId, String paymentType) {
        log.info("Creating delivery for Order ID: {}, Customer ID: {}, Payment Type: {}", orderId, customerId, paymentType);

        DriverEntity driver = driverRepository.findAvailableDriver()
                .orElseThrow(() -> new IllegalStateException("No available drivers at the moment"));

        driver.assignDelivery();

        DeliveryEntity delivery = DeliveryEntity.builder()
                .orderId(orderId)
                .driver(driver)
                .status(DeliveryStatus.PENDING)
                .build();

        deliveryRepository.save(delivery);

        // Redis에 저장
        saveToRedis(delivery);

        // 상태 카운트 업데이트
        adjustStatusCountInRedis(null, delivery.getStatus());

        log.info("Delivery created successfully for Order ID: {}, Status: {}", orderId, delivery.getStatus());
    }

    public void updateDeliveryStatus(Long deliveryId, DeliveryStatus newStatus) {
        // 기존 배달 정보 조회
        DeliveryEntity delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("Delivery not found with id: " + deliveryId));

        DeliveryStatus oldStatus = delivery.getStatus();

        // 상태 전환 검증
        if (!isValidStatusTransition(oldStatus, newStatus)) {
            throw new IllegalStateException("Invalid status transition: " + oldStatus + " → " + newStatus);
        }

        // 상태 업데이트
        delivery.updateStatus(newStatus);
        deliveryRepository.save(delivery);

        // Redis 상태 카운트 조정
        adjustStatusCountInRedis(oldStatus, newStatus);

        // Redis에 개별 배달 정보 저장
        saveToRedis(delivery);

        log.info("Delivery status updated: Delivery ID={}, Old Status={}, New Status={}", deliveryId, oldStatus, newStatus);

        // "배달 시작" 상태일 경우, 바로 "배달 중" 상태로 전환
        if (newStatus == DeliveryStatus.START) {
            log.info("Immediately transitioning Delivery ID={} to IN_PROGRESS", deliveryId);

            // 즉시 "배달 중" 상태로 변경
            delivery.updateStatus(DeliveryStatus.IN_PROGRESS);
            deliveryRepository.save(delivery);

            // Redis 상태 카운트 재조정
            adjustStatusCountInRedis(newStatus, DeliveryStatus.IN_PROGRESS);

            // Redis에 개별 배달 정보 저장
            saveToRedis(delivery);

            log.info("Delivery ID={} transitioned to IN_PROGRESS", deliveryId);
        }
    }

    private void adjustStatusCountInRedis(DeliveryStatus oldStatus, DeliveryStatus newStatus) {
        try {
            if (oldStatus != null) {
                redisTemplate.opsForHash().increment("delivery-status-count", oldStatus.toString(), -1);
                log.info("Decremented Redis status count: {}", oldStatus);
            }

            redisTemplate.opsForHash().increment("delivery-status-count", newStatus.toString(), 1);
            log.info("Incremented Redis status count: {}", newStatus);
        } catch (Exception e) {
            log.error("Failed to adjust status count in Redis", e);
        }
    }

    private void saveToRedis(DeliveryEntity delivery) {
        try {
            // 개별 배달 정보 저장
            String redisKey = "delivery:" + delivery.getDeliveryId();
            String redisValue = objectMapper.writeValueAsString(delivery);
            redisTemplate.opsForValue().set(redisKey, redisValue);

            log.info("Saved delivery information to Redis: {} -> {}", redisKey, redisValue);
        } catch (Exception e) {
            log.error("Error saving delivery information to Redis", e);
        }
    }


    private void publishKafkaEvent(String eventType, DeliveryEntity delivery) {
        try {
            String payload = objectMapper.writeValueAsString(delivery);
            kafkaTemplate.send("delivery-events", eventType, payload);
            log.info("Published Kafka event: {} with payload: {}", eventType, payload);
        } catch (Exception e) {
            log.error("Error publishing Kafka event", e);
        }
    }

    private boolean isValidStatusTransition(DeliveryStatus currentStatus, DeliveryStatus newStatus) {
        return switch (currentStatus) {
            case PENDING -> newStatus == DeliveryStatus.START || newStatus == DeliveryStatus.CANCELLED;
            case START -> newStatus == DeliveryStatus.IN_PROGRESS || newStatus == DeliveryStatus.CANCELLED;
            case IN_PROGRESS -> newStatus == DeliveryStatus.COMPLETED || newStatus == DeliveryStatus.CANCELLED;
            default -> false; // COMPLETED 또는 CANCELLED 상태는 변경 불가
        };
    }

    private void updateOutboxEvent(String eventType, Long orderId, String payload) {
        OutboxEntity existingOutbox = outboxRepository.findByOrder_OrderID(orderId)
                .orElseThrow(() -> new IllegalStateException("No Outbox event found for Order ID: " + orderId));

        existingOutbox.updateEventType(eventType); // 이벤트 타입 변경
        existingOutbox.updatePayload(payload); // 새로운 페이로드 저장
        outboxRepository.save(existingOutbox); // 변경 사항 저장

        log.info("Outbox event updated: EventType={}, OrderID={}", eventType, orderId);
    }

    private String createDeliveryPayload(DeliveryEntity delivery) {
        try {
            return objectMapper.writeValueAsString(Map.of(
                    "deliveryId", delivery.getDeliveryId(),
                    "orderId", delivery.getOrderId(),
                    "status", delivery.getStatus().toString()
            ));
        } catch (Exception e) {
            throw new RuntimeException("Failed to create delivery payload", e);
        }
    }
}

