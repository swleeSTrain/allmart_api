package org.sunbong.allmart_api.social.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.sunbong.allmart_api.customer.domain.Customer;
import org.sunbong.allmart_api.customer.domain.CustomerLoginType;
import org.sunbong.allmart_api.customer.repository.CustomerRepository;
import org.sunbong.allmart_api.security.util.JWTUtil;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoOAuthService {

    @Value("${org.allmart_api.accessTime}")
    private int accessTime;

    @Value("${org.allmart_api.refreshTime}")
    private int refreshTime;

    @Value("${KAKAO_CLIENT_ID}")
    private String clientId;

    private final JWTUtil jwtUtil;

    private final CustomerRepository customerRepository;

    public Map<String, Object> authenticateWithKakao(String code, String state, String s_martID) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", "http://localhost:5173/oauth/kakao");
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        // 액세스 토큰 요청
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://kauth.kakao.com/oauth/token", request, Map.class);

        // 사용자 정보 요청
        String accessToken = (String) response.getBody().get("access_token");
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<Void> userInfoRequest = new HttpEntity<>(headers);
        ResponseEntity<Map> userInfoResponse = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me", HttpMethod.GET, userInfoRequest, Map.class);

        // 사용자 데이터 추출
        Map<String, Object> userInfo = userInfoResponse.getBody();
        log.info("============================");
        log.info("getBody : {}", userInfo);
        log.info("martID : {}", s_martID);

        // 이메일 및 닉네임 추출
        Map<String, Object> kakaoAccount = (Map<String, Object>) userInfo.getOrDefault("kakao_account", new HashMap<>());
        String email = kakaoAccount.getOrDefault("email", "no-email-provided").toString();

        Map<String, Object> properties = (Map<String, Object>) userInfo.getOrDefault("properties", new HashMap<>());
        String nickname = properties.getOrDefault("nickname", "no-nickname-provided").toString();

        Long customerID = userInfo.containsKey("id") ? ((Number) userInfo.get("id")).longValue() : null;
        Long martID = null;
        try {
            martID = Long.valueOf(s_martID);
        } catch (NumberFormatException e) {
            log.error("Invalid martID: {}", s_martID);
        }

        // JWT 토큰 생성
        Map<String, Object> claimMap = new HashMap<>();
        String UserAccessToken = jwtUtil.createToken(claimMap, accessTime);
        String UserRefreshToken = jwtUtil.createToken(claimMap, refreshTime);

        // DB 저장
        Map<String, Object> map = new HashMap<>();
        Customer customer = customerRepository.findByEmail(email);

        if (customer == null) {
            customer = customerRepository.save(Customer.builder()
                    .email(email)
                    .name(nickname)
                    .loginType(CustomerLoginType.SOCIAL)
                    .build());
            log.info("이메일 가회원이 되었습니다 : {}", email);
        }

        // phoneNumber가 null인 경우 처리
        String phoneNumber = customer.getPhoneNumber() != null ? customer.getPhoneNumber() : "N/A";
        map.put("phoneNumber", phoneNumber);
        map.put("customerID", customer.getCustomerID());

        // 반환 데이터 구성
        return Map.of(
                "accessToken", UserAccessToken,
                "refreshToken", UserRefreshToken,
                "email", email,
                "customerID", map.getOrDefault("customerID", 0),
                "martID", martID != null ? martID : 0,
                "phoneNumber", map.getOrDefault("phoneNumber", "N/A"),
                "name", nickname // 닉네임 추가
        );
    }
}
