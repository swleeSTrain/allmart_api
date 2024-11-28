package org.sunbong.allmart_api.order.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.sunbong.allmart_api.order.domain.OrderEntity;
import org.sunbong.allmart_api.order.domain.OrderStatus;
import org.sunbong.allmart_api.order.repository.search.OrderSearch;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long>, OrderSearch {
    /**
     * 특정 시간 범위 내 상태가 COMPLETED인 주문 조회
     */
    List<OrderEntity> findByStatusAndCreatedDateBetween(
            OrderStatus status,
            LocalDateTime startTime,
            LocalDateTime endTime);

    /**
     * 특정 배달 ID와 연결된 주문 조회
     */
    List<OrderEntity> findByDeliveryDeliveryID(Long deliveryId);

    /**
     * 특정 고객 ID와 특정 상태의 주문 조회
     */
    List<OrderEntity> findByCustomerIdAndStatus(
            String customerId,
            OrderStatus status);
}