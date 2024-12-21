package org.sunbong.allmart_api.order.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.sunbong.allmart_api.order.service.OrderService;

@Component
@RequiredArgsConstructor
@Log4j2
public class TemporaryOrderScheduler {

    private final OrderService orderService;

    /**
     * 매일 10시와 16시에 처리되지 않은 임시 주문을 정식 주문으로 변환
     */
    @Scheduled(cron = "0 00 10,16 * * ?")
    public void processTemporaryOrders() {
        log.info("Scheduled task started for processing temporary orders.");
        orderService.processUnprocessedTemporaryOrders();
        log.info("Scheduled task completed for processing temporary orders.");
    }
}