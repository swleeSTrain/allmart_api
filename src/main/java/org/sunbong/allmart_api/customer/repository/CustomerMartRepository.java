package org.sunbong.allmart_api.customer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.sunbong.allmart_api.customer.domain.CustomerMart;

public interface CustomerMartRepository extends JpaRepository<CustomerMart, Long> {
}
