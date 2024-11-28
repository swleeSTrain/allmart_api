package org.sunbong.allmart_api.customer.service;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.sunbong.allmart_api.customer.domain.Customer;
import org.sunbong.allmart_api.customer.domain.CustomerLoginType;
import org.sunbong.allmart_api.customer.dto.*;
import org.sunbong.allmart_api.customer.exception.CustomerExceptions;
import org.sunbong.allmart_api.customer.repository.CustomerRepository;
import org.sunbong.allmart_api.customer.repository.search.CustomerSearch;
import org.sunbong.allmart_api.customer.repository.search.CustomerSearchImpl;
import org.sunbong.allmart_api.security.util.JWTUtil;

import java.util.*;


@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;

    private final CustomerSearch customerSearch;

    private final JWTUtil jwtUtil;

    // 조회
    // 1.전화번호 조회
    public Optional<Customer> findByPhoneNumber(String phoneNumber) {

        Optional<Customer> customerOne = customerRepository.findByPhoneNumber(phoneNumber);
        if (customerOne.isPresent()) {
            return customerOne;
        }
        return Optional.empty();
    }
    // 2. 이름 조회
    public Optional<List<Customer>> findByNames(String name) {
        Optional<List<Customer>> customers = customerRepository.findByName(name);
        if (customers.isPresent()) {
            customers.get().forEach(customer -> {
                log.info("Customer: " + customer.getName());
            });
            return customers;
        }
        return Optional.empty();
    }

    //3. 리스트 조회
    public Page<Customer> list (Pageable pageable) {
        Page<Customer> list = customerRepository.list(pageable);
        return list;
    }

    // 생성

    // 1.간편 전화번호 고객 생성
    public Optional<Customer> addMemberWithPhoneNumber(CustomerRequestDTO customerRequestDTO) {

        Customer customer = Customer.builder()
                .phoneNumber(customerRequestDTO.getPhoneNumber())
                .loginType(CustomerLoginType.PHONE)
                .build();
        Customer savedCustomer = customerRepository.save(customer);
        return Optional.of(savedCustomer);
    }

    //----------------------------------------------------------------

    //삭제

    // 1.아이디로 삭제 (관리자용)
    public void deleteCustomerById(Long id) {
        customerRepository.deleteById(id);
    }

    // 2.폰번호로 삭제 (관리자용)
    public void deleteCustomerByPhoneNumber(String phoneNumber) {
        customerRepository.deleteByPhoneNumber(phoneNumber);
    }

    // 3.회원 삭제
    public void deleteAllCustomers() {
        customerRepository.deleteAll();
    }

    //----------------------------------------------------------------

    //수정
    public Optional<Customer> update(CustomerUpdateDTO updateDTO) {
        //조회
        Optional<Customer> customerOptional = findByPhoneNumber(updateDTO.getPhoneNumber());
        Customer customer = customerOptional.orElseThrow(() -> new NoSuchElementException("Customer not found"));
            Customer updatedCustomer = customer.toBuilder()
                    .phoneNumber(updateDTO.getPhoneNumber())
                    .name(updateDTO.getName())
                    .build();
            customerRepository.save(updatedCustomer);
            return Optional.of(updatedCustomer);
        }


    // 중복가입 여부 확인
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


    public CustomerResponseDTO authenticate(String userData, CustomerLoginType loginType) {

        CustomerResponseDTO findedCustomerDTO = customerSearch.findByPhoneNumberOrEmail(userData, loginType);

        Customer customer = Customer.builder()
                .email(findedCustomerDTO.getEmail())
                .phoneNumber(findedCustomerDTO.getPhoneNumber())
                .loginType(loginType)
                .build();

        if( customer == null) {
            throw CustomerExceptions.BAD_AUTH.get();
        }

        CustomerResponseDTO customerDTO = new CustomerResponseDTO();
        if(loginType == CustomerLoginType.PHONE){
            customerDTO.setPhoneNumber(customer.getPhoneNumber());
        }else   customerDTO.setEmail(customer.getEmail());

        return customerDTO;
    }

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

        CustomerResponseDTO customerDTO = authenticate(userData, loginType);

        if (customerDTO == null) {
            throw CustomerExceptions.BAD_AUTH.get();
        }

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
                .phoneNumber(customerDTO.getPhoneNumber())
                .build();

        return tokenResponseDTO;
    }

    public CustomerResponseDTO verify(String token) {
        try {
            // JWT 토큰 검증
            Map<String, Object> claims = jwtUtil.validateToken(token);

            // 클레임에서 이메일 또는 전화번호 추출
            String email = (String) claims.get("email");
            String phoneNumber = (String) claims.get("phoneNumber");

            // 이메일과 전화번호 중 하나가 반드시 존재해야 함
            if (email == null && phoneNumber == null) {
                throw CustomerExceptions.INVALID_TOKEN.get();
            }

            // 로그인 타입 결정
            CustomerLoginType loginType = (email != null) ? CustomerLoginType.SOCIAL : CustomerLoginType.PHONE;
            String userData = (loginType == CustomerLoginType.SOCIAL) ? email : phoneNumber;

            // 데이터베이스에서 고객 정보 조회
            CustomerResponseDTO customerDTO = authenticate(userData, loginType);

            if (customerDTO == null) {
                throw CustomerExceptions.INVALID_TOKEN.get();
            }

            // 성공적으로 검증된 고객 정보 반환
            return customerDTO;

        } catch (JwtException e) {
            // JWT 예외 발생 시 로그 남기고 예외 던지기
            log.error("JWT verification failed: {}", e.getMessage());
            throw CustomerExceptions.INVALID_TOKEN.get();
        }
    }




}
