package org.sunbong.allmart_api.product.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.sunbong.allmart_api.category.domain.Category;
import org.sunbong.allmart_api.common.domain.BaseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_product")
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = {"attachImages", "category"})
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productID;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 50, unique = true, nullable = false)
    private String sku;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    @Builder.Default
    private boolean delFlag = false;

    @ElementCollection
    @CollectionTable(name = "tbl_product_image")
    @BatchSize(size = 50)
    @Builder.Default
    private List<ProductImage> attachImages = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY) // 즉시 로딩하지 않고 필요할 때만 쿼리에서 실행
    @JoinColumn(name = "categoryid", nullable = false) // 외래 키 설정
    private Category category;

    public void addImage(String filename) {
        attachImages.add(new ProductImage(filename, attachImages.size()));
    }

    public void clearImages() {
        attachImages.clear();
    }

    public void softDelete() {
        this.delFlag = true;
    }
}

