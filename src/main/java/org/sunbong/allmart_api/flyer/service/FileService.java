package org.sunbong.allmart_api.flyer.service;


import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Log4j2
public class FileService {

    private static final String UPLOAD_DIR = "C:/upload/uploads/";
    private final Path fileStorageLocation = Paths.get(UPLOAD_DIR).toAbsolutePath().normalize();

    public String saveFile(MultipartFile file) throws IOException {
        // 디렉토리 확인 및 생성
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            boolean dirsCreated = uploadDir.mkdirs();
            if (!dirsCreated) {
                throw new IOException("업로드 디렉토리를 생성할 수 없습니다: " + UPLOAD_DIR);
            }
        }

        // 고유 파일명 생성
        String uniqueFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        String filePath = UPLOAD_DIR + uniqueFileName;

        // 파일 저장
        File destinationFile = new File(filePath);
        file.transferTo(destinationFile);

        return uniqueFileName;
    }

    public Resource getFileResource(String fileName) throws Exception {
        Path filePath = Paths.get(UPLOAD_DIR).resolve(fileName).normalize();
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            throw new Exception("File not found: " + fileName);
        }

        return resource;
    }

    


    public FileService() {

        try {
            Files.createDirectories(this.fileStorageLocation); // 파일 디렉토리 생성

        } catch (Exception ex) {
            log.error("파일 저장 디렉토리를 생성할 수 없습니다.", ex);
            throw new RuntimeException("파일 저장 디렉토리를 생성할 수 없습니다.");
        }
    }

    public Path getFileStorageLocation() {
        return fileStorageLocation;
    }
}
