package org.sunbong.allmart_api.address.domain;

import jakarta.persistence.*;
import lombok.*;
import org.sunbong.allmart_api.common.domain.BaseEntity;
import org.sunbong.allmart_api.customer.domain.Customer;

@Entity
@Table(name = "tbl_address")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressID;

    @Column(nullable = false, length = 10)
    private String postcode; // 우편번호

    @Column(nullable = false, length = 255)
    private String roadAddress; // 도로명 주소

    @Column(nullable = false, length = 255)
    private String detailAddress; // 상세 주소

    @Column(length = 500)
    private String fullAddress; // 전체 주소 (도로명 주소 + 상세 주소)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerID", nullable = false)
    private Customer customer; // 단방향 관계 설정


}

