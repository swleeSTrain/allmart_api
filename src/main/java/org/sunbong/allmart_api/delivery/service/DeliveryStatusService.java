package org.sunbong.allmart_api.delivery.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.sunbong.allmart_api.delivery.domain.DeliveryStatus;

import java.util.EnumMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class DeliveryStatusService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String DELIVERY_STATUS_KEY = "delivery-status-count";

    /**
     * Redis에서 상태별 배달 건수 가져오기
     * @return 상태별 배달 건수
     */
    public Map<DeliveryStatus, Long> getDeliveryStatusCount() {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        Map<String, String> redisData = hashOperations.entries(DELIVERY_STATUS_KEY);

        Map<DeliveryStatus, Long> statusCountMap = new EnumMap<>(DeliveryStatus.class);
        redisData.forEach((key, value) -> {
            try {
                statusCountMap.put(DeliveryStatus.valueOf(key), Long.parseLong(value));
            } catch (Exception e) {
                log.error("Invalid data in Redis: key={}, value={}", key, value);
            }
        });
        log.info("Fetched delivery status count from Redis: {}", statusCountMap);
        return statusCountMap;
    }
}
