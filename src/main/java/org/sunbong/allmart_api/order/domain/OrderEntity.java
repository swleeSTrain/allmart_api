package org.sunbong.allmart_api.order.domain;


import jakarta.persistence.*;
import lombok.*;
import org.sunbong.allmart_api.common.domain.BaseEntity;
import org.sunbong.allmart_api.delivery.domain.DeliveryEntity;

import java.math.BigDecimal;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder(toBuilder = true)
@ToString(callSuper = true, exclude = "delivery")
@Table(name = "tbl_order", indexes = {@Index(name = "idx_deliveryID", columnList = "deliveryID")})
public class OrderEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderID;

    @Column(name = "customerID", nullable = false)
    private String customerId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Builder.Default
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;

//    @Column(nullable = false)
//    private int notification;

    @ManyToOne
    @JoinColumn(name = "deliveryID", nullable = true)
    private DeliveryEntity delivery;

    @Builder.Default
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType = PaymentType.ONLINE;

    // 상태 변경 메서드
    public OrderEntity changeStatusToCompleted() {
        this.status = OrderStatus.COMPLETED;
        return this;
    }

    public void changePaymentTypeToOfflineComplete() {
        if (this.paymentType == PaymentType.OFFLINE) {
            this.paymentType = PaymentType.OFFLINE_COMPLETE;
        } else {
            throw new IllegalStateException("Cannot change payment type to OFFLINE_COMPLETE for a non-offline payment");
        }
    }
}
