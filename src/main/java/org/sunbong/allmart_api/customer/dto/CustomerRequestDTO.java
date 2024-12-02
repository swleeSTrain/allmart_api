package org.sunbong.allmart_api.customer.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.sunbong.allmart_api.customer.domain.CustomerLoginType;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerRequestDTO {

    private String phoneNumber;

    private String email;

    private CustomerLoginType loginType;

    private LocalDateTime createdDate;


}
