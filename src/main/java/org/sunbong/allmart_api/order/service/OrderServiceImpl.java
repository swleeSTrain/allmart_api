package org.sunbong.allmart_api.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.sunbong.allmart_api.common.dto.PageRequestDTO;
import org.sunbong.allmart_api.common.dto.PageResponseDTO;
import org.sunbong.allmart_api.delivery.repository.DeliveryRepository;
import org.sunbong.allmart_api.delivery.service.DeliveryService;
import org.sunbong.allmart_api.order.domain.OrderEntity;

import org.sunbong.allmart_api.order.domain.OrderItem;
import org.sunbong.allmart_api.order.domain.OrderStatus;
import org.sunbong.allmart_api.order.dto.OrderDTO;
import org.sunbong.allmart_api.order.dto.OrderItemDTO;
import org.sunbong.allmart_api.order.dto.OrderListDTO;
import org.sunbong.allmart_api.order.exception.OrderNotFoundException;
import org.sunbong.allmart_api.order.repository.OrderItemJpaRepository;
import org.sunbong.allmart_api.order.repository.OrderJpaRepository;
import org.sunbong.allmart_api.order.repository.search.OrderSearch;
import org.sunbong.allmart_api.product.domain.Product;
import org.sunbong.allmart_api.product.exception.ProductNotFoundException;
import org.sunbong.allmart_api.product.repository.ProductRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class OrderServiceImpl implements OrderService {

    private final OrderJpaRepository orderRepository;
    private final OrderItemJpaRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final DeliveryService deliveryService;

    @Override
    @Transactional(readOnly = true)
    public OrderListDTO getOrderById(Long orderId) {

        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        // OrderItemRepository를 통해 OrderItem 리스트를 조회
        List<OrderItem> orderItems = orderItemRepository.findByOrderOrderID(orderId);
        List<OrderItemDTO> orderItemDTOs = orderItems.stream()
                .map(item -> OrderItemDTO.builder()
                        .orderItemId(item.getOrderItemID())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .productId(item.getProduct().getProductID())
                        .productName(item.getProduct().getName())
                        .build())
                .collect(Collectors.toList());

        return OrderListDTO.builder()
                .orderId(order.getOrderID())
                .customerId(order.getCustomerId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .orderTime(order.getCreatedDate())
                .orderItems(orderItemDTOs)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<OrderListDTO> searchOrders(OrderStatus status, String customerId, PageRequestDTO pageRequestDTO) {

        return orderRepository.searchOrders(status, customerId, pageRequestDTO);
    }

    @Override
    public void changeOrderStatus(Long orderId, OrderStatus newStatus) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        // 단계별 로깅
        log.info("Order Retrieved: {}", order);
        log.info("Order CreatedDate Before Check: {}", order.getCreatedDate());

        if (order.getCreatedDate() == null) {
            throw new IllegalStateException("Order createdDate is null");
        }


        OrderEntity updatedOrder = order.changeStatus(newStatus);
        orderRepository.save(updatedOrder);

        log.info("Order CreatedDate After Save: {}", updatedOrder.getCreatedDate());

        if (newStatus == OrderStatus.COMPLETED) {
            LocalDateTime startTime = order.getCreatedDate().minusHours(2);
            LocalDateTime endTime = order.getCreatedDate();

            log.info("StartTime: {}, EndTime: {}", startTime, endTime);

            deliveryService.processOrdersForDelivery(startTime, endTime);
        }
    }

    @Override
    public OrderDTO createOrderFromVoice(String name, int quantity, String userId) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }

        // Product 조회
        Product product = productRepository.findByName(name)
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + name));

        // 총 금액 계산 (BigDecimal로 정확하게 처리)
        BigDecimal totalAmount = product.getPrice().multiply(BigDecimal.valueOf(quantity));

        // OrderEntity 생성 (상태는 대기 중, 총 금액을 미리 계산하여 설정)
        OrderEntity order = OrderEntity.builder()
                .customerId(userId)
                .status(OrderStatus.PENDING)
                .totalAmount(totalAmount)
                .build();

        // OrderEntity 저장
        order = orderRepository.save(order);

        // OrderItem 생성
        OrderItem orderItem = OrderItem.builder()
                .order(order)           // OrderItem에 OrderEntity를 설정
                .product(product)       // Product 설정
                .quantity(quantity)     // 수량 설정
                .unitPrice(product.getPrice())  // 가격 설정
                .build();

        // OrderItem 저장
        orderItemRepository.save(orderItem);

        // OrderItem -> OrderItemDTO 변환
        OrderItemDTO orderItemDTO = OrderItemDTO.builder()
                .orderItemId(orderItem.getOrderItemID())
                .productId(product.getProductID())
                .productName(product.getName())
                .unitPrice(product.getPrice())
                .quantity(quantity)
                .build();

        // OrderEntity -> OrderDTO 변환
        return OrderDTO.builder()
                .orderId(order.getOrderID())
                .customerId(order.getCustomerId())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus().name())
                .orderTime(order.getCreatedDate())
                .orderItems(List.of(orderItemDTO)) // 단일 항목 포함
                .build();
    }


    private BigDecimal calculateTotalAmount(Long orderId) {
        List<OrderItem> orderItems = orderItemRepository.findByOrderOrderID(orderId);
        return orderItems.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    @Override
    public List<OrderDTO> getCustomerCompletedOrders(String customerId) {
        List<OrderEntity> completedOrders = orderRepository.findByCustomerIdAndStatus(customerId, OrderStatus.COMPLETED);

        return completedOrders.stream()
                .map(order -> OrderDTO.builder()
                        .orderId(order.getOrderID())
                        .customerId(order.getCustomerId())
                        .totalAmount(order.getTotalAmount())
                        .status(order.getStatus().name())
                        .orderTime(order.getCreatedDate())
                        .build())
                .collect(Collectors.toList());
    }
}
