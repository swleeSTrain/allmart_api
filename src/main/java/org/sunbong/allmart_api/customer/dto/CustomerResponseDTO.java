package org.sunbong.allmart_api.customer.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerResponseDTO {

    private Long customerID;
    private Long martID;  // 하나의 마트 ID만 반환
    private String phoneNumber;
    private String email;
    private String name;
    private int loyaltyPoint;

}
