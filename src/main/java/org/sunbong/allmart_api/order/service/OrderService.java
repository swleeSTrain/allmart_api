package org.sunbong.allmart_api.order.service;

import org.sunbong.allmart_api.common.dto.PageRequestDTO;
import org.sunbong.allmart_api.common.dto.PageResponseDTO;
import org.sunbong.allmart_api.order.domain.OrderStatus;
import org.sunbong.allmart_api.order.dto.OrderDTO;
import org.sunbong.allmart_api.order.dto.OrderListDTO;
import org.sunbong.allmart_api.order.dto.TemporaryOrderDTO;

import java.util.List;

public interface OrderService {

    OrderListDTO getOrderById(Long orderId); // 주문 상세 조회

    PageResponseDTO<OrderListDTO> searchOrders(OrderStatus status, String customerId, PageRequestDTO pageRequestDTO);

//    void changeOrderStatus(Long orderId, OrderStatus newStatus); // 주문 상태 변경 메서드

    List<OrderDTO> getCustomerCompletedOrders(String customerId);

    TemporaryOrderDTO createOrderFromVoice(String name, int quantity, String userId);

    List<OrderDTO> processUnprocessedTemporaryOrders();

    void completeOrder(Long orderId);
}
