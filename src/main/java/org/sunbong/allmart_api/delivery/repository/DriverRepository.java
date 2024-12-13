package org.sunbong.allmart_api.delivery.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.sunbong.allmart_api.delivery.domain.DriverEntity;

import java.util.Optional;

public interface DriverRepository extends JpaRepository<DriverEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM DriverEntity d WHERE d.currentDeliveryCount < d.maxDeliveryCount ORDER BY d.currentDeliveryCount ASC")
    Optional<DriverEntity> findAvailableDriver();
}