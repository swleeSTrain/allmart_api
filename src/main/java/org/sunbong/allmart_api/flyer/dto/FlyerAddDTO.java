package org.sunbong.allmart_api.flyer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.sunbong.allmart_api.flyer.domain.FlyerImage;

import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlyerAddDTO {

    @NotBlank(message = "제목은 필수 항목입니다.")
    @Size(max=100, message="제목은 100자를 초과할 수 없습니다.")
    private String title;

    @NotBlank(message = "내용은 필수 항목입니다.")
    private String content;

    private Set<String> audioURL;
    private List<FlyerImage> attachImages;
}
