package org.sunbong.allmart_api.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerSocialRegisterDTO {

    private String name;        // 고객 이름
    private String email; // 고객 이메일
    private String postcode;    // 우편번호
    private String roadAddress; // 도로명 주소
    private String detailAddress; // 상세 주소
    private String fullAddress;  // 전체 주소
    private Long martID;
}