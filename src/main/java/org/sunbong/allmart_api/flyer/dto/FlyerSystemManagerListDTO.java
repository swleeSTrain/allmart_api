package org.sunbong.allmart_api.flyer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.sunbong.allmart_api.flyer.domain.FlyerImage;
import org.sunbong.allmart_api.flyer.domain.ProducedVideo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlyerSystemManagerListDTO {

    private Long flyerID;
    private String title;
    private String content;
    private LocalDateTime createdDate;
    private String martName;
    private ProducedVideoDTO producedVideo;
    private Set<String> audioURL;

    private List<FlyerImage> attachImages;

    private String thumbnailImage; // 썸네일 이미지 URL

//    private int audioCount;
}
