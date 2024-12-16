package org.sunbong.allmart_api.outbox.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.sunbong.allmart_api.outbox.domain.OutboxEntity;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OutboxRepository extends JpaRepository<OutboxEntity, Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM OutboxEntity e WHERE e.processed = true AND e.createdDate < :beforeDate")
    int deleteByProcessedTrueAndCreatedDateBefore(LocalDateTime beforeDate);


    Optional<OutboxEntity> findByOrder_OrderID(Long orderId);
}