package org.sunbong.allmart_api.delivery.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import org.sunbong.allmart_api.delivery.domain.DeliveryStatus;
import org.sunbong.allmart_api.delivery.service.DeliveryService;
import org.sunbong.allmart_api.delivery.service.DeliveryStatusService;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/delivery")
@RequiredArgsConstructor
@Log4j2
public class DeliveryController {

    private final DeliveryService deliveryService;
    private final DeliveryStatusService deliveryStatusService;

    /**
     * 배달 상태 변경
     * @param deliveryId 배달 ID
     * @param newStatus 새로운 상태
     */
    @PutMapping("/{deliveryId}/status")
    public void updateDeliveryStatus(@PathVariable Long deliveryId, @RequestParam DeliveryStatus newStatus) {
        log.info("Request to update delivery status: Delivery ID={}, New Status={}", deliveryId, newStatus);
        deliveryService.updateDeliveryStatus(deliveryId, newStatus);
    }

    /**
     * 실시간 배달 상태 데이터 조회
     * @return 상태별 배달 건수
     */
    @GetMapping("/status-count")
    public Map<DeliveryStatus, Long> getRealTimeDeliveryStatus() {
        log.info("Request to fetch real-time delivery status count");
        return deliveryStatusService.getDeliveryStatusCount();
    }
}
