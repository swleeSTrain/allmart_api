package org.sunbong.allmart_api.delivery.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.sunbong.allmart_api.delivery.dto.DeliveryStatusUpdateDTO;
import org.sunbong.allmart_api.delivery.service.DeliveryService;
import org.sunbong.allmart_api.order.dto.OrderEvent;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/delivery")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PatchMapping("/status")
    public ResponseEntity<Void> updateDeliveryStatus(@RequestBody @Validated DeliveryStatusUpdateDTO dto) {
        deliveryService.updateDeliveryStatus(dto.getDeliveryId(), dto.getStatus());
        return ResponseEntity.ok().build();
    }
}
