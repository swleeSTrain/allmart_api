package org.sunbong.allmart_api.qrcode.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Log4j2
public class QrResponseDto {

    @NotNull
    private String fileName;

    @NotNull
    private String data;

    @NotNull
    private LocalDateTime createTime;

    @NotNull
    private LocalDateTime expireTime;

    @NotNull
    private String qrCodeURL;

    @Builder.Default
    private boolean isExpired = false;
}
