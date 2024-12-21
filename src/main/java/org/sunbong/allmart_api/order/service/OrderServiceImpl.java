package org.sunbong.allmart_api.order.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.sunbong.allmart_api.common.dto.PageRequestDTO;
import org.sunbong.allmart_api.common.dto.PageResponseDTO;
import org.sunbong.allmart_api.order.domain.*;
import org.sunbong.allmart_api.order.dto.OrderDTO;
import org.sunbong.allmart_api.order.dto.OrderItemDTO;
import org.sunbong.allmart_api.order.dto.OrderListDTO;
import org.sunbong.allmart_api.order.dto.TemporaryOrderDTO;
import org.sunbong.allmart_api.order.exception.OrderNotFoundException;
import org.sunbong.allmart_api.order.repository.OrderItemJpaRepository;
import org.sunbong.allmart_api.order.repository.OrderJpaRepository;
import org.sunbong.allmart_api.order.repository.TemporaryOrderRepository;
import org.sunbong.allmart_api.outbox.domain.OutboxEntity;
import org.sunbong.allmart_api.outbox.repository.OutboxRepository;
import org.sunbong.allmart_api.product.domain.Product;
import org.sunbong.allmart_api.product.exception.ProductNotFoundException;
import org.sunbong.allmart_api.product.repository.ProductRepository;
import org.sunbong.allmart_api.tosspay.dto.TossPaymentRequestDTO;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class OrderServiceImpl implements OrderService {

    private final OrderJpaRepository orderRepository;
    private final OrderItemJpaRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final TemporaryOrderRepository temporaryOrderRepository;
    private final OutboxRepository outboxRepository;

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
                .payment(order.getPaymentType())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<OrderListDTO> searchOrders(OrderStatus status, String customerId, PageRequestDTO pageRequestDTO) {

        return orderRepository.searchOrders(status, customerId, pageRequestDTO);
    }

//    @Override
//    public void changeOrderStatus(Long orderId, OrderStatus newStatus) {
//        OrderEntity order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new OrderNotFoundException(orderId));
//
//        // 단계별 로깅
//        log.info("Order Retrieved: {}", order);
//        log.info("Order CreatedDate Before Check: {}", order.getCreatedDate());
//
//        if (order.getCreatedDate() == null) {
//            throw new IllegalStateException("Order createdDate is null");
//        }
//
//
//        OrderEntity updatedOrder = order.changeStatus(newStatus);
//        orderRepository.save(updatedOrder);
//
//        log.info("Order CreatedDate After Save: {}", updatedOrder.getCreatedDate());
//
//        if (newStatus == OrderStatus.COMPLETED) {
//            LocalDateTime startTime = order.getCreatedDate().minusHours(2);
//            LocalDateTime endTime = order.getCreatedDate();
//
//            log.info("StartTime: {}, EndTime: {}", startTime, endTime);
//
//            deliveryService.processOrdersForDelivery(startTime, endTime);
//        }
//    }

    @Override
    public TemporaryOrderDTO createOrderFromVoice(String name, int quantity, String userId) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }

        // TemporaryOrderEntity 생성 및 저장
        TemporaryOrderEntity temporaryOrder = TemporaryOrderEntity.builder()
                .customerId(userId)
                .productName(name)
                .quantity(quantity)
                .build();

        TemporaryOrderEntity savedOrder = temporaryOrderRepository.save(temporaryOrder);

        log.info("Temporary order created successfully: {}", savedOrder);

        // TemporaryOrderEntity -> TemporaryOrderDTO 변환
        return TemporaryOrderDTO.builder()
                .tempOrderId(savedOrder.getTempOrderID())
                .customerId(savedOrder.getCustomerId())
                .productName(savedOrder.getProductName())
                .quantity(savedOrder.getQuantity())
                .orderTime(savedOrder.getCreatedDate())
                .build();
    }

    /**
     * 처리되지 않은 임시 주문을 정식 주문으로 변환
     */
    @Override
    public List<OrderDTO> processUnprocessedTemporaryOrders() {
        List<TemporaryOrderEntity> temporaryOrders = temporaryOrderRepository.findByStatus(TemporaryOrderStatus.PENDING);

        if (temporaryOrders.isEmpty()) {
            log.info("No unprocessed temporary orders found.");
            return Collections.emptyList();
        }

        Map<String, List<TemporaryOrderEntity>> groupedByCustomer = temporaryOrders.stream()
                .collect(Collectors.groupingBy(TemporaryOrderEntity::getCustomerId));

        List<OrderDTO> processedOrders = new ArrayList<>();

        groupedByCustomer.forEach((customerId, customerOrders) -> {
            BigDecimal totalAmount = customerOrders.stream()
                    .map(order -> productRepository.findByName(order.getProductName())
                            .orElseThrow(() -> new ProductNotFoundException("Product not found: " + order.getProductName()))
                            .getPrice().multiply(BigDecimal.valueOf(order.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // 정식 주문 생성
            OrderEntity orderEntity = OrderEntity.builder()
                    .customerId(customerId)
                    .totalAmount(totalAmount)
                    .paymentType(PaymentType.OFFLINE)
                    .status(OrderStatus.PENDING)
                    .build();

            OrderEntity savedOrder = orderRepository.save(orderEntity);

            // Outbox 이벤트 생성
            saveOutboxEvent("ORDER_CREATED", savedOrder, createOrderPayload(savedOrder));

            // 주문 항목 저장
            customerOrders.forEach(order -> {
                Product product = productRepository.findByName(order.getProductName())
                        .orElseThrow(() -> new ProductNotFoundException("Product not found: " + order.getProductName()));

                OrderItem orderItem = OrderItem.builder()
                        .order(savedOrder)
                        .product(product)
                        .quantity(order.getQuantity())
                        .unitPrice(product.getPrice())
                        .build();

                orderItemRepository.save(orderItem);
                order.markAsProcessed();
            });

            temporaryOrderRepository.saveAll(customerOrders);

            processedOrders.add(OrderDTO.builder()
                    .orderId(savedOrder.getOrderID())
                    .customerId(customerId)
                    .totalAmount(totalAmount)
                    .status(OrderStatus.PENDING.name())
                    .build());
        });

        return processedOrders;
    }

    private void saveOutboxEvent(String eventType, OrderEntity order, String payload) {
        OutboxEntity outboxEntity = OutboxEntity.builder()
                .eventType(eventType)
                .order(order)
                .payload(payload)
                .processed(false) // Kafka 처리 전
                .build();

        outboxRepository.save(outboxEntity);

        log.info("Outbox event created: EventType={}, OrderID={}, Payload={}", eventType, order.getOrderID(), payload);
    }


    private String createOrderPayload(OrderEntity order) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(Map.of(
                    "orderId", order.getOrderID(),
                    "customerId", order.getCustomerId(),
                    "paymentType", order.getPaymentType().toString()
            ));
        } catch (Exception e) {
            throw new RuntimeException("Failed to create order payload", e);
        }
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

    @Override
    public void completeOrder(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        // 주문 상태를 COMPLETED로 변경
        order.changeStatusToCompleted();
        orderRepository.save(order);

        log.info("Order status updated to COMPLETED for Order ID: {}", orderId);

        // Outbox 이벤트 업데이트
        updateOutboxEventType("ORDER_COMPLETED", orderId, createOrderPayload(order));
    }

    private void updateOutboxEventType(String newEventType, Long orderId, String payload) {
        OutboxEntity existingOutbox = outboxRepository.findByOrder_OrderID(orderId)
                .orElseThrow(() -> new IllegalStateException("No Outbox event found for Order ID: " + orderId));

        existingOutbox.updateEventType(newEventType); // 이벤트 타입 변경
        existingOutbox.updatePayload(payload); // 새로운 페이로드 저장
        outboxRepository.save(existingOutbox); // 변경 사항 저장

        log.info("Outbox event updated: EventType={}, OrderID={}", newEventType, orderId);
    }

    @Override
    public void deleteOrder(Long tempOrderId) {
        if (!temporaryOrderRepository.existsById(tempOrderId)) {
            throw new IllegalArgumentException("주문을 찾을 수 없습니다. ID: " + tempOrderId);
        }
        temporaryOrderRepository.deleteById(tempOrderId);
    }

    @Override
    public OrderEntity createOrder(TossPaymentRequestDTO paymentDTO, List<OrderItemDTO> orderItems) {
        // 1. OrderEntity 생성
        OrderEntity order = OrderEntity.builder()
                .customerId(paymentDTO.getOrderId()) // 고객 ID를 주문 ID로 설정
                .totalAmount(BigDecimal.valueOf(Long.parseLong(paymentDTO.getAmount())))
                .status(OrderStatus.PENDING)
                .paymentType(PaymentType.ONLINE) // 무조건 온라인 결제
                .build();

        // 2. OrderItem 생성 및 Order 연결
        List<OrderItem> items = orderItems.stream().map(itemDTO -> {
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));

            return OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemDTO.getQuantity())
                    .unitPrice(itemDTO.getUnitPrice())
                    .build();
        }).collect(Collectors.toList());


        orderRepository.save(order);

        orderItemRepository.saveAll(items);

        saveOutboxEvent("ORDER_CREATED", order, createOrderPayload(order));
        return order;
    }
}
