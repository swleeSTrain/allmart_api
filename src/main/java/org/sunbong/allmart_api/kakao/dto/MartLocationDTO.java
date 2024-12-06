package org.sunbong.allmart_api.kakao.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MartLocationDTO {

    private String addressName; // 도로명 주소
    private double x; // 경도 (lng)
    private double y; // 위도 (lat)
}
