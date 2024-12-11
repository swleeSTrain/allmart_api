package org.sunbong.allmart_api.member.dto;

import lombok.Data;

@Data
public class TokenResponseDTO {
    private String email;
    private String accessToken;
    private String refreshToken;

    private String role; // 사용자 유형 추가
}
