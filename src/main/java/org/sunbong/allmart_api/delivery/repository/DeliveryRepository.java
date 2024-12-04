package org.sunbong.allmart_api.delivery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.sunbong.allmart_api.delivery.domain.DeliveryEntity;

import java.util.List;

public interface DeliveryRepository extends JpaRepository<DeliveryEntity, Long> {
    List<DeliveryEntity> findByStatus(String status); // 특정 상태(PENDING 등)의 배달 검색
}