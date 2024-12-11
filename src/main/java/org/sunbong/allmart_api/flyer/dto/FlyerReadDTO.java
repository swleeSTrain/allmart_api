package org.sunbong.allmart_api.flyer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlyerReadDTO {

    private Long flyerID;
    private String title;
    private String content;

    private List<String> attachImages;
    private Set<String> audioURL;

    private ProducedVideoDTO producedVideoDTO;

    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

}

