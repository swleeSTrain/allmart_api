package org.sunbong.allmart_api.delivery.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.sunbong.allmart_api.delivery.domain.DeliveryStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryStatusUpdateDTO {

    @NotNull(message = "Delivery ID cannot be null")
    private Long deliveryId;

    @NotNull(message = "Delivery status cannot be null")
    private DeliveryStatus status;
}