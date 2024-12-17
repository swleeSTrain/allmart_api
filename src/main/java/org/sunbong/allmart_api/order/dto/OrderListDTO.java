package org.sunbong.allmart_api.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.sunbong.allmart_api.order.domain.OrderStatus;
import org.sunbong.allmart_api.order.domain.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderListDTO {

    private Long orderId;                // 주문 ID
    private String customerId;           // 고객 ID
    private OrderStatus status;          // 주문 상태
    private BigDecimal totalAmount;      // 주문 총액
    private LocalDateTime orderTime;     // 주문 시간
    private List<OrderItemDTO> orderItems; // 주문 항목 리스트
    private PaymentType payment;       // 결제 정보
}
