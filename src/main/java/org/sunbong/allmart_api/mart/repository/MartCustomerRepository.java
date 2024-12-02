package org.sunbong.allmart_api.mart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.sunbong.allmart_api.mart.domain.MartCustomer;


public interface MartCustomerRepository extends JpaRepository<MartCustomer, Long> {

}
