package org.sunbong.allmart_api.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerMartDTO {

    private Long martID;      // 마트 ID
    private String martName;  // 마트 이름
    private String logoURL;   // 마트 로고 URL
}
