package org.sunbong.allmart_api.outbox.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.sunbong.allmart_api.order.domain.OrderEntity;
import org.sunbong.allmart_api.outbox.domain.OutboxEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OutboxRepository extends JpaRepository<OutboxEntity, Long> {
    // 특정 주문과 관련된 처리되지 않은 이벤트 조회
    Optional<OutboxEntity> findByOrderAndEventTypeAndProcessed(OrderEntity order, String eventType, boolean processed);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM OutboxEntity e WHERE e.id = :id")
    Optional<OutboxEntity> findByIdForUpdate(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM OutboxEntity e WHERE e.processed = false ORDER BY e.createdDate ASC")
    List<OutboxEntity> findTopNByProcessedFalse(Pageable pageable);

    @Transactional
    @Modifying
    @Query("DELETE FROM OutboxEntity e WHERE e.processed = true AND e.createdDate < :beforeDate")
    int deleteByProcessedTrueAndCreatedDateBefore(LocalDateTime beforeDate);
}