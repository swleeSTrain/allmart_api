package org.sunbong.allmart_api.tosspay.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TossPaymentResponseDTO {
    private String status;
    private String message;
    private String paymentKey;
    private String orderId;
    private String amount;
}