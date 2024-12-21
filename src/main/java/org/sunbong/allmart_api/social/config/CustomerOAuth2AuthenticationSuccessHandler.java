package org.sunbong.allmart_api.social.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.sunbong.allmart_api.customer.service.CustomerService;
import org.sunbong.allmart_api.security.util.JWTUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@Component
@RequiredArgsConstructor
public class CustomerOAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

     private final JWTUtil jwtUtil;
     private final CustomerService customerService;

//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
//        Map<String, Object> attributes = oAuth2User.getAttributes();
//
//        log.info("OAuth2User attributes: {}", attributes);
//
//        // 쿼리 파라미터 데이터
//        String email = attributes != null ? (String) attributes.get("email") : null;
//        Long customerID = attributes != null ? (Long) attributes.get("customerID") : null;
//        String frag = "kakao";
//
//        // martID 동적 추출
//        String martID = request.getParameter("martID"); // 또는 다른 방식으로 추출
//        log.info("MartID: {}", martID);
//
//        if (customerService.findByEmail(email).getPhoneNumber() == null ||
//                customerService.findByEmail(email).getPhoneNumber().isEmpty()){
//            String  redirectUrl = UriComponentsBuilder.fromUriString("http://localhost:5173/" + martID + "/customer/update")
//                    .queryParam("email", email)
//                    .queryParam("customerID", customerID)
//                    .queryParam("frag", frag)
//                    .build().toUriString();
//            response.sendRedirect(redirectUrl);
//        }else{
//            String  redirectUrl = UriComponentsBuilder.fromUriString("http://localhost:5173/" + martID )
//                    .queryParam("email", email)
//                    .queryParam("customerID", customerID)
//                    .build().toUriString();
//            response.sendRedirect(redirectUrl);
//        }
//
//
//
//    }

//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
//        Map<String, Object> attributes = oAuth2User.getAttributes();
//
//        log.info("OAuth2User attributes: {}", attributes);
//
//        // 필요한 데이터 추출
//        String email = attributes != null ? (String) attributes.get("email") : null;
//        String customerID = attributes != null ? (String) attributes.get("customerID") : null;
//        String accessToken = attributes != null ? (String) attributes.get("accessToken") : null;
//        String refreshToken = attributes != null ? (String) attributes.get("refreshToken") : null;
//
//
//        String martID = request.getParameter("martID"); // 매장 ID 추출
//
//        // JSON 응답 데이터 생성
//        Map<String, Object> jsonResponse = new HashMap<>();
//        jsonResponse.put("email", email);
//        jsonResponse.put("customerID", customerID);
//        jsonResponse.put("martID", martID);
//        jsonResponse.put("accessToken", accessToken);
//        jsonResponse.put("refreshToken", refreshToken);
//
//
//        // HTTP 응답에 JSON 작성
//        response.setContentType("application/json");
//        response.setCharacterEncoding("UTF-8");
//        response.setStatus(HttpServletResponse.SC_OK);
//        response.getWriter().write(new ObjectMapper().writeValueAsString(jsonResponse));
//    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        log.info("OAuth2User attributes: {}", attributes);

        // 필요한 데이터 추출
        String email = attributes != null ? (String) attributes.get("email") : null;
        if (email == null || email.isEmpty()) {
            log.error("Email is missing in OAuth2 attributes");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing email in authentication response");
            return;
        }

        Long customerID = null;
        try {
            customerID = attributes != null && attributes.get("customerID") instanceof Long
                    ? (Long) attributes.get("customerID")
                    : Long.parseLong((String) attributes.get("customerID"));
        } catch (Exception e) {
            log.error("Invalid customerID format: {}", attributes.get("customerID"), e);
        }

        if (customerID == null) {
            log.error("CustomerID is missing or invalid");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing or invalid customerID in authentication response");
            return;
        }

        String martID = request.getParameter("martID");
        if (martID == null || martID.isEmpty()) {
            log.warn("martID is missing in request parameters");
            martID = "defaultMartID"; // 기본값 설정
        }

        // JSON 응답 데이터 생성
        Map<String, Object> jsonResponse = new HashMap<>();
        jsonResponse.put("email", email);
        jsonResponse.put("customerID", customerID);
        jsonResponse.put("martID", martID);

        log.info("Authentication successful, sending response: {}", jsonResponse);

        // HTTP 응답에 JSON 작성
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(new ObjectMapper().writeValueAsString(jsonResponse));
    }



}
