package org.sunbong.allmart_api.delivery.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class DeliveryEventDTO {
    private String deliveryId;  // 배달 ID
    private String newStatus;   // 새로운 상태
}
