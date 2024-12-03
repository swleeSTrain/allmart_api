package org.sunbong.allmart_api.tosspayment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.sunbong.allmart_api.tosspayment.domain.TossPayment;

import java.util.Optional;

@Repository
public interface TossPaymentRepository extends JpaRepository<TossPayment, Long> {
    Optional<TossPayment> findByPaymentKey(String paymentKey);
}
