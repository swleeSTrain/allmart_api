package org.sunbong.allmart_api.tosspayment.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TossPaymentResponseDTO {
    private Long tossPaymentId;
    private Long orderId;
    private String paymentKey;
    private String method;
    private BigDecimal amount;
    private String status;
    private String receiptUrl;
}

