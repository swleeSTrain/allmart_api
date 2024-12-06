package org.sunbong.allmart_api.customer.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.sunbong.allmart_api.address.dto.AddressDTO;
import org.sunbong.allmart_api.address.repository.AddressRepository;
import org.sunbong.allmart_api.address.service.AddressService;
import org.sunbong.allmart_api.customer.domain.Customer;
import org.sunbong.allmart_api.customer.domain.CustomerLoginType;
import org.sunbong.allmart_api.customer.dto.*;
import org.sunbong.allmart_api.customer.exception.CustomerExceptions;
import org.sunbong.allmart_api.mart.domain.MartCustomer;
import org.sunbong.allmart_api.customer.dto.CustomerListDTO;
import org.sunbong.allmart_api.customer.dto.CustomerRegisterDTO;
import org.sunbong.allmart_api.customer.dto.CustomerRequestDTO;
import org.sunbong.allmart_api.customer.dto.CustomerUpdateDTO;
import org.sunbong.allmart_api.mart.repository.MartCustomerRepository;
import org.sunbong.allmart_api.customer.repository.CustomerRepository;
import org.sunbong.allmart_api.security.util.JWTUtil;
import org.sunbong.allmart_api.mart.domain.Mart;
import org.sunbong.allmart_api.mart.repository.MartRepository;

import java.util.*;

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
    private final MartRepository martRepository;
    private final MartCustomerRepository martCustomerRepository;

    // 구조를 똑같이 가줘야 함 다른 것들은 Repository로 받아옴
    // 이것 때문에 꼬이는 문제 발생 전부 Repository로 통일해야함
//    private final CustomerSearch customerSearch;

    private final JWTUtil jwtUtil;

    public CustomerTokenResponseDTO signIn(CustomerSignInRequestDTO signInRequest, CustomerLoginType loginType) {
        // 입력값 확인
        if ((loginType == CustomerLoginType.PHONE && signInRequest.getPhoneNumber() == null) ||
                (loginType == CustomerLoginType.SOCIAL && signInRequest.getEmail() == null)) {
            throw CustomerExceptions.BAD_AUTH.get();
        }

        // 고객 조회 (전화번호 또는 이메일로)
        String userData = (loginType == CustomerLoginType.PHONE)
                ? signInRequest.getPhoneNumber()
                : signInRequest.getEmail();

        // findCustomerWithMart 메서드 사용
        Optional<CustomerResponseDTO> customerDTOOpt = customerRepository.findCustomerWithMart(userData, loginType);

        // 고객 정보가 없으면 예외 처리
        if (customerDTOOpt.isEmpty()) {
            throw CustomerExceptions.BAD_AUTH.get();
        }

        CustomerResponseDTO customerDTO = customerDTOOpt.get();

        // JWT 클레임 생성
        Map<String, Object> claims = new HashMap<>();
        if (loginType == CustomerLoginType.PHONE) {
            claims.put("phoneNumber", customerDTO.getPhoneNumber());
        } else {
            claims.put("email", customerDTO.getEmail());
        }

        // JWT 토큰 생성
        String accessToken = jwtUtil.createToken(claims, 60); // Access Token 유효 시간: 60분
        String refreshToken = jwtUtil.createToken(claims, 1440); // Refresh Token 유효 시간: 1440분 (1일)

        // 응답 DTO 생성
        CustomerTokenResponseDTO tokenResponseDTO = CustomerTokenResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .email(customerDTO.getEmail())
                .name(customerDTO.getName())
                .phoneNumber(customerDTO.getPhoneNumber())
                .martID(customerDTO.getMartID()) // 마트 ID 추가
                .customerID(customerDTO.getCustomerID()) // 고객 ID 추가
                .build();

        return tokenResponseDTO;
    }



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
                .loginType(CustomerLoginType.PHONE)
                .build();
        Customer savedCustomer = customerRepository.save(customer);
        return Optional.of(savedCustomer);
    }

    public Customer registerCustomer(CustomerRegisterDTO customerRegisterDTO) {
        // 1. Customer 생성 및 저장
        Customer customer = Customer.builder()
                .name(customerRegisterDTO.getName())
                .phoneNumber(customerRegisterDTO.getPhoneNumber())
                .loginType(CustomerLoginType.PHONE) // 로그인 타입 설정
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

        Mart mart = martRepository.findById(customerRegisterDTO.getMartID())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Mart ID: " + customerRegisterDTO.getMartID()));

        MartCustomer martCustomer = MartCustomer.builder()
                .mart(mart)
                .customer(savedCustomer)
                .build();
        martCustomerRepository.save(martCustomer);

        // 4. 저장된 Customer 반환
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

    public String createCookie(Customer customer, HttpServletResponse response) {
        Cookie cookie = new Cookie("customer", customer.getPhoneNumber());
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // Https환경에서만 전송( 개발 중에는 false 가능)
        cookie.setPath("/"); //모든 경로에서 유효
        cookie.setMaxAge(60 * 60); // 1시간

        response.addCookie(cookie);

        //응답에 쿠키 추가
        return "쿠키가 설정되었습니다";

    }

    // 여기서부터 주석단 부분은 customerSearch를 사용해서 꼬여서 주석처리함
//    public CustomerResponseDTO authenticate(String userData, CustomerLoginType loginType) {
//
//        CustomerResponseDTO findedCustomerDTO = customerSearch.findByPhoneNumberOrEmail(userData, loginType);
//
//        Customer customer = Customer.builder()
//                .email(findedCustomerDTO.getEmail())
//                .phoneNumber(findedCustomerDTO.getPhoneNumber())
//                .loginType(loginType)
//                .build();
//
//        if( customer == null) {
//            throw CustomerExceptions.BAD_AUTH.get();
//        }
//
//        CustomerResponseDTO customerDTO = new CustomerResponseDTO();
//        if(loginType == CustomerLoginType.PHONE){
//            customerDTO.setPhoneNumber(customer.getPhoneNumber());
//        }else   customerDTO.setEmail(customer.getEmail());
//
//        return customerDTO;
//    }

//    public CustomerResponseDTO verify(String token) {
//        try {
//            // JWT 토큰 검증
//            Map<String, Object> claims = jwtUtil.validateToken(token);
//
//            // 클레임에서 이메일 또는 전화번호 추출
//            String email = (String) claims.get("email");
//            String phoneNumber = (String) claims.get("phoneNumber");
//
//            // 이메일과 전화번호 중 하나가 반드시 존재해야 함
//            if (email == null && phoneNumber == null) {
//                throw CustomerExceptions.INVALID_TOKEN.get();
//            }
//
//            // 로그인 타입 결정
//            CustomerLoginType loginType = (email != null) ? CustomerLoginType.SOCIAL : CustomerLoginType.PHONE;
//            String userData = (loginType == CustomerLoginType.SOCIAL) ? email : phoneNumber;
//
//            // 데이터베이스에서 고객 정보 조회
//            CustomerResponseDTO customerDTO = authenticate(userData, loginType);
//
//            if (customerDTO == null) {
//                throw CustomerExceptions.INVALID_TOKEN.get();
//            }
//
//            // 성공적으로 검증된 고객 정보 반환
//            return customerDTO;
//
//        } catch (JwtException e) {
//            // JWT 예외 발생 시 로그 남기고 예외 던지기
//            log.error("JWT verification failed: {}", e.getMessage());
//            throw CustomerExceptions.INVALID_TOKEN.get();
//        }
//    }




}
