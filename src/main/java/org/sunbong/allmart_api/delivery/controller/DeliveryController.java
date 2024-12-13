package org.sunbong.allmart_api.delivery.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.sunbong.allmart_api.delivery.domain.DeliveryStatus;
import org.sunbong.allmart_api.delivery.service.DeliveryService;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/delivery")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @GetMapping("/status-counts")
    public ResponseEntity<Map<DeliveryStatus, Long>> getDeliveryStatusCounts() {
        return ResponseEntity.ok(deliveryService.getDeliveryStatusCounts());
    }

    @PatchMapping("/update-status")
    public ResponseEntity<Void> updateDeliveryStatus(@RequestBody Map<String, Object> payload) {
        Long deliveryId = Long.valueOf(payload.get("deliveryId").toString());
        DeliveryStatus newStatus = DeliveryStatus.valueOf(payload.get("status").toString());
        deliveryService.updateDeliveryStatus(deliveryId, newStatus);
        return ResponseEntity.ok().build();
    }
}