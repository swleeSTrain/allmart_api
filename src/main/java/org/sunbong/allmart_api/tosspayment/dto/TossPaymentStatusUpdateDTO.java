package org.sunbong.allmart_api.tosspayment.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TossPaymentStatusUpdateDTO {
    private final String status;
}
