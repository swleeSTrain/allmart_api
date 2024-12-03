package org.sunbong.allmart_api.tosspayment.domain;

import jakarta.persistence.*;
import lombok.*;

import org.sunbong.allmart_api.common.domain.BaseEntity;
import org.sunbong.allmart_api.order.domain.OrderEntity;

import java.math.BigDecimal;

@Entity
@Table(name = "tbl_toss_payment", indexes = @Index(name = "idx_orderID", columnList = "orderID"))
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TossPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tossPaymentID;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderID", nullable = false)
    private OrderEntity order;

    @Column(nullable = false)
    private String paymentKey;

    @Column(nullable = false)
    private String method;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(nullable = true)
    private String receiptUrl;

    public void changeStatus(PaymentStatus newStatus) {
        if (this.status == PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Cannot change status from COMPLETED.");
        }
        this.status = newStatus;
    }


    public void completePayment(String method, BigDecimal amount, String receiptUrl) {
        this.status = PaymentStatus.COMPLETED;
        this.method = method;
        this.amount = amount;
        this.receiptUrl = receiptUrl;
    }
}
