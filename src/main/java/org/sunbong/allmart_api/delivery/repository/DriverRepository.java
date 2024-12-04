package org.sunbong.allmart_api.delivery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.sunbong.allmart_api.delivery.domain.DriverEntity;

public interface DriverRepository extends JpaRepository<DriverEntity, Long> {
    DriverEntity findFirstByMaxDeliveryCountGreaterThanOrderByIdAsc(int minDeliveryCount);
}