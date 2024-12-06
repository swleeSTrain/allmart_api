package org.sunbong.allmart_api.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SKURequestDTO {
    private List<String> skuList;
    private int page; // 페이지 번호
    private int size; // 페이지 크기
}
