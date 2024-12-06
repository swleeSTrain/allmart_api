package org.sunbong.allmart_api.tosspay.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.sunbong.allmart_api.tosspay.domain.TossPayment;
import org.sunbong.allmart_api.tosspay.dto.TossPaymentRequestDTO;
import org.sunbong.allmart_api.tosspay.dto.TossPaymentResponseDTO;
import org.sunbong.allmart_api.tosspay.repository.TossPaymentRepository;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class TossPaymentService {
    private final TossPaymentRepository tossPaymentRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${toss.SECRET_KEY}")
    private String tossSecretKey;


    public TossPaymentService(TossPaymentRepository tossPaymentRepository) {
        this.tossPaymentRepository = tossPaymentRepository;
    }

    public TossPaymentResponseDTO confirmPayment(TossPaymentRequestDTO requestDTO) {
        // Toss API 호출
        String authorizations = createAuthorizationHeader(tossSecretKey);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authorizations);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<TossPaymentRequestDTO> request = new HttpEntity<>(requestDTO, headers);
        ResponseEntity<TossPaymentResponseDTO> responseEntity =
                restTemplate.postForEntity("https://api.tosspayments.com/v1/payments/confirm", request, TossPaymentResponseDTO.class);
        TossPaymentResponseDTO responseDTO = responseEntity.getBody();

        // 결제 정보 저장
        TossPayment payment = TossPayment.builder()
                .paymentKey(requestDTO.getPaymentKey())
                .orderId(requestDTO.getOrderId())
                .amount(Long.parseLong(requestDTO.getAmount()))
                .status(responseDTO.getStatus())
                .method(responseDTO.getMessage()) // 예: 카드
                .receiptUrl(responseDTO.getPaymentKey()) // URL 가져오기
                .build();

        tossPaymentRepository.save(payment);
        return responseDTO;
    }

    private String createAuthorizationHeader(String secretKey) {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedBytes);



    }
}
