package org.sunbong.allmart_api.mart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MartAddDTO {

    private String martName;
    private String phoneNumber;
    private String template;
    private String address;
    private String certificate;
    private List<MultipartFile> files;
}
