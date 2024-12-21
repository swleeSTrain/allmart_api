package org.sunbong.allmart_api.common.util;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.coobird.thumbnailator.Thumbnails;

@Component
@Log4j2
@RequiredArgsConstructor
public class CustomFileUtil {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private static final String FILES_PREFIX = "files/";
    private static final String QR_PREFIX = "qr/";

    public List<String> saveFiles(List<MultipartFile> files) throws RuntimeException {
        if (files == null || files.isEmpty()) {
            return List.of();
        }

        List<String> uploadUrls = new ArrayList<>();

        for (MultipartFile multipartFile : files) {
            String savedName = UUID.randomUUID().toString() + "_" + multipartFile.getOriginalFilename();
            String fullPath = FILES_PREFIX + savedName;

            try {
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentType(multipartFile.getContentType());
                metadata.setContentLength(multipartFile.getSize());

                amazonS3Client.putObject(new PutObjectRequest(bucket, fullPath, multipartFile.getInputStream(), metadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));

                String fileUrl = getFileUrl(savedName);
                uploadUrls.add(fileUrl);

                String contentType = multipartFile.getContentType();
                if (contentType != null && contentType.startsWith("image")) {
                    ByteArrayOutputStream thumbnailOutput = new ByteArrayOutputStream();
                    Thumbnails.of(multipartFile.getInputStream())
                            .size(400, 400)
                            .toOutputStream(thumbnailOutput);

                    byte[] thumbnailBytes = thumbnailOutput.toByteArray();
                    InputStream thumbnailInput = new ByteArrayInputStream(thumbnailBytes);

                    ObjectMetadata thumbnailMetadata = new ObjectMetadata();
                    thumbnailMetadata.setContentType(contentType);
                    thumbnailMetadata.setContentLength(thumbnailBytes.length);

                    String thumbnailPath = FILES_PREFIX + "s_" + savedName;
                    amazonS3Client.putObject(new PutObjectRequest(bucket, thumbnailPath, thumbnailInput, thumbnailMetadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead));

                    String thumbnailUrl = getFileUrl("s_" + savedName);
                    uploadUrls.add(thumbnailUrl);
                }

            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        return uploadUrls;
    }

    public ResponseEntity<Resource> getFile(String fileName) {
        try {
            com.amazonaws.services.s3.model.S3Object s3Object = amazonS3Client.getObject(bucket, fileName);
            InputStream inputStream = s3Object.getObjectContent();
            Resource resource = new InputStreamResource(inputStream);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(s3Object.getObjectMetadata().getContentType()));

            return ResponseEntity.ok().headers(headers).body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    public void deleteFiles(List<String> fileNames) {
        if (fileNames == null || fileNames.size() == 0) {
            return;
        }

        fileNames.forEach(fileName -> {
            amazonS3Client.deleteObject(bucket, fileName);
            amazonS3Client.deleteObject(bucket, "s_" + fileName);
        });
    }
    //QR코드 저장
    public void saveFilesToS3(String fileName, InputStream inputStream, long contentLength, String contentType) {
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(contentType);
            metadata.setContentLength(contentLength);

            amazonS3Client.putObject(new PutObjectRequest(bucket, QR_PREFIX+fileName, inputStream, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (Exception e) {
            throw new RuntimeException("S3 업로드 실패: " + e.getMessage(), e);
        }
    }

    public String getFileUrl(String fileName) {
        return amazonS3Client.getUrl(bucket, FILES_PREFIX + fileName).toString();
    }
    public String getQrUrl(String fileName) {
        return amazonS3Client.getUrl(bucket, QR_PREFIX + fileName).toString();
    }
}