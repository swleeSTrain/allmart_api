package org.sunbong.allmart_api.mart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.sunbong.allmart_api.mart.domain.MartProduct;

public interface MartProductRepository extends JpaRepository<MartProduct, Long> {

    MartProduct findByMartMartIDAndProductProductID(Long martID, Long productID);
}
