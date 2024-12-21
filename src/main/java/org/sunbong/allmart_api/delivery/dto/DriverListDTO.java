package org.sunbong.allmart_api.delivery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverListDTO {
    private Long driverId;
    private String name;
    private int maxDeliveryCount;
    private int currentDeliveryCount;
    private boolean canAcceptDelivery; // 배달 가능 여부
}
