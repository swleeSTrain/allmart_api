package org.sunbong.allmart_api.delivery.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.sunbong.allmart_api.delivery.service.RedisLocationService;

@RestController
@RequestMapping("/api/location")
@RequiredArgsConstructor
public class RedisLocationController {

    private final RedisLocationService redisLocationService;

    // 드라이버 위치 업데이트
    @PostMapping("/{driverId}")
    public ResponseEntity<String> updateDriverLocation(
            @PathVariable Long driverId,
            @RequestParam String location) {
        redisLocationService.updateDriverLocation(driverId, location);
        return ResponseEntity.ok("Driver location updated successfully.");
    }

    // 드라이버 위치 조회
    @GetMapping("/{driverId}")
    public ResponseEntity<String> getDriverLocation(@PathVariable Long driverId) {
        String location = redisLocationService.getDriverLocation(driverId);
        return ResponseEntity.ok(location != null ? location : "Location not available.");
    }

    // 드라이버 위치 삭제
    @DeleteMapping("/{driverId}")
    public ResponseEntity<String> removeDriverLocation(@PathVariable Long driverId) {
        redisLocationService.removeDriverLocation(driverId);
        return ResponseEntity.ok("Driver location removed successfully.");
    }
}
