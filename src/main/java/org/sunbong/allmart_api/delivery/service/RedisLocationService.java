package org.sunbong.allmart_api.delivery.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisLocationService {
    private final RedisTemplate<String, String> redisTemplate;

    public void updateDriverLocation(Long driverId, String location) {
        redisTemplate.opsForValue().set("driver:" + driverId, location);
    }

    public String getDriverLocation(Long driverId) {
        return redisTemplate.opsForValue().get("driver:" + driverId);
    }

    public void removeDriverLocation(Long driverId) {
        redisTemplate.delete("driver:" + driverId);
    }
}