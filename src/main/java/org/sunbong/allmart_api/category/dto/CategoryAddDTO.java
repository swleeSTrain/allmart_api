package org.sunbong.allmart_api.category.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class CategoryAddDTO {

    private Long categoryID;

    @NotNull
    private String name;

}