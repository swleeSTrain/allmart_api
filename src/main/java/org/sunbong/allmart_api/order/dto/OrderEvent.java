package org.sunbong.allmart_api.order.dto;

import lombok.Data;

@Data
public class OrderEvent {
    private Long orderId;
    private String customerId;
}