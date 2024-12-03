package org.sunbong.allmart_api.kakao.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MartMapDTO {

    private Long martID;    // 마트 ID
    private String martName; // 마트 이름
    private double lat;      // 위도
    private double lng;      // 경도
}
