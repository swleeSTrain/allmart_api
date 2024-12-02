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
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"order"})
public class TossPayment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT")
    private Long tossPaymentID;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderID", nullable = false, columnDefinition = "BIGINT")
    private OrderEntity order;

    @Column(nullable = false)
    private String paymentKey; // Toss Payments에서 제공하는 결제 키

    @Column(nullable = false)
    private String method; // Toss 결제 수단 (예: 카드, 계좌이체 등)

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal amount; // Toss 결제 금액

    @Column(nullable = false)
    private String status; // Toss 결제 상태 (예: PENDING, COMPLETED, FAILED 등)

    @Column(nullable = true)
    private String receiptUrl; // Toss 결제 완료 후 제공되는 영수증 URL
}