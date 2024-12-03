package org.sunbong.allmart_api.tosspayment.service;


import lombok.RequiredArgsConstructor;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.sunbong.allmart_api.order.domain.OrderEntity;
import org.sunbong.allmart_api.order.repository.OrderJpaRepository;
import org.sunbong.allmart_api.tosspayment.domain.PaymentStatus;
import org.sunbong.allmart_api.tosspayment.domain.TossPayment;
import org.sunbong.allmart_api.tosspayment.dto.TossPaymentCreateDTO;
import org.sunbong.allmart_api.tosspayment.dto.TossPaymentResponseDTO;
import org.sunbong.allmart_api.tosspayment.repository.TossPaymentRepository;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class TossPaymentService {


    private final TossPaymentRepository tossPaymentRepository;
    private final OrderJpaRepository orderJpaRepository;


    @Value("${toss.api.secret-key:}")
    private String tossSecretKey;


    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.tosspayments.com/v1/payments/")
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " +
                    Base64.getEncoder().encodeToString((tossSecretKey + ":").getBytes()))
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();




    /**
     * TossPayment 생성
     */
    public TossPaymentResponseDTO createTossPayment(TossPaymentCreateDTO request) {

        // 로깅 추가


        log.info("Creating TossPayment for orderId: {}", request.getOrderId());
        log.info("Payment Key: {}, Method: {}, Amount: {}", request.getPaymentKey(),
                request.getMethod(), request.getAmount());
        log.info("Base64 Encoded Secret Key: {}",
                Base64.getEncoder().encodeToString((tossSecretKey + ":").getBytes()));

        log.info("Toss Secret Key: '{}'", tossSecretKey);



        OrderEntity order = orderJpaRepository.findById(request.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Order ID not found: " + request.getOrderId()));

        TossPayment tossPayment = TossPayment.builder()
                .order(order)
                .paymentKey(request.getPaymentKey())
                .method(request.getMethod())
                .amount(request.getAmount())
                .status(PaymentStatus.PENDING)
                .receiptUrl(request.getReceiptUrl())
                .build();

        TossPayment savedPayment = tossPaymentRepository.save(tossPayment);

        log.info("TossPayment created with ID: {}", savedPayment.getTossPaymentID());

        return TossPaymentResponseDTO.builder()
                .tossPaymentId(savedPayment.getTossPaymentID())
                .orderId(savedPayment.getOrder().getOrderID())
                .paymentKey(savedPayment.getPaymentKey())
                .method(savedPayment.getMethod())
                .amount(savedPayment.getAmount())
                .status(savedPayment.getStatus().name())
                .receiptUrl(savedPayment.getReceiptUrl())
                .build();
    }

    /**
     * TossPayment 상태 업데이트
     */
    public void updateTossPaymentStatus(Long id, String status) {
        TossPayment tossPayment = tossPaymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("TossPayment ID not found: " + id));

        PaymentStatus newStatus = PaymentStatus.valueOf(status.toUpperCase());
        tossPayment.changeStatus(newStatus); // 상태 변경 로직 호출
        tossPaymentRepository.save(tossPayment);


    }

    /**
     * TossPayment 검증
     */
    public TossPaymentResponseDTO confirmPayment(String paymentKey) {
        // Toss API 호출 URL 로그 추가
        log.info("Requesting Toss API with URL: https://api.tosspayments.com/v1/payments/{}", paymentKey);

        // Toss API 호출 및 응답 처리
        Map<String, Object> response = webClient.get()
                .uri(paymentKey)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .doOnNext(error -> log.error("Toss API Error Response: {}", error)) // 에러 응답 로그
                                .map(error -> new IllegalArgumentException("Toss API Error: " + error))
                )
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .doOnNext(res -> log.info("Toss API Response: {}", res)) // 정상 응답 로그
                .block();

        // 응답 데이터 처리
        String status = (String) response.get("status");
        String method = (String) response.get("method");
        BigDecimal amount = new BigDecimal(response.get("amount").toString());
        String receiptUrl = (String) response.get("receiptUrl");

        if (!"DONE".equals(status)) {
            throw new IllegalArgumentException("Payment not completed: " + status);
        }

        // TossPayment 업데이트
        TossPayment tossPayment = tossPaymentRepository.findByPaymentKey(paymentKey)
                .orElseThrow(() -> new IllegalArgumentException("PaymentKey not found: " + paymentKey));

        tossPayment.completePayment(method, amount, receiptUrl);

        TossPayment updatedPayment = tossPaymentRepository.save(tossPayment);

        // 결과 반환
        return TossPaymentResponseDTO.builder()
                .tossPaymentId(updatedPayment.getTossPaymentID())
                .orderId(updatedPayment.getOrder().getOrderID())
                .paymentKey(updatedPayment.getPaymentKey())
                .method(updatedPayment.getMethod())
                .amount(updatedPayment.getAmount())
                .status(updatedPayment.getStatus().name())
                .receiptUrl(updatedPayment.getReceiptUrl())
                .build();
    }
}



