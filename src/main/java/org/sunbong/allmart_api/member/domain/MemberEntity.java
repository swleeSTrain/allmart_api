package org.sunbong.allmart_api.member.domain;

import jakarta.persistence.*;
import lombok.*;
import org.sunbong.allmart_api.mart.domain.Mart;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(exclude = "mart")
public class MemberEntity {

    @Id
    private String email;

    private String pw;

    private MemberRole role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "martID", nullable = false)
    private Mart mart;

}
