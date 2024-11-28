package org.sunbong.allmart_api.customer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.sunbong.allmart_api.address.domain.Address;
import org.sunbong.allmart_api.address.dto.AddressDTO;
import org.sunbong.allmart_api.address.repository.AddressRepository;
import org.sunbong.allmart_api.address.service.AddressService;
import org.sunbong.allmart_api.customer.domain.Customer;
import org.sunbong.allmart_api.customer.dto.CustomerListDTO;
import org.sunbong.allmart_api.customer.dto.CustomerRegisterDTO;
import org.sunbong.allmart_api.customer.dto.CustomerRequestDTO;
import org.sunbong.allmart_api.customer.dto.CustomerUpdateDTO;
import org.sunbong.allmart_api.customer.repository.CustomerRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final AddressService addressService;
    private final AddressRepository addressRepository;

    // -----------------------------------------------------------------
    // 조회
    // -----------------------------------------------------------------

    // 1. 전화번호로 고객 조회
    public Optional<Customer> findByPhoneNumber(String phoneNumber) {
        return customerRepository.findByPhoneNumber(phoneNumber);
    }

    // 2. 이름으로 고객 목록 조회
    public Optional<List<Customer>> findByNames(String name) {
        Optional<List<Customer>> customers = customerRepository.findByName(name);
        customers.ifPresent(customerList ->
                customerList.forEach(customer -> log.info("Customer: " + customer.getName()))
        );
        return customers;
    }

    // 3. 전체 고객 목록 조회 (페이징)
    public Page<Customer> list(Pageable pageable) {
        return customerRepository.list(pageable);
    }

    public List<CustomerListDTO> getAllAddressesWithCustomer() {
        return addressRepository.findAllWithCustomer().stream()
                .map(address -> CustomerListDTO.builder()
                        .customerID(address.getCustomer().getCustomerID())
                        .name(address.getCustomer().getName()) // Customer의 이름
                        .phoneNumber(address.getCustomer().getPhoneNumber()) // Customer의 전화번호
                        .postcode(address.getPostcode()) // Address의 우편번호
                        .roadAddress(address.getRoadAddress()) // Address의 도로명 주소
                        .detailAddress(address.getDetailAddress()) // Address의 상세 주소
                        .fullAddress(address.getFullAddress()) // Address의 전체 주소
                        .build())
                .collect(Collectors.toList());
    }



//    // 주소포함 고객 조회 (기존)
//    public List<Customer> getAllCustomers() {
//        return customerRepository.findAll();
//    }

    // -----------------------------------------------------------------
    // 생성
    // -----------------------------------------------------------------

    // 1. 간편 전화번호 고객 생성
    public Optional<Customer> addMemberWithPhoneNumber(CustomerRequestDTO customerRequestDTO) {
        Customer customer = Customer.builder()
                .phoneNumber(customerRequestDTO.getPhoneNumber())
                .build();
        Customer savedCustomer = customerRepository.save(customer);
        return Optional.of(savedCustomer);
    }

    public Customer registerCustomerWithAddress(CustomerRegisterDTO customerRegisterDTO) {
        // 1. Customer 생성 및 저장
        Customer customer = Customer.builder()
                .name(customerRegisterDTO.getName())
                .phoneNumber(customerRegisterDTO.getPhoneNumber())
                .build();
        Customer savedCustomer = customerRepository.save(customer);

        log.info("Customer saved: {}", savedCustomer);

        // 2. Address 생성 및 저장
        AddressDTO addressDTO = AddressDTO.builder()
                .postcode(customerRegisterDTO.getPostcode())
                .roadAddress(customerRegisterDTO.getRoadAddress())
                .detailAddress(customerRegisterDTO.getDetailAddress())
                .fullAddress(customerRegisterDTO.getRoadAddress() + " " + customerRegisterDTO.getDetailAddress())
                .build();

        addressService.saveAddress(addressDTO, savedCustomer);

        log.info("Address saved for Customer: {}", savedCustomer.getCustomerID());

        // 3. 저장된 Customer 반환
        return savedCustomer;
    }

    // -----------------------------------------------------------------
    // 삭제
    // -----------------------------------------------------------------

    // 1. 고객 ID로 삭제
    public void deleteCustomerById(Long id) {
        customerRepository.deleteById(id);
    }

    // 2. 전화번호로 삭제
    public void deleteCustomerByPhoneNumber(String phoneNumber) {
        customerRepository.deleteByPhoneNumber(phoneNumber);
    }

    // 3. 전체 고객 삭제
    public void deleteAllCustomers() {
        customerRepository.deleteAll();
    }

    // -----------------------------------------------------------------
    // 수정
    // -----------------------------------------------------------------

    // 고객 정보 수정
    public Optional<Customer> update(CustomerUpdateDTO updateDTO) {
        // 고객 조회
        Optional<Customer> customerOptional = findByPhoneNumber(updateDTO.getPhoneNumber());
        Customer customer = customerOptional.orElseThrow(() -> new NoSuchElementException("Customer not found"));

        // 수정된 고객 저장
        Customer updatedCustomer = customer.toBuilder()
                .phoneNumber(updateDTO.getPhoneNumber())
                .name(updateDTO.getName())
                .build();
        customerRepository.save(updatedCustomer);
        return Optional.of(updatedCustomer);
    }

    // -----------------------------------------------------------------
    // 중복 가입 여부 확인
    // -----------------------------------------------------------------

    public void checkDuplicateRegistration(CustomerRequestDTO customerRequestDTO) {
        if (customerRepository.existsByPhoneNumber(customerRequestDTO.getPhoneNumber())) {
            throw new IllegalArgumentException("이미 등록된 전화번호입니다.");
        }
    }
}
