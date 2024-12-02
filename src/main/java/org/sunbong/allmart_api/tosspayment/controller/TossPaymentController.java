package org.sunbong.allmart_api.tosspayment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.sunbong.allmart_api.order.repository.OrderJpaRepository;
import org.sunbong.allmart_api.tosspayment.dto.TossPaymentCreateDTO;
import org.sunbong.allmart_api.tosspayment.dto.TossPaymentResponseDTO;
import org.sunbong.allmart_api.tosspayment.dto.TossPaymentStatusUpdateDTO;
import org.sunbong.allmart_api.tosspayment.service.TossPaymentService;

@RestController
@RequestMapping("/api/v1/toss-payments")
@RequiredArgsConstructor
public class TossPaymentController {

    private final TossPaymentService tossPaymentService;
    private final OrderJpaRepository orderJpaRepository;


    @PostMapping("/create")
    public ResponseEntity<TossPaymentResponseDTO> createTossPayment(
            @RequestBody TossPaymentCreateDTO request) {
        // TossPayment 생성 요청
        TossPaymentResponseDTO response = tossPaymentService.createTossPayment(request);
        return ResponseEntity.ok(response);
    }


    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateTossPaymentStatus(
            @PathVariable Long id,
            @RequestBody TossPaymentStatusUpdateDTO request) {
        // TossPayment 상태 업데이트 요청
        tossPaymentService.updateTossPaymentStatus(id, request.getStatus());
        return ResponseEntity.ok("Toss Payment status updated");
    }
}
