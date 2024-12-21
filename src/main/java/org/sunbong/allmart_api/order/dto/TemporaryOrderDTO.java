package org.sunbong.allmart_api.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemporaryOrderDTO {
    private Long tempOrderId;
    private String customerId;
    private String productName;
    private int quantity;
    private LocalDateTime orderTime;
}
