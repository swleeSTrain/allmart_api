package org.sunbong.allmart_api.customer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.sunbong.allmart_api.customer.domain.Customer;
import org.sunbong.allmart_api.customer.domain.CustomerLoginType;
import org.sunbong.allmart_api.customer.dto.CustomerUpdateDTO;
import org.sunbong.allmart_api.customer.repository.search.CustomerSearch;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long>, CustomerSearch {

    Optional<Customer> findByPhoneNumber(String phoneNumber);

    @Query("SELECT c FROM Customer c WHERE c.name = :name")
    Optional<List<Customer>> findByName(@Param("name")String name);

    void deleteByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumber(String phoneNumber);


}
