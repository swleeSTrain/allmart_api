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


@RestController
@RequestMapping("/api/v1/qrcode")
@RequiredArgsConstructor
public class QrController {

    final private QrService qrService;

    @GetMapping("/generate")
    public String generateQRCode() {
        String customUrl = "https://www.allmartservice.shop/";
        String qrCodeUrl = qrService.generateQRCodeForCustomURL(customUrl);

        if (qrCodeUrl != null) {
            return "QR Code generated successfully! Access it at: " + qrCodeUrl;
        } else {
            return "Failed to generate QR Code.";
        }
    }
}
