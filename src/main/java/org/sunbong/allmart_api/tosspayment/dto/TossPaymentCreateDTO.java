package org.sunbong.allmart_api.tosspayment.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TossPaymentCreateDTO {
    private Long orderId;
    private String paymentKey;
    private String method;
    private BigDecimal amount;
    private String receiptUrl;
}
