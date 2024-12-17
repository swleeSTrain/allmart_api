package org.sunbong.allmart_api.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    @JsonProperty("orderId")
    private Long orderId;

    @JsonProperty("customerId")
    private String customerId;
}