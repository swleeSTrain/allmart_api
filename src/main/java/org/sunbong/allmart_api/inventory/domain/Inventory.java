package org.sunbong.allmart_api.inventory.domain;

import jakarta.persistence.*;
import lombok.*;
import org.sunbong.allmart_api.common.domain.BaseEntity;
import org.sunbong.allmart_api.product.domain.Product;

@Entity
@Table(
        name = "tbl_inventory"
//        indexes = @Index(name = "idx_inventory_sku", columnList = "sku")
)
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"product"})
public class Inventory extends BaseEntity {

    // 기본 키 - inventoryID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inventoryID;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sku", referencedColumnName = "sku", nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 0;

    @Column(name = "inStock", nullable = true)
    private Integer inStock = 1;

    @Builder.Default
    private boolean delFlag = false;

    public void softDelete() {
        this.delFlag = true;
    }

}

