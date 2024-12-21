package org.sunbong.allmart_api.order.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.sunbong.allmart_api.order.domain.TemporaryOrderEntity;
import org.sunbong.allmart_api.order.domain.TemporaryOrderStatus;
import org.sunbong.allmart_api.order.dto.TemporaryOrderDTO;
import org.sunbong.allmart_api.order.repository.TemporaryOrderRepository;
import org.sunbong.allmart_api.order.service.OrderService;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@RestController
@RequestMapping("/api/v1/temporary-orders")
@RequiredArgsConstructor
public class TemporaryOrderController {

    private final TemporaryOrderRepository temporaryOrderRepository;
    private final OrderService orderService;

    // 상태별 임시 주문 리스트 조회
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TemporaryOrderDTO>> getOrdersByStatus(@PathVariable TemporaryOrderStatus status) {
        log.info("Fetching temporary orders with status: {}", status);

        // 상태별 임시 주문 리스트 조회
        List<TemporaryOrderEntity> orders = temporaryOrderRepository.findByStatus(status);

        // Entity -> DTO 변환
        List<TemporaryOrderDTO> orderDTOs = orders.stream()
                .map(order -> TemporaryOrderDTO.builder()
                        .tempOrderId(order.getTempOrderID())
                        .customerId(order.getCustomerId())
                        .productName(order.getProductName())
                        .quantity(order.getQuantity())
                        .orderTime(order.getCreatedDate())
                        .build())
                .collect(Collectors.toList());

        log.info("Fetched {} temporary orders with status: {}", orderDTOs.size(), status);

        return ResponseEntity.ok(orderDTOs);
    }

    // 임시 주문 삭제
    @DeleteMapping("/{tempOrderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long tempOrderId) {
        orderService.deleteOrder(tempOrderId);
        return ResponseEntity.noContent().build();
    }
}
