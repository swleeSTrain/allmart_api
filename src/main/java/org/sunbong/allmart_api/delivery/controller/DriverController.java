package org.sunbong.allmart_api.delivery.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.sunbong.allmart_api.delivery.domain.DriverEntity;
import org.sunbong.allmart_api.delivery.service.DriverService;

@RestController
@RequestMapping("/api/driver")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    // 새 드라이버 추가
    @PostMapping
    public ResponseEntity<DriverEntity> addDriver(
            @RequestParam String name,
            @RequestParam int maxDeliveryCount) {
        DriverEntity newDriver = driverService.addNewDriver(name, maxDeliveryCount);
        return ResponseEntity.ok(newDriver);
    }

    // 드라이버 배달 가능 수 업데이트
    @PatchMapping("/{driverId}/delivery-count")
    public ResponseEntity<String> updateDriverDeliveryCount(
            @PathVariable Long driverId,
            @RequestParam int newDeliveryCount) {
        driverService.updateDriverDeliveryCount(driverId, newDeliveryCount);
        return ResponseEntity.ok("Driver delivery count updated successfully.");
    }
}
