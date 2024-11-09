package org.sunbong.allmart_api.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductReadDTO {

    private Long productID;
    private String name;
    private String sku;
    private BigDecimal price;

    private List<String> attachFiles;

    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
}
