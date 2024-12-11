package org.sunbong.allmart_api.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDTO {
    private Long orderItemId;       // 주문 항목 ID
    private int quantity;           // 수량
    private BigDecimal unitPrice;   // 단가
    private Long productId;         // 제품 ID
    private String productName;     // 제품 이름
}