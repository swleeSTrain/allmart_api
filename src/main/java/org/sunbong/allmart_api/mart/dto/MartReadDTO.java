package org.sunbong.allmart_api.mart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MartReadDTO {

    private Long martID;
    private String martName;
    private String phoneNumber;
    private String template;
    private String address;
    private String certificate;

    private List<String> attachLogo;

    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
}
