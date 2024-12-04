package org.sunbong.allmart_api.delivery.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.sunbong.allmart_api.delivery.domain.DeliveryStatus;
import org.sunbong.allmart_api.delivery.service.DeliveryService;
import org.sunbong.allmart_api.order.dto.OrderEvent;

@RestController
@RequestMapping("/api/delivery")
@RequiredArgsConstructor
@Log4j2
public class DeliveryController {

    private final DeliveryService deliveryService;

    // 주문 이벤트 처리
    @PostMapping("/process")
    public ResponseEntity<String> processOrder(@RequestBody OrderEvent orderEvent) {
        log.info("Received order event: {}", orderEvent);
        deliveryService.processOrderEvent(orderEvent);
        return ResponseEntity.ok("Order processed successfully.");
    }

    // 배달 상태 업데이트
    @PatchMapping("/{deliveryId}/status")
    public ResponseEntity<String> updateDeliveryStatus(
            @PathVariable Long deliveryId,
            @RequestParam DeliveryStatus status) {
        deliveryService.updateDeliveryStatus(deliveryId, status);
        return ResponseEntity.ok("Delivery status updated successfully.");
    }
}
