package org.sunbong.allmart_api.customer.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerSignInRequestDTO {
    private String email;
    private String phoneNumber;
}
