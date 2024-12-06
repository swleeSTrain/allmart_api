package org.sunbong.allmart_api.qrcode.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.sunbong.allmart_api.qrcode.domain.QrCodeType;
import org.sunbong.allmart_api.qrcode.dto.QrRequestDto;
import org.sunbong.allmart_api.qrcode.dto.QrResponseDto;
import org.sunbong.allmart_api.qrcode.dto.QrSingUpResponseDTO;
import org.sunbong.allmart_api.qrcode.service.QrService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Log4j2
@RestController
@RequestMapping("/api/v1/qrcode")
@RequiredArgsConstructor
public class QrController {

    private final QrService qrService;

    @GetMapping("/generate")
    public ResponseEntity<String> generateQRCode(@RequestParam Long martID) {
        log.info("QR 코드 생성 요청 수신: martID = {}", martID);

        // martID를 포함한 URL 생성
        String customUrl = "https://www.allmartservice.shop/" + martID;
        log.info("생성된 URL: {}", customUrl);

        // QR 코드 생성 서비스 호출
        String qrCodeUrl = qrService.generateQRCodeForCustomURL(customUrl);

        if (qrCodeUrl != null) {
            return ResponseEntity.ok(qrCodeUrl); // 생성된 파일명 반환
        } else {
            return ResponseEntity.status(500).body("QR 코드 생성 실패");
        }
    }
}