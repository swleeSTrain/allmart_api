package org.sunbong.allmart_api.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CustomerTokenResponseDTO {

    private String email;
    private String name;
    private String phoneNumber;
    private Long customerID;
    private Long martID;

    private String accessToken;
    private String refreshToken;
}
