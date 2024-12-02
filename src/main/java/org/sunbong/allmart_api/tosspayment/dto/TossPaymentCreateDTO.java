package org.sunbong.allmart_api.tosspayment.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TossPaymentCreateDTO {
    private final Long orderId;
    private final String paymentKey;
    private final String method;
    private final BigDecimal amount;
    private final String receiptUrl;
}
