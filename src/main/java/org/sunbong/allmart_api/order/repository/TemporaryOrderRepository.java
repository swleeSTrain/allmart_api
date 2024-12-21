package org.sunbong.allmart_api.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.sunbong.allmart_api.order.domain.TemporaryOrderEntity;
import org.sunbong.allmart_api.order.domain.TemporaryOrderStatus;

import java.util.List;

public interface TemporaryOrderRepository extends JpaRepository<TemporaryOrderEntity, Long> {
    List<TemporaryOrderEntity> findByStatus(TemporaryOrderStatus status);

}