package org.sunbong.allmart_api.product.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Embeddable
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ProductImage {

    @Column(length = 1000)
    private String imageURL;

    private int ord;
}
