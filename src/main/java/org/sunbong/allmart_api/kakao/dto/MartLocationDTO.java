package org.sunbong.allmart_api.kakao.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MartLocationDTO {
    private String name;
    private double lat;
    private double lng;
}
