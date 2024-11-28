package org.sunbong.allmart_api.order.controller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.sunbong.allmart_api.common.dto.PageRequestDTO;
import org.sunbong.allmart_api.common.dto.PageResponseDTO;
import org.sunbong.allmart_api.order.domain.OrderStatus;
import org.sunbong.allmart_api.order.dto.NaverChatbotOrderDTO;
import org.sunbong.allmart_api.order.dto.OrderDTO;
import org.sunbong.allmart_api.order.dto.OrderItemDTO;
import org.sunbong.allmart_api.order.dto.OrderListDTO;
import org.sunbong.allmart_api.order.service.OrderService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Validated  // 클래스 전체에 유효성 검사 적용
@Log4j2
public class OrderController {

    private final OrderService orderService;

    // 주문 상세 조회
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderListDTO> getOrderById(
            @PathVariable @Positive Long orderId) { // orderId는 양수여야 함
        OrderListDTO order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    // 주문 목록 조회
    @GetMapping("/list")
    public ResponseEntity<PageResponseDTO<OrderListDTO>> searchOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String customerId,
            @Validated PageRequestDTO pageRequestDTO) { // PageRequestDTO 유효성 검사
        PageResponseDTO<OrderListDTO> orders = orderService.searchOrders(status, customerId, pageRequestDTO);
        return ResponseEntity.ok(orders);
    }

    // 주문 상태 변경
    @PutMapping("/{orderId}/status")
    public ResponseEntity<Void> changeOrderStatus(
            @PathVariable @Positive Long orderId, // orderId는 양수여야 함
            @RequestParam @NotNull OrderStatus newStatus) { // newStatus는 필수 값
        orderService.changeOrderStatus(orderId, newStatus);
        return ResponseEntity.noContent().build();
    }

    // 주문 생성 (음성 주문 처리)
    @PostMapping("/voice")
    public ResponseEntity<String> createOrder(@RequestBody NaverChatbotOrderDTO naverChatbotOrderDTO) {
        log.info("++++++++++++++++++++++++++++++++++++++++++=");
        log.info(naverChatbotOrderDTO.toString());
        log.info("++++++++++++++++++++++++++++++++++++++++++=");

        // 주문 생성 서비스 호출
        orderService.createOrderFromVoice(naverChatbotOrderDTO.getProductName(),naverChatbotOrderDTO.getQuantity(),naverChatbotOrderDTO.getUserId());
        log.info("+++++++++++++++++++++++++++++++++++++++++++");

        return ResponseEntity.ok("Order created successfully.");
    }

    @GetMapping("/customer/{customerId}/completed")
    public ResponseEntity<List<OrderDTO>> getCompletedOrdersByCustomer(@PathVariable String customerId) {
        List<OrderDTO> completedOrders = orderService.getCustomerCompletedOrders(customerId);
        return ResponseEntity.ok(completedOrders);
    }
}
