package org.sunbong.allmart_api.order.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sunbong.allmart_api.common.dto.PageRequestDTO;
import org.sunbong.allmart_api.common.dto.PageResponseDTO;
import org.sunbong.allmart_api.mock.FakeOrderSearch;
import org.sunbong.allmart_api.order.domain.OrderEntity;
import org.sunbong.allmart_api.order.domain.OrderItem;
import org.sunbong.allmart_api.order.domain.OrderStatus;
import org.sunbong.allmart_api.order.dto.OrderListDTO;
import org.sunbong.allmart_api.order.exception.OrderNotFoundException;
import org.sunbong.allmart_api.order.repository.OrderItemJpaRepository;
import org.sunbong.allmart_api.order.repository.OrderJpaRepository;
import org.sunbong.allmart_api.product.domain.Product;
import org.sunbong.allmart_api.product.exception.ProductNotFoundException;
import org.sunbong.allmart_api.product.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {

    private OrderServiceImpl orderService;
    private OrderJpaRepository orderRepository;
    private OrderItemJpaRepository orderItemRepository; // 추가된 mock 객체
    private FakeOrderSearch fakeOrderSearch;
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderJpaRepository.class);
        orderItemRepository = mock(OrderItemJpaRepository.class);
        productRepository = mock(ProductRepository.class);
        fakeOrderSearch = mock(FakeOrderSearch.class);
//        orderService = new OrderServiceImpl(orderRepository, orderItemRepository, productRepository); // 두 개의 mock 객체 주입
    }

    @Test
    @DisplayName("getOrderById는_존재하는_주문을_반환한다")
    void getOrderByIdTest() {
        // given
        Long orderId = 1L;
        OrderEntity mockOrder = OrderEntity.builder()
                .orderID(orderId)
                .customerId("customer1")
                .totalAmount(BigDecimal.valueOf(100.00))
                .status(OrderStatus.PENDING)
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));

        // when
        OrderListDTO result = orderService.getOrderById(orderId);

        // then
        assertThat(result.getOrderId()).isEqualTo(orderId);
        assertThat(result.getCustomerId()).isEqualTo("customer1");
        assertThat(result.getTotalAmount()).isEqualTo(BigDecimal.valueOf(100.00));
    }

    @Test
    @DisplayName("getOrderById는_존재하지_않는_주문일_경우_예외를_던진다")
    void getOrderByIdFailTest() {
        // given
        Long nonExistentOrderId = 999L;

        // when & then
        assertThatThrownBy(() -> orderService.getOrderById(nonExistentOrderId))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    @DisplayName("searchOrders는_가짜_데이터를_반환한다")
    void searchOrdersTest() {
        // given
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .page(1)
                .size(5)
                .build();

        PageResponseDTO<OrderListDTO> fakeResponse = new PageResponseDTO<>(
                Collections.nCopies(5, new OrderListDTO()), pageRequestDTO, 1L);

        // 페이크 데이터 반환하도록 설정
        when(fakeOrderSearch.searchOrders(any(), any(), eq(pageRequestDTO))).thenReturn(fakeResponse);

        // orderService의 searchOrders 메서드를 호출할 때 FakeOrderSearch가 아닌 mock 설정된 fakeResponse 사용
        PageResponseDTO<OrderListDTO> response = fakeOrderSearch.searchOrders(null, null, pageRequestDTO);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getDtoList().size()).isEqualTo(5);
    }

    @Test
    @DisplayName("changeOrderStatus는_주문_상태를_변경한다")
    void changeOrderStatusTest() {
        // given
        Long orderId = 1L;
        OrderEntity mockOrder = OrderEntity.builder()
                .orderID(orderId)
                .customerId("customer1")
                .totalAmount(BigDecimal.valueOf(100.00))
                .status(OrderStatus.PENDING)
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));

        // when
        orderService.changeOrderStatus(orderId, OrderStatus.COMPLETED);

        // then
        assertThat(mockOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    @DisplayName("createOrderFromVoice는_상품을_찾을_수_없으면_ProductNotFoundException을_던진다")
    void createOrderFromVoiceProductNotFoundTest() {
        // given
        String productName = "쌀";
        int quantity = 2;
        String userId = "user123";

        when(productRepository.findByName(productName)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orderService.createOrderFromVoice(productName, quantity, userId))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("Product not found: " + productName);
    }

    @Test
    @DisplayName("createOrderFromVoice는_정상적으로_주문을_생성한다")
    void createOrderFromVoiceTest() {
        // 초기 설정
        String productName = "쌀";
        int quantity = 2;
        String userId = "user123";

        Product mockProduct = Product.builder()
                .productID(1L)
                .name(productName)
                .price(BigDecimal.valueOf(50.00))
                .sku("5개 남아요")
                .build();

        // 주문 생성
        OrderEntity mockOrder = OrderEntity.builder()
                .orderID(1L)
                .customerId("customer1")
                .totalAmount(BigDecimal.valueOf(100.00))
                .status(OrderStatus.PENDING)
                .build();

        // Mock 설정
        when(productRepository.findByName(productName)).thenReturn(Optional.of(mockProduct));
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(mockOrder);
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(new OrderItem());

        // 테스트 실행
        orderService.createOrderFromVoice(productName, quantity, userId);

        // 호출 횟수 확인
        verify(orderRepository, times(1)).save(any(OrderEntity.class));
    }

}
