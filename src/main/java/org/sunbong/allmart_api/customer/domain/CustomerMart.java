package org.sunbong.allmart_api.customer.domain;

import jakarta.persistence.*;
import lombok.*;
import org.sunbong.allmart_api.common.domain.BaseEntity;
import org.sunbong.allmart_api.mart.domain.Mart;

@Entity
@Table(name = "tbl_customer_mart")
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"mart", "customer"})
public class CustomerMart extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerMartID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "martID", nullable = false)
    private Mart mart; // 연관된 마트

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerID", nullable = false)
    private Customer customer; // 연관된 회원

}