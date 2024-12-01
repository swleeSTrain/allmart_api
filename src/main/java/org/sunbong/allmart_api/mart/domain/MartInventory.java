package org.sunbong.allmart_api.mart.domain;

import jakarta.persistence.*;
import lombok.*;
import org.sunbong.allmart_api.common.domain.BaseEntity;
import org.sunbong.allmart_api.inventory.domain.Inventory;

@Entity
@Table(name = "tbl_mart_inventory")
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"mart", "inventory"})
public class MartInventory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long martInventoryID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "martID", nullable = false)
    private Mart mart; // 연관된 마트

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventoryID", nullable = false)
    private Inventory inventory; // 연관된 상품

    @Builder.Default
    private Boolean delFlag = false;

    public void softDelete() {
        this.delFlag = true;
    }
}
