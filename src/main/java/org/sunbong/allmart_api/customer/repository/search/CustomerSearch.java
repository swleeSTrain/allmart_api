package org.sunbong.allmart_api.customer.repository.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.sunbong.allmart_api.common.dto.PageRequestDTO;
import org.sunbong.allmart_api.common.dto.PageResponseDTO;
import org.sunbong.allmart_api.customer.domain.Customer;
import org.sunbong.allmart_api.customer.domain.CustomerLoginType;
import org.sunbong.allmart_api.customer.dto.CustomerListDTO;
import org.sunbong.allmart_api.customer.dto.CustomerResponseDTO;

import java.util.Optional;


public interface CustomerSearch {
    Page<Customer> list (Pageable pageable);
    PageResponseDTO<CustomerListDTO> listByName(String name, PageRequestDTO pageRequestDTO);
    PageResponseDTO<CustomerListDTO> listByPhoneNumber(String phoneNumber, PageRequestDTO pageRequestDTO);

    CustomerResponseDTO findByPhoneNumberOrEmail(String customerData, CustomerLoginType loginType);

    Optional<CustomerResponseDTO> findCustomerWithMart(String userData, CustomerLoginType loginType);
}
