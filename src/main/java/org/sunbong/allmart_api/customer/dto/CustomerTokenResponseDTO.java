package org.sunbong.allmart_api.customer.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CustomerTokenResponseDTO {
    private String email;
    private Long customerId;
    private String phoneNumber;
    private String accessToken;
    private String refreshToken;
    private Long martID;
    private String name;
    private int loyaltyPoints;
}
