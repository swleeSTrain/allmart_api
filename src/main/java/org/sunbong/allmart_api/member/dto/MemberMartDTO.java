package org.sunbong.allmart_api.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberMartDTO {

    private Long martID;
    private String martName;
    private String logoURL;
}
