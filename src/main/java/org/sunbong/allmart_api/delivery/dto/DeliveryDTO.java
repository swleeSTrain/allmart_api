package org.sunbong.allmart_api.delivery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryDTO {

    private Long deliveryId;

    private LocalDateTime deliveryTime;

    private String status;

    private String customerId; // 고객 ID 추가
}
