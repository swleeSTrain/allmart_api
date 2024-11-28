package org.sunbong.allmart_api.member.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class MemberEntity {

    @Id
    private String email;

    private String pw;

    private MemberRole role;

    private String phoneNumber;

}
