package org.sunbong.allmart_api.mart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MartEditDTO {

    private String martName;
    private String phoneNumber;
    private String template;
    private String address;
    private String certificate;

    private List<String> existingFileNames;     // 기존 파일 이름
    private List<MultipartFile> files;  // 새로 업로드된 파일
}
