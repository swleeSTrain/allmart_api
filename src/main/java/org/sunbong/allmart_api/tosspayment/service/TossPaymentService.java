package org.sunbong.allmart_api.tosspayment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.sunbong.allmart_api.order.domain.OrderEntity;
import org.sunbong.allmart_api.order.domain.OrderItem;
import org.sunbong.allmart_api.order.repository.OrderJpaRepository;
import org.sunbong.allmart_api.order.repository.OrderItemJpaRepository;
import org.sunbong.allmart_api.tosspayment.domain.TossPayment;
import org.sunbong.allmart_api.tosspayment.dto.TossPaymentCreateDTO;
import org.sunbong.allmart_api.tosspayment.dto.TossPaymentResponseDTO;
import org.sunbong.allmart_api.tosspayment.repository.TossPaymentRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TossPaymentService {

    private final TossPaymentRepository tossPaymentRepository;
    private final OrderJpaRepository orderJpaRepository;
    private final OrderItemJpaRepository orderItemJpaRepository;

    /**
     * TossPayment 생성
     */
    public TossPaymentResponseDTO createTossPayment(TossPaymentCreateDTO request) {
        // OrderEntity 조회
        OrderEntity order = orderJpaRepository.findById(request.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Order ID not found: " + request.getOrderId()));

        // OrderItem을 활용한 추가 검증 (예: 주문 항목의 총액 계산 검증)
        List<OrderItem> orderItems = orderItemJpaRepository.findByOrderOrderID(order.getOrderID());
        BigDecimal calculatedAmount = orderItems.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (calculatedAmount.compareTo(request.getAmount()) != 0) {
            throw new IllegalArgumentException("Invalid payment amount. Calculated: " + calculatedAmount);
        }

        // TossPayment 생성
        TossPayment tossPayment = TossPayment.builder()
                .order(order)
                .paymentKey(request.getPaymentKey())
                .method(request.getMethod())
                .amount(request.getAmount())
                .status("PENDING")
                .receiptUrl(request.getReceiptUrl())
                .build();

        TossPayment savedPayment = tossPaymentRepository.save(tossPayment);

        // Response DTO 생성
        return TossPaymentResponseDTO.builder()
                .tossPaymentId(savedPayment.getTossPaymentID())
                .orderId(savedPayment.getOrder().getOrderID())
                .paymentKey(savedPayment.getPaymentKey())
                .method(savedPayment.getMethod())
                .amount(savedPayment.getAmount())
                .status(savedPayment.getStatus())
                .receiptUrl(savedPayment.getReceiptUrl())
                .build();
    }

    /**
     * TossPayment 상태 업데이트
     */
    public void updateTossPaymentStatus(Long id, String status) {
        TossPayment tossPayment = tossPaymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("TossPayment ID not found: " + id));

        tossPayment.toBuilder().status(status).build();
        tossPaymentRepository.save(tossPayment);
    }
}
