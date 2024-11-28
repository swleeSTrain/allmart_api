package org.sunbong.allmart_api.member.dto;

import lombok.Data;

@Data
public class MemberDTO {

    private String email;

    private String pw;

    private String role;

    private Long martID;

}
