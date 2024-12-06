package org.sunbong.allmart_api.board.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.file.Files;

@RestController
@RequestMapping("/api/v1/files")
@Log4j2
@RequiredArgsConstructor
public class BoardFileController {

    private final static String folder = "C:\\upload\\";
    @GetMapping("download/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        try {
            File file = new File(folder + File.separator + filename);
            Resource resource = new UrlResource(file.toURI());

            // 파일이 존재하는지 확인
            if (!resource.exists()) {
                throw new RuntimeException("파일을 찾을 수 없습니다: " + filename);
            }

            // 콘텐츠 유형 결정
            String contentType = Files.probeContentType(file.toPath());
            if (contentType == null) {
                contentType = "application/octet-stream"; // 기본 콘텐츠 유형
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + URLEncoder.encode(filename, "UTF-8") + "\"")
                    .body(resource);
        } catch (MalformedURLException e) {
            throw new RuntimeException("파일을 찾을 수 없습니다: " + filename, e);
        } catch (IOException e) {
            throw new RuntimeException("파일의 콘텐츠 유형을 결정할 수 없습니다: " + filename, e);
        }
    }



    @GetMapping("view/{filename}")
    public ResponseEntity<Resource> viewFileImage(@PathVariable String filename) {
        try {
            // 파일을 지정된 경로에서 찾기
            File file = new File(folder + filename);
            Resource resource = new UrlResource(file.toURI());

            // 파일이 존재하지 않거나 읽을 수 없을 때 처리
            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("파일을 읽을 수 없거나 존재하지 않습니다: " + filename);
            }

            // 파일의 Content-Type 설정 (이미지 형식 확인)
            String contentType = Files.probeContentType(file.toPath());
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            // 이미지 파일을 브라우저에 표시하도록 `inline`으로 설정
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType)) // 파일의 실제 타입 설정
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);

        } catch (MalformedURLException e) {
            log.error("잘못된 파일 경로: " + filename, e);
            throw new RuntimeException("파일을 찾을 수 없습니다: " + filename, e);
        } catch (Exception e) {
            log.error("파일 처리 중 오류 발생: " + filename, e);
            throw new RuntimeException("파일 처리 중 오류 발생", e);
        }
    }
}
