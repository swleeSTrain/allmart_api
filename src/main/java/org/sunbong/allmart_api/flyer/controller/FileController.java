package org.sunbong.allmart_api.flyer.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.sunbong.allmart_api.flyer.service.FileService;
import org.sunbong.allmart_api.flyer.service.FlyerService;

import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;


@Log4j2
@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService; // 파일 저장 및 경로를 처리하는 서비스

    private final String uploadDir = "C:/upload/uploads/";

    @GetMapping("/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        try {
            String decodedFileName = URLDecoder.decode(fileName, StandardCharsets.UTF_8);

            // 파일 경로 지정
            Path filePath = Paths.get(uploadDir).resolve(decodedFileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                log.error("파일을 찾을 수 없거나 읽을 수 없습니다: {}", decodedFileName);
                return ResponseEntity.notFound().build();
            }

            // ASCII 파일 이름 처리
            String asciiFileName = decodedFileName.replaceAll("[^\\x20-\\x7E]", "_"); // 한글, 특수문자 "_" 처리

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + asciiFileName + "\"")
                    .body(resource);

        } catch (MalformedURLException e) {
            log.error("파일 경로 오류: {}", fileName, e);
            return ResponseEntity.badRequest().build();
        }
    }

    // 영상 다운로드
    // FlyerVideoListComponent
    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadFileIn(@PathVariable String fileName) {
        log.info("Downloading file: {}", fileName);

        try {
            Resource resource = fileService.getFileResource(fileName);
            String contentDisposition = "attachment; filename=\"" + fileName + "\"";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                    .body(resource);

        } catch (Exception e) {
            log.error("Error downloading file: {}", fileName, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/image/{fileName}")
    public ResponseEntity<Resource> getFile(@PathVariable String fileName) {
        log.info("Fetching image file: {}", fileName);
        try {
            // 파일 경로 생성
            Path filePath = Paths.get(uploadDir).resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            log.info("Fetching file from path: {}", filePath.toUri());

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            // 적절한 Content-Type 설정
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG) // JPG 파일일 경우, 필요에 따라 변경
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
