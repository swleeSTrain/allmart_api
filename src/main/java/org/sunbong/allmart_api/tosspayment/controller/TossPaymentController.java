package org.sunbong.allmart_api.tosspayment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.sunbong.allmart_api.order.repository.OrderJpaRepository;
import org.sunbong.allmart_api.tosspayment.dto.TossPaymentCreateDTO;
import org.sunbong.allmart_api.tosspayment.dto.TossPaymentResponseDTO;
import org.sunbong.allmart_api.tosspayment.dto.TossPaymentStatusUpdateDTO;
import org.sunbong.allmart_api.tosspayment.service.TossPaymentService;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/toss-payments")
@RequiredArgsConstructor
public class TossPaymentController {

    private final TossPaymentService tossPaymentService;

    @PostMapping("/create")
    public ResponseEntity<TossPaymentResponseDTO> createTossPayment(
            @RequestBody TossPaymentCreateDTO request) {
        TossPaymentResponseDTO response = tossPaymentService.createTossPayment(request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateTossPaymentStatus(
            @PathVariable Long id,
            @RequestBody TossPaymentStatusUpdateDTO request) {
        tossPaymentService.updateTossPaymentStatus(id, request.getStatus());
        return ResponseEntity.ok("Toss Payment status updated");
    }

    @PostMapping("/confirm")
    public ResponseEntity<TossPaymentResponseDTO> confirmPayment(
            @RequestBody Map<String, String> requestBody) {
        String paymentKey = requestBody.get("paymentKey");
        return ResponseEntity.ok(tossPaymentService.confirmPayment(paymentKey));
    }


}
