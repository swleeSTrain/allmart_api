package org.sunbong.allmart_api.common.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDTO {
    @Builder.Default
    @Min(value = 1, message = "1도 없어.")
    private int page = 1;
    @Builder.Default
    @Min(value = 10, message = "10도 없어")
    @Max(value = 100, message = "cannot over 100")
    private int size = 10;

    private String keyword; // 검색어 필드 추가

    private String type; // 검색 타입 필드 추가

    private Long inventoryID;

    private Long categoryID;  // 카테고리 검색 필드 추가

    private String sort; // 정렬 조건 추가

    private Set<String> tags; // **태그 검색 필드 추가**
}
