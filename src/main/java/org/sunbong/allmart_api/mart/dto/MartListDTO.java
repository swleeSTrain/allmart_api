package org.sunbong.allmart_api.mart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MartListDTO {

    private Long martID;
    private String martName;
    private String phoneNumber;
    private String address;

    private String thumbnailImage; // 썸네일 이미지 URL
}
