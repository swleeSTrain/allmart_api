package org.sunbong.allmart_api.delivery.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.sunbong.allmart_api.delivery.domain.DeliveryEntity;
import org.sunbong.allmart_api.delivery.domain.DeliveryStatus;
import org.sunbong.allmart_api.delivery.domain.DriverEntity;
import org.sunbong.allmart_api.delivery.repository.DeliveryRepository;
import org.sunbong.allmart_api.delivery.repository.DriverRepository;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DriverRepository driverRepository;


    /**
     * 배달 생성
     */
    public void createDelivery(Long orderId, String customerId, String paymentType) {
        log.info("Creating delivery for Order ID: {}, Customer ID: {}, Payment Type: {}", orderId, customerId, paymentType);

        DriverEntity driver = driverRepository.findAvailableDriver()
                .orElseThrow(() -> new IllegalStateException("No available drivers at the moment"));

        driver.assignDelivery(); // 드라이버의 현재 배달 카운트 증가

        DeliveryEntity delivery = DeliveryEntity.builder()
                .orderId(orderId)
                .driver(driver)
                .status(DeliveryStatus.PENDING) // 초기 상태
                .build();

        deliveryRepository.save(delivery);

        log.info("Delivery created successfully for Order ID: {} and assigned to Driver ID: {}", orderId, driver.getDriverId());
    }

    /**
     * 배달 상태 업데이트
     */
    public void updateDeliveryStatus(Long deliveryId, DeliveryStatus newStatus) {
        DeliveryEntity delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("Delivery not found with id: " + deliveryId));
        DriverEntity driver = delivery.getDriver();
        DeliveryStatus currentStatus = delivery.getStatus();

        if (currentStatus == DeliveryStatus.PENDING && newStatus == DeliveryStatus.IN_PROGRESS) {
            driver.decrementCurrentDeliveryCount();
        } else if (currentStatus == DeliveryStatus.IN_PROGRESS && newStatus == DeliveryStatus.COMPLETED) {
            driver.incrementCurrentDeliveryCount();
        } else {
            throw new IllegalStateException("Invalid state transition from " + currentStatus + " to " + newStatus);
        }

        delivery.updateStatus(newStatus);
        deliveryRepository.save(delivery);
        driverRepository.save(driver);
        log.info("Updated delivery ID: {} to status: {}", deliveryId, newStatus);
    }

    @Transactional(readOnly = true)
    public Map<DeliveryStatus, Long> getDeliveryStatusCounts() {
        Map<DeliveryStatus, Long> statusCounts = new HashMap<>();
        for (DeliveryStatus status : DeliveryStatus.values()) {
            statusCounts.put(status, deliveryRepository.countByStatus(status));
        }
        return statusCounts;
    }

}
