package org.sunbong.allmart_api.category.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_category")
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryID;

    @Column(length = 100, nullable = false)
    private String name;

}
