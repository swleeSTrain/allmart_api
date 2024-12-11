package org.sunbong.allmart_api.delivery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.sunbong.allmart_api.delivery.domain.DriverEntity;

import java.util.Optional;

public interface DriverRepository extends JpaRepository<DriverEntity, Long> {
    @Query("SELECT d FROM DriverEntity d WHERE d.currentDeliveryCount < d.maxDeliveryCount ORDER BY d.currentDeliveryCount ASC")
    Optional<DriverEntity> findAvailableDriver();
}