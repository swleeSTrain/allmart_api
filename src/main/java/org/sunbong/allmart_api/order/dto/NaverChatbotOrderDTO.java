package org.sunbong.allmart_api.order.dto;

import lombok.Data;

@Data
public class NaverChatbotOrderDTO {
    private String userId;
    private String productName;
    private int quantity;
}
