package org.sunbong.allmart_api.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.sunbong.allmart_api.tosspay.dto.TossPaymentRequestDTO;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateRequest {
    private TossPaymentRequestDTO paymentDTO;      // Toss 결제 정보
    private List<OrderItemDTO> orderItems;         // 주문 항목 리스트
}