package org.sunbong.allmart_api.customer.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.sunbong.allmart_api.customer.domain.Customer;
import org.sunbong.allmart_api.customer.dto.CustomerListDTO;
import org.sunbong.allmart_api.customer.dto.CustomerRegisterDTO;
import org.sunbong.allmart_api.customer.dto.CustomerRequestDTO;
import org.sunbong.allmart_api.customer.service.CustomerService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/v1/customer")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public ResponseEntity<List<CustomerListDTO>> getAllCustomers() {
        List<CustomerListDTO> customers = customerService.getAllAddressesWithCustomer();
        return ResponseEntity.ok(customers);
    }


// 기존 getMapping
//    @GetMapping
//    public ResponseEntity<List<Customer>> getAllCustomers() {
//        List<Customer> customers = customerService.getAllCustomers();
//        return ResponseEntity.ok(customers);
//    }




    // 기존 전화번호 설정 페이지
    @GetMapping("/signUp/phoneNumber")
    public ResponseEntity<String> phoneNumberSignUp() {
        return ResponseEntity.ok("전화번호설정 페이지?");
    }

    // 기존 전화번호 기반 가등록 로직
    @PostMapping("/signUp/phoneNumber/{phoneNumber}")
    public ResponseEntity<Void> phoneNumberSignUp(@PathVariable("phoneNumber") String phoneNumber) {
        CustomerRequestDTO customerRequestDTO = CustomerRequestDTO.builder()
                .phoneNumber(phoneNumber)
                .build();

        Optional<Customer> customer = customerService.addMemberWithPhoneNumber(customerRequestDTO);
        Long customerId = customer.get().getCustomerID();

        log.info("전화번호 가등록 회원 생성");
        return ResponseEntity.status(303).header("Location", "/api/v1/qrcode/signUp?phoneNumber=" + phoneNumber + "&customerID=" + customerId)
                .build();
    }

    // 새로운 고객 및 주소 등록 로직
    @PostMapping("/register")
    public ResponseEntity<String> registerCustomer(@RequestBody CustomerRegisterDTO customerRegisterDTO) {
        Customer registeredCustomer = customerService.registerCustomerWithAddress(customerRegisterDTO);
        log.info("Customer and Address registered successfully: {}", registeredCustomer);
        return ResponseEntity.ok("Customer and Address registered successfully!");
    }
}
