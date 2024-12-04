package org.sunbong.allmart_api.outbox.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.sunbong.allmart_api.outbox.domain.OutboxEntity;

import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxEntity, Long> {
    List<OutboxEntity> findByProcessedFalse();
}