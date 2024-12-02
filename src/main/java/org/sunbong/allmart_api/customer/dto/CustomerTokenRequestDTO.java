package org.sunbong.allmart_api.customer.dto;

import lombok.Data;
import org.sunbong.allmart_api.customer.domain.CustomerLoginType;

@Data
public class CustomerTokenRequestDTO {

    private String email;

    private String phoneNumber;

    private CustomerLoginType loginType;

}
