package org.sunbong.allmart_api.delivery.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.sunbong.allmart_api.delivery.domain.DeliveryEntity;
import org.sunbong.allmart_api.delivery.domain.DeliveryStatus;
import org.sunbong.allmart_api.delivery.domain.DriverEntity;
import org.sunbong.allmart_api.delivery.repository.DeliveryRepository;
import org.sunbong.allmart_api.delivery.repository.DriverRepository;
import org.sunbong.allmart_api.order.dto.OrderEvent;

@Service
@RequiredArgsConstructor
@Log4j2
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DriverRepository driverRepository;
    private final RedisLocationService redisLocationService;

    public void processOrderEvent(OrderEvent orderEvent) {
        DriverEntity driver = findAvailableDriver();

        if (driver == null) {
            log.warn("No available driver for order: {}", orderEvent.getOrderId());
            return;
        }

        DeliveryEntity delivery = DeliveryEntity.builder()
                .driver(driver)
                .orderId(orderEvent.getOrderId())
                .status(DeliveryStatus.PENDING)
                .build();

        deliveryRepository.save(delivery);

        log.info("Assigned delivery: {} to driver: {}", orderEvent.getOrderId(), driver.getName());

        updateDriverDeliveryCount(driver);
    }

    private DriverEntity findAvailableDriver() {
        return driverRepository.findFirstByMaxDeliveryCountGreaterThanOrderByIdAsc(0);
    }

    private void updateDriverDeliveryCount(DriverEntity driver) {
        int newCount = driver.getMaxDeliveryCount() - 1;
        driver = DriverEntity.builder()
                .id(driver.getId())
                .name(driver.getName())
                .maxDeliveryCount(newCount)
                .build();
        driverRepository.save(driver);
    }

    public void updateDeliveryStatus(Long deliveryId, DeliveryStatus newStatus) {
        DeliveryEntity delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("Delivery not found with id: " + deliveryId));

        delivery = DeliveryEntity.builder()
                .id(delivery.getId())
                .driver(delivery.getDriver())
                .orderId(delivery.getOrderId())
                .status(newStatus)
                .build();

        deliveryRepository.save(delivery);

        // 배달이 완료되면 Redis에서 위치 정보 삭제
        if (newStatus == DeliveryStatus.COMPLETED) {
            // RedisLocationService 인스턴스를 사용하여 드라이버 위치 제거
            redisLocationService.removeDriverLocation(delivery.getDriver().getId());
            log.info("Removed location for driver: {}", delivery.getDriver().getId());
        }
    }
}

