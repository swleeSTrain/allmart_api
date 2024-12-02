package org.sunbong.allmart_api.member.domain;

import jakarta.persistence.*;
import lombok.*;
import org.sunbong.allmart_api.mart.domain.Mart;

@Entity
@Table(name = "tbl_member")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(exclude = "mart")
public class MemberEntity {

    @Id
    @Column(length = 50, nullable = false)
    private String email;

    @Column(length = 255, nullable = false)
    private String pw;

    @Column(nullable = false)
    private MemberRole role;

    @Column(length = 20, nullable = false)
    private String phoneNumber;

    // 시스템 관리자는 martID를 안 넣고 생성
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "martID")
    private Mart mart;

    @Builder.Default
    private Boolean delFlag = false;

    public void softDelete() {
        this.delFlag = true;
    }

}
