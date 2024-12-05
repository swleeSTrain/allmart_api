package org.sunbong.allmart_api.tosspay.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.sunbong.allmart_api.tosspay.domain.TossPayment;

import java.util.Optional;

public interface TossPaymentRepository extends JpaRepository<TossPayment, Long> {
    Optional<TossPayment> findByPaymentKey(String paymentKey);
    Optional<TossPayment> findByOrderId(String orderId);
}
