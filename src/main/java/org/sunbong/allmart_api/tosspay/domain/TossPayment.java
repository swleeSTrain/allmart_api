package org.sunbong.allmart_api.tosspay.domain;


import jakarta.persistence.*;
import lombok.*;
import org.sunbong.allmart_api.common.domain.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "toss_payments")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TossPayment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 내부적으로 사용하는 고유 ID

    @Column(nullable = false, unique = true)
    private String paymentKey; // Toss에서 제공하는 결제 키

    @Column(nullable = false, unique = true)
    private String orderId; // 주문 ID

    @Column(nullable = false)
    private Long amount; // 결제 금액

    @Column(nullable = false)
    private String status; // 결제 상태 (e.g., "SUCCESS", "FAILED")

    private String method; // 결제 수단 (e.g., 카드, 계좌이체)

    @Column(length = 1000)
    private String receiptUrl; // 영수증 URL (optional)
}
