//package org.sunbong.allmart_api.order.domain;
//
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.sunbong.allmart_api.order.exception.OrderStatusChangeException;
//
//import java.math.BigDecimal;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//
//class OrderEntityTest {
//
//    @Test
//    @DisplayName("주문의_상태를_변경할_수_있다.")
//    void changeStatusTest() {
//        //given
//        OrderEntity orderEntity = OrderEntity.builder()
//                .orderID(1L)
//                .status(OrderStatus.PENDING)
//                .customerId("김대진")
//                .notification(0)
//                .totalAmount(BigDecimal.valueOf(10000))
//                .build();
//        //when
//        orderEntity.changeStatus(OrderStatus.COMPLETED);
//        //then
//        assertThat(orderEntity.getStatus()).isEqualTo(OrderStatus.COMPLETED);
//    }
//
//    @Test
//    @DisplayName("주문의_상태를_변경_실패시_에러를_던진다.")
//    void changeStatusFailTest() {
//        //given
//        OrderEntity orderEntity = OrderEntity.builder()
//                .orderID(1L)
//                .status(OrderStatus.COMPLETED)
//                .customerId("김대진")
//                .notification(0)
//                .totalAmount(BigDecimal.valueOf(10000))
//                .build();
//        //when
//        //then;
//        assertThatThrownBy(()-> orderEntity.changeStatus(OrderStatus.COMPLETED))
//                .isInstanceOf(OrderStatusChangeException.class);
//    }
//
//    @Test
//    @DisplayName("총금액을_현재상태로_바꾼다")
//    void updateTotalAmountTest() {
//        // given
//        OrderEntity orderEntity = OrderEntity.builder()
//                .orderID(1L)
//                .status(OrderStatus.COMPLETED)
//                .customerId("김대진")
//                .notification(0)
//                .totalAmount(BigDecimal.valueOf(10000))
//                .build();
//
//        //when
//        BigDecimal newAmount = BigDecimal.valueOf(15000);
//        orderEntity.updateTotalAmount(newAmount);
//
//        //then
//        assertThat(orderEntity.getTotalAmount()).isEqualTo(newAmount);
//    }
//}