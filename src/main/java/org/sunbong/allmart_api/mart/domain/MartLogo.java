package org.sunbong.allmart_api.mart.domain;

import jakarta.persistence.*;
import lombok.*;

@Embeddable
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MartLogo {

    @Column(name = "logoURL", length = 255, nullable = false)
    private String logoURL; // 로고 URL

    int ord;
}