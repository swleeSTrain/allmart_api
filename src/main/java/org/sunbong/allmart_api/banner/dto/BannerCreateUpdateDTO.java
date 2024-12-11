package org.sunbong.allmart_api.banner.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BannerCreateUpdateDTO {
    private String title;
    private String link;
    private String content;
    private String image;
    private Long martId;
}
