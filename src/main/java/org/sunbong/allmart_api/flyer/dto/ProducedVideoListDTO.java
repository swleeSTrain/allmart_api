package org.sunbong.allmart_api.flyer.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProducedVideoListDTO {
    private String fileName;
    private String size;
    private LocalDate uploadDate;
    private String link;
    private String originalFile;
    private String memo;
}
