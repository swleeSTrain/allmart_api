package org.sunbong.allmart_api.tosspay.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.sunbong.allmart_api.tosspay.dto.TossPaymentRequestDTO;
import org.sunbong.allmart_api.tosspay.dto.TossPaymentResponseDTO;
import org.sunbong.allmart_api.tosspay.service.TossPaymentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/toss-payments")
public class TossPaymentController {

    private final TossPaymentService tossPaymentService;

    @PostMapping("/confirm")
    public ResponseEntity<TossPaymentResponseDTO> confirmPayment(@RequestBody TossPaymentRequestDTO requestDTO) {
        try {
            TossPaymentResponseDTO responseDTO = tossPaymentService.confirmPayment(requestDTO);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    TossPaymentResponseDTO.builder()
                            .status("error")
                            .message(e.getMessage())
                            .build()
            );
        }
    }
}