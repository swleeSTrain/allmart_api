package org.sunbong.allmart_api.delivery.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.sunbong.allmart_api.delivery.domain.DriverEntity;
import org.sunbong.allmart_api.delivery.dto.DriverListDTO;
import org.sunbong.allmart_api.delivery.service.DriverService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/driver")
@RequiredArgsConstructor
@Log4j2
public class DriverController {

    private final DriverService driverService;

    // 새 드라이버 추가
    @PostMapping("/add")
    public ResponseEntity<DriverEntity> addDriver(@RequestBody DriverEntity driver) {
        if (driver.getName() == null || driver.getName().isBlank()) {
            return ResponseEntity.badRequest().body(null);
        }
        if (driver.getMaxDeliveryCount() <= 0) {
            return ResponseEntity.badRequest().body(null);
        }
        DriverEntity newDriver = driverService.addNewDriver(driver.getName(), driver.getMaxDeliveryCount());
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


    //드라이버 리스트 조회 API
    @GetMapping("/list")
    public ResponseEntity<List<DriverListDTO>> getAllDrivers() {
        log.info("Fetching all drivers");
        List<DriverListDTO> drivers = driverService.getAllDrivers();
        return ResponseEntity.ok(drivers);
    }
}
