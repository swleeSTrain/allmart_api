package org.sunbong.allmart_api.order.domain;


import jakarta.persistence.*;
import lombok.*;
import org.sunbong.allmart_api.common.domain.BaseEntity;
import org.sunbong.allmart_api.delivery.domain.DeliveryEntity;
import org.sunbong.allmart_api.order.exception.OrderStatusChangeException;

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

    @Column(nullable = false)
    private int notification;

    @ManyToOne
    @JoinColumn(name = "deliveryID", nullable = false)
    private DeliveryEntity delivery;


    public OrderEntity changeStatus(OrderStatus newStatus) {
        return this.toBuilder()
                .status(newStatus)
                .build();
    }

    // 총 금액 업데이트 메서드
    public void updateTotalAmount(BigDecimal amount) {
        this.totalAmount = amount;
    }

    // 배달 연결 메서드
    public OrderEntity assignDelivery(DeliveryEntity delivery) {
        return this.toBuilder()
                .delivery(delivery)
                .build();
    }
}
