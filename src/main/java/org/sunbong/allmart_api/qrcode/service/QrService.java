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


import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.*;

@Service
@Transactional
@Log4j2
@RequiredArgsConstructor
public class QrService {

    private static final String UPLOAD_DIR = "C:/upload/";

    public String generateQRCodeForCustomURL(String customUrl) {
        try {
            log.info("QR 코드 생성 시작: URL = {}", customUrl);

            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            String fileName = UUID.randomUUID().toString() + ".png";
            String filePath = UPLOAD_DIR + fileName;

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(customUrl, BarcodeFormat.QR_CODE, 300, 300, hints);

            File directory = new File(UPLOAD_DIR);
            if (!directory.exists() && !directory.mkdirs()) {
                throw new RuntimeException("디렉토리 생성 실패");
            }

            Path path = FileSystems.getDefault().getPath(filePath);
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);

            log.info("QR 코드 생성 성공: {}", filePath);
            return fileName;
        } catch (Exception e) {
            log.error("QR 코드 생성 실패", e);
            return null;
        }
    }
}
