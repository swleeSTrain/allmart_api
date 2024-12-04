package org.sunbong.allmart_api.delivery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.sunbong.allmart_api.delivery.domain.DeliveryEntity;
import org.sunbong.allmart_api.delivery.domain.DeliveryStatus;
import org.sunbong.allmart_api.order.domain.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<DeliveryEntity, Long> {

    /**
     * 특정 상태의 배달 조회
     */
    List<DeliveryEntity> findByStatus(DeliveryStatus status);

    /**
     * 특정 시간 범위 내 생성된 배달 조회
     */
    List<DeliveryEntity> findByCreatedDateBetween(
            LocalDateTime startTime,
            LocalDateTime endTime);

    /**
     * 고객 ID와 배달 상태로 배달 조회 (PENDING 상태)
     */
    Optional<DeliveryEntity> findByDeliveryIDAndStatus(Long deliveryId, DeliveryStatus status);
}


