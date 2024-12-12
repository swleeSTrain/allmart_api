package org.sunbong.allmart_api.qrcode.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.sunbong.allmart_api.common.util.CustomFileUtil;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.*;

@Service
@Transactional
@Log4j2
@RequiredArgsConstructor
public class QrService {

    private final CustomFileUtil customFileUtil;

    public String generateQRCodeForCustomURL(String customUrl) {
        try {
            log.info("QR 코드 생성 시작: URL = {}", customUrl);

            // QR 코드 생성 설정
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            String fileName = UUID.randomUUID().toString() + ".png";

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(customUrl, BarcodeFormat.QR_CODE, 300, 300, hints);

            // QR 코드를 ByteArrayOutputStream에 저장
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

            // S3에 업로드
            byte[] qrCodeBytes = outputStream.toByteArray();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(qrCodeBytes);
            customFileUtil.saveFilesToS3(fileName, inputStream, qrCodeBytes.length, "image/png");

            // 업로드된 파일의 URL 반환
            String fileUrl = customFileUtil.getQrUrl(fileName);
            log.info("QR 코드 생성 및 S3 업로드 성공: {}", fileUrl);
            return fileUrl;

        } catch (Exception e) {
            log.error("QR 코드 생성 실패", e);
            return null;
        }
    }
}
