package org.sunbong.allmart_api.delivery.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.sunbong.allmart_api.delivery.domain.DeliveryEntity;
import org.sunbong.allmart_api.delivery.domain.DeliveryStatus;
import org.sunbong.allmart_api.delivery.domain.DriverEntity;
import org.sunbong.allmart_api.delivery.repository.DeliveryRepository;
import org.sunbong.allmart_api.delivery.repository.DriverRepository;
import org.sunbong.allmart_api.order.domain.OrderEntity;
import org.sunbong.allmart_api.order.domain.PaymentType;
import org.sunbong.allmart_api.order.repository.OrderJpaRepository;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DriverRepository driverRepository;
    private final OrderJpaRepository orderRepository;
    private final StringRedisTemplate redisTemplate;

    public void createDelivery(Long orderId, String customerId, String paymentType) {
        log.info("Creating delivery for Order ID: {}, Customer ID: {}, Payment Type: {}", orderId, customerId, paymentType);

        // 배달 가능한 운전자를 조회
        DriverEntity driver = driverRepository.findAvailableDriver()
                .orElseThrow(() -> new IllegalStateException("No available drivers at the moment"));

        // 운전자에게 배달 할당
        driver.assignDelivery();

        // 배달 엔티티 생성
        DeliveryEntity delivery = DeliveryEntity.builder()
                .orderId(orderId)
                .driver(driver)
                .status(DeliveryStatus.PENDING)
                .build();

        // 배달 정보 저장
        deliveryRepository.save(delivery);

        // Redis에 초기 상태 저장
        redisTemplate.opsForValue().set("delivery:" + delivery.getDeliveryId(), DeliveryStatus.PENDING.name());

        log.info("Delivery created successfully for Order ID: {} and assigned to Driver ID: {}", orderId, driver.getDriverId());
    }

    public void updateDeliveryStatus(Long deliveryId, DeliveryStatus newStatus) {
        DeliveryEntity delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("Delivery not found for ID: " + deliveryId));

        log.info("Updating delivery status for Delivery ID: {} to {}", deliveryId, newStatus);

        if (newStatus == DeliveryStatus.START) {
            // 배달 시작
            delivery.updateStatus(newStatus);
        } else if (newStatus == DeliveryStatus.IN_PROGRESS) {
            // 배달 진행 중 (자동 상태 전환)
            delivery.updateStatus(newStatus);
        } else if (newStatus == DeliveryStatus.COMPLETED) {
            // 배달 완료
            completeDelivery(delivery);
        }

        deliveryRepository.save(delivery);

        // Redis 상태 업데이트
        redisTemplate.opsForValue().set("delivery:" + deliveryId, newStatus.name());

        log.info("Delivery status updated successfully for Delivery ID: {}", deliveryId);
    }

    private void completeDelivery(DeliveryEntity delivery) {
        // 운전자의 현재 배달 카운트 감소
        DriverEntity driver = delivery.getDriver();
        driver.decreaseCurrentDeliveryCount();
        driverRepository.save(driver);

        // Redis 상태 업데이트
        redisTemplate.opsForValue().set("delivery:" + delivery.getDeliveryId(), DeliveryStatus.COMPLETED.name());

        log.info("Delivery completed for Delivery ID: {}. Driver's current delivery count updated.", delivery.getDeliveryId());

        // 현장 결제인 경우 Order 상태를 OFFLINE_COMPLETE로 변경
        OrderEntity order = orderRepository.findById(delivery.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found for ID: " + delivery.getOrderId()));

        if (order.getPaymentType() == PaymentType.OFFLINE) {
            order.changePaymentTypeToOfflineComplete();
            orderRepository.save(order);
            log.info("Order status updated to OFFLINE_COMPLETE for Order ID: {}", order.getOrderID());
        }
    }
}
