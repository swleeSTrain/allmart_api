package org.sunbong.allmart_api.tosspayment.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // 기본 생성자 추가
public class TossPaymentStatusUpdateDTO {
    private String status;

    @Builder
    public TossPaymentStatusUpdateDTO(String status) {
        this.status = status;
    }
}
