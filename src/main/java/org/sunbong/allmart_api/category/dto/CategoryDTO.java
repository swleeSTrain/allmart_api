package org.sunbong.allmart_api.category.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CategoryDTO {

    private Long categoryID;

    @NotNull
    private String name;

}