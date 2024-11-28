package org.sunbong.allmart_api.delivery.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DeliveryScheduler {

    private final DeliveryService deliveryService;

    @Scheduled(cron = "0 0 */2 * * *") // 매 2시간마다 실행
    public void processDelivery() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime twoHoursAgo = now.minusHours(2);

        deliveryService.processOrdersForDelivery(twoHoursAgo, now);
    }
}