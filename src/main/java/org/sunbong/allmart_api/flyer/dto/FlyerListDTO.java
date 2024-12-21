package org.sunbong.allmart_api.flyer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlyerListDTO {
    private Long flyerID;
    private String title;
    private String content;
    private Boolean progress;
    private String thumbnailImage;// 썸네일 이미지 URL
    private String martName; // 마트 이름 추가

//    private int audioCount;
}
