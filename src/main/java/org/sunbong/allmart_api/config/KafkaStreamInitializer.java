package org.sunbong.allmart_api.config;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.sunbong.allmart_api.delivery.service.DeliveryStatusStreamService;

@Component
public class KafkaStreamInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private final DeliveryStatusStreamService deliveryStatusStreamService;

    public KafkaStreamInitializer(DeliveryStatusStreamService deliveryStatusStreamService) {
        this.deliveryStatusStreamService = deliveryStatusStreamService;
    }

    @Override
    public void onApplicationEvent(@Nullable ContextRefreshedEvent event) {
        deliveryStatusStreamService.processDeliveryStatusStream();
    }
}