package org.sunbong.allmart_api.order.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sunbong.allmart_api.common.domain.BaseEntity;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Table(name = "tbl_temporary_order")
public class TemporaryOrderEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tempOrderID;

    @Column(name = "customerID", nullable = false)
    private String customerId;

    @Column(name = "productName", nullable = false)
    private String productName;

    @Column(nullable = false)
    private int quantity;

    @Builder.Default
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TemporaryOrderStatus status = TemporaryOrderStatus.PENDING;

    public TemporaryOrderEntity markAsProcessed() {
        this.status = TemporaryOrderStatus.PROCESSED;
        return this;
    }
}
