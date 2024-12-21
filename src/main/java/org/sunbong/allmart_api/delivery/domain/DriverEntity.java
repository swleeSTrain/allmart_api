package org.sunbong.allmart_api.delivery.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long driverId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int maxDeliveryCount; // 최대 처리 가능 주문 수

    @Builder.Default
    @Column(nullable = false)
    private int currentDeliveryCount = 0; // 현재 처리 중인 배달 수

    // 배달 할당 가능 여부 확인
    public boolean canAcceptDelivery() {
        return currentDeliveryCount < maxDeliveryCount;
    }

    // 배달 할당 처리
    public void assignDelivery() {
        if (!canAcceptDelivery()) {
            throw new IllegalStateException("Driver cannot accept more deliveries.");
        }
        this.currentDeliveryCount++;
    }

    // 배달 할당 처리
    public void decrementCurrentDeliveryCount() {
        if (this.currentDeliveryCount <= 0) {
            throw new IllegalStateException("Current delivery count cannot be negative.");
        }
        this.currentDeliveryCount--;
    }

    public void incrementCurrentDeliveryCount() {
        if (this.currentDeliveryCount < maxDeliveryCount) {
            this.currentDeliveryCount++;
        } else {
            throw new IllegalStateException("Driver has reached the max delivery count.");
        }
    }
}