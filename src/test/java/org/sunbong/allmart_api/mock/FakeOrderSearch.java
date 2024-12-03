//package org.sunbong.allmart_api.mock;
//
//import org.sunbong.allmart_api.common.dto.PageRequestDTO;
//import org.sunbong.allmart_api.common.dto.PageResponseDTO;
//import org.sunbong.allmart_api.order.domain.OrderStatus;
//import org.sunbong.allmart_api.order.dto.OrderListDTO;
//import org.sunbong.allmart_api.order.repository.search.OrderSearch;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//public class FakeOrderSearch implements OrderSearch {
//
//    @Override
//    public PageResponseDTO<OrderListDTO> searchOrders(OrderStatus status, String customerId, PageRequestDTO pageRequestDTO) {
//        List<OrderListDTO> fakeOrders = new ArrayList<>();
//
//        // 요청받은 크기만큼 가짜 데이터 생성
//        for (int i = 0; i < pageRequestDTO.getSize(); i++) {
//            fakeOrders.add(OrderListDTO.builder()
//                    .orderId((long) i + 1)
//                    .customerId(customerId != null ? customerId : "testCustomer" + (i + 1))
//                    .status(status != null ? status : OrderStatus.PENDING)
//                    .totalAmount(BigDecimal.valueOf(1000))
//                    .orderTime(LocalDateTime.now())
//                    .build());
//        }
//
//        return new PageResponseDTO<>(fakeOrders, pageRequestDTO, fakeOrders.size());
//    }
//}
