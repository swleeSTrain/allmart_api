package org.sunbong.allmart_api.mart.domain;

import jakarta.persistence.*;
import lombok.*;
import org.sunbong.allmart_api.common.domain.BaseEntity;
import org.sunbong.allmart_api.product.domain.Product;

@Entity
@Table(name = "tbl_mart_product")
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"mart", "product"})
public class MartProduct extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long martProductID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "martID", nullable = false)
    private Mart mart; // 연관된 마트

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productID", nullable = false)
    private Product product; // 연관된 상품

    @Builder.Default
    private Boolean delFlag = false;

    public void softDelete() {
        this.delFlag = true;
    }
}
