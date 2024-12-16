package org.sunbong.allmart_api.outbox.domain;

import jakarta.persistence.*;
import lombok.*;
import org.sunbong.allmart_api.common.domain.BaseEntity;
import org.sunbong.allmart_api.order.domain.OrderEntity;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Table(name = "tbl_outbox")
@ToString(exclude = {"order"})
public class OutboxEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(nullable = false)
    private boolean processed; // 처리 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    // 기존 이벤트 타입을 갱신하는 메서드
    public void updateEventType(String newEventType) {
        this.eventType = newEventType;
    }


    public void updatePayload(String newPayload) {
        this.payload = newPayload;
    }

    // 처리 완료 상태로 변경하는 메서드
    public void markAsProcessed() {
        this.processed = true;
    }

    // 처리 미완료 상태로 초기화하는 메서드
    public void markAsUnprocessed() {
        this.processed = false;
    }
}
