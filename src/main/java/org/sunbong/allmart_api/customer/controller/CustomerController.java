package org.sunbong.allmart_api.customer.controller;


import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.sunbong.allmart_api.customer.domain.Customer;
import org.sunbong.allmart_api.customer.domain.CustomerLoginType;
import org.sunbong.allmart_api.customer.dto.*;
import org.sunbong.allmart_api.customer.exception.CustomerExceptions;
import org.sunbong.allmart_api.customer.service.CustomerService;
import org.sunbong.allmart_api.security.util.JWTUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/v1/customer")
public class CustomerController {

    private final CustomerService customerService;

    private final JWTUtil jWTUtil;

    @Value("${org.allmart_api.accessTime}")
    private int accessTime;

    @Value("${org.allmart_api.refreshTime}")
    private int refreshTime;

    @Value("${org.allmart_api.alwaysNew}")
    private boolean alwaysNew;

    @PostMapping("/makeToken")
    public ResponseEntity<CustomerTokenResponseDTO> makeToken(@RequestBody @Validated CustomerTokenRequestDTO tokenRequestDTO) {

        log.info("Making token");
        log.info("------------------------");

        CustomerResponseDTO customerDTO = customerService.authenticate(
                tokenRequestDTO.getEmail(), tokenRequestDTO.getLoginType());

        log.info(customerDTO);

        Map<String, Object> claimMap;

        if (tokenRequestDTO.getLoginType() == CustomerLoginType.SOCIAL) {
            claimMap = Map.of(
                    "email", customerDTO.getEmail());

        } else {
            claimMap = Map.of(
                    "phoneNumber", customerDTO.getPhoneNumber());
        }

        String accesToken = jWTUtil.createToken(claimMap, accessTime);
        String refreshToken = jWTUtil.createToken(claimMap, refreshTime);

        CustomerTokenResponseDTO tokenResponseDTO = CustomerTokenResponseDTO.builder()
                .accessToken(accesToken)
                .refreshToken(refreshToken)
                .email(customerDTO.getEmail())
                .phoneNumber(customerDTO.getPhoneNumber())
                .build();

        return ResponseEntity.ok(tokenResponseDTO);
    }

    @PostMapping(value = "refreshToken",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CustomerTokenResponseDTO> refreshToken(
            @RequestHeader("Authorization") String accessToken,
            String refreshToken,
            CustomerLoginType loginType // LoginType 추가
    ) {
        // Access Token 또는 Refresh Token이 없는 경우 예외 처리
        if (accessToken == null || refreshToken == null) {
            throw CustomerExceptions.TOKEN_NOT_ENOUGH.get();
        }
        if (!accessToken.startsWith("Bearer ")) {
            throw CustomerExceptions.ACCESSTOKEN_TOO_SHORT.get();
        }
        String accessTokenStr = accessToken.substring("Bearer ".length());

        try {
            // Access Token 검증 (만료되지 않은 경우)
            Map<String, Object> payload = jWTUtil.validateToken(accessTokenStr);

            String phoneNumber = loginType == CustomerLoginType.PHONE
                    ? payload.get("phoneNumber") != null ? payload.get("phoneNumber").toString() : null
                    : null;

            String email = loginType == CustomerLoginType.SOCIAL
                    ? payload.get("email") != null ? payload.get("email").toString() : null
                    : null;

            CustomerTokenResponseDTO tokenResponseDTO = CustomerTokenResponseDTO.builder()
                    .accessToken(accessTokenStr)
                    .refreshToken(refreshToken)
                    .phoneNumber(phoneNumber)
                    .email(email)
                    .build();
            return ResponseEntity.ok(tokenResponseDTO);

        } catch (ExpiredJwtException ex) {
            // Access Token이 만료된 경우
            try {
                // Refresh Token 검증
                Map<String, Object> payload = jWTUtil.validateToken(refreshToken);

                String phoneNumber = loginType == CustomerLoginType.PHONE
                        ? payload.get("phoneNumber").toString()
                        : null;

                String email = loginType == CustomerLoginType.SOCIAL
                        ? payload.get("email").toString()
                        : null;

                // 새로운 Access Token 및 Refresh Token 생성
                Map<String, Object> claimMap = new HashMap<>();
                if (email != null) claimMap.put("email", email);
                if (phoneNumber != null) claimMap.put("phoneNumber", phoneNumber);

                String newAccessToken = jWTUtil.createToken(claimMap, accessTime);
                String newRefreshToken = jWTUtil.createToken(claimMap, refreshTime);

                CustomerTokenResponseDTO tokenResponseDTO = CustomerTokenResponseDTO.builder()
                        .accessToken(newAccessToken)
                        .refreshToken(newRefreshToken)
                        .phoneNumber(phoneNumber != null ? phoneNumber : "N/A")
                        .email(email != null ? email : "N/A")
                        .build();

                return ResponseEntity.ok(tokenResponseDTO);

            } catch (ExpiredJwtException ex2) {
                // Refresh Token도 만료된 경우
                throw CustomerExceptions.REQUIRE_SIGN_IN.get();
            }
        }
    }


    @GetMapping("/signIn/phoneNumber/{phoneNumber}")
    public ResponseEntity<String> phoneNumberSignIn(@PathVariable(name = "phoneNumber", required = false) String phoneNumber) {

        return ResponseEntity.ok("");
    }

    @GetMapping("/signUp/phoneNumber")
    public ResponseEntity<String> phoneNumberSignUp() {
        return ResponseEntity.ok("전화번호설정 페이지?");
    }


    @PostMapping("/signUp/phoneNumber/{phoneNumber}")
    public ResponseEntity<Void> phoneNumberSignUp(@PathVariable("phoneNumber") String phoneNumber) {
        //
        CustomerRequestDTO customerRequestDTO = CustomerRequestDTO.builder()
                .phoneNumber(phoneNumber)
                .loginType(CustomerLoginType.PHONE)
                .build();

        Optional<Customer> customer = customerService.addMemberWithPhoneNumber(customerRequestDTO);
        Long customerId = customer.get().getCustomerID();

        log.info("전화번호 가등록 회원 생성");
        //return ResponseEntity.ok("전화번호 간편 로그인 성공");
        return ResponseEntity.status(303).header("Location", "/api/v1/qrcode/signUp?phoneNumber=" + phoneNumber + "&customerID=" + customerId)
                .build();
    }

    @PostMapping("/signIn")
    public ResponseEntity<CustomerTokenResponseDTO> signIn(@RequestBody CustomerSignInRequestDTO signInRequest,
            @RequestParam("loginType") CustomerLoginType loginType) {
        // 서비스 계층 호출
        CustomerTokenResponseDTO tokenResponse = customerService.signIn(signInRequest, loginType);
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/verifyToken")
    public ResponseEntity<CustomerResponseDTO> verifyToken(@RequestHeader("Authorization") String token) {
        // Bearer 토큰에서 "Bearer " 제거
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 토큰 검증 및 고객 정보 반환
        CustomerResponseDTO customerDTO = customerService.verify(token);
        return ResponseEntity.ok(customerDTO);
    }





}
