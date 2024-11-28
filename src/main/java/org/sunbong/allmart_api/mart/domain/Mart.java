package org.sunbong.allmart_api.mart.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.sunbong.allmart_api.common.domain.BaseEntity;
import org.sunbong.allmart_api.product.domain.ProductImage;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_mart")
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"attachLogo"})
public class Mart extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "martID")
    private Long martID; // 마트ID (기본키)

    @Column(name = "martName", length = 50, nullable = false)
    private String martName; // 이름

    @Column(name = "phoneNumber", length = 13, nullable = false)
    private String phoneNumber; // 전화번호

    @Column(name = "template", length = 20, nullable = false)
    private String template; // 템플릿

    @Column(name = "address", length = 500, nullable = false)
    private String address; // 주소

    @Column(name = "certificate", length = 255, nullable = false)
    private String certificate; // 사업자등록증

    @Builder.Default
    private boolean delFlag = false;

    @ElementCollection
    @CollectionTable(name = "tbl_mart_logo")
    @BatchSize(size = 50)
    @Builder.Default
    private List<MartLogo> attachLogo = new ArrayList<>();

    public void addLogo(String filename) {
        attachLogo.add(new MartLogo(filename, attachLogo.size()));
    }

    public void clearLogo() {
        attachLogo.clear();
    }

    public void softDelete() {
        this.delFlag = true;
    }
}