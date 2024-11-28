package org.sunbong.allmart_api.address.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.sunbong.allmart_api.address.domain.Address;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
    @Query("SELECT a FROM Address a JOIN FETCH a.customer")
    List<Address> findAllWithCustomer();
}
