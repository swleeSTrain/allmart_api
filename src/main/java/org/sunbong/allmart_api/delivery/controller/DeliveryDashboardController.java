package org.sunbong.allmart_api.delivery.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.sunbong.allmart_api.delivery.domain.DeliveryEntity;
import org.sunbong.allmart_api.delivery.repository.DeliveryRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/delivery-dashboard")
public class DeliveryDashboardController {

    private final StringRedisTemplate redisTemplate;

    @GetMapping("/status-summary")
    public ResponseEntity<Map<String, Long>> getStatusSummary() {
        // Redis에서 상태별 데이터 가져오기
        Map<Object, Object> redisData = redisTemplate.opsForHash().entries("delivery-status");
        Map<String, Long> summary = redisData.entrySet().stream()
                .collect(Collectors.groupingBy(
                        entry -> entry.getValue().toString(),
                        Collectors.counting()
                ));
        return ResponseEntity.ok(summary);
    }

}
