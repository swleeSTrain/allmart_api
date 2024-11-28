package org.sunbong.allmart_api.order.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long orderId;
    private String customerId;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime orderTime;
    private List<OrderItemDTO> orderItems;
}
