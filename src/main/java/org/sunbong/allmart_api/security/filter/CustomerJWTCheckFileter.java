package org.sunbong.allmart_api.security.filter;

import com.google.gson.Gson;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.sunbong.allmart_api.security.auth.CustomerPrincipal;
import org.sunbong.allmart_api.security.util.JWTUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
public class CustomerJWTCheckFileter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        log.info("shouldNotFilter");

        String uri = request.getRequestURI();
        log.info("----------------------------------");
        if(uri.equals("/api/v1/member/makeToken") ||
                uri.equals("/api/v1/member/refreshToken") ||
                uri.equals("/api/v1/member/signUp")||
                uri.startsWith("/api/v1/qrcode")||
                // "/"동적으로 오는 @PathVariable 값 처리 부분
                uri.startsWith("/api/v1/customer")){

            // 회원가입 엔드포인트 추가

            return true;
        }


        log.info("--------------request.false---------");

        return false;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("doFilterInternal");

        // 요청 URI 확인
        log.info("Request URI: {}", request.getRequestURI());

        // Authorization 헤더 확인
        String authHeader = request.getHeader("Authorization");
        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // "Bearer " 이후의 JWT 추출
        } else {
            makeError(response, Map.of("status", 401, "msg", "No Access Token"));
            return;
        }

        // JWT validate
        try {
            Map<String, Object> claims = jwtUtil.validateToken(token); // JWT 검증 및 Claims 추출
            log.info("Claims: {}", claims);

            // Claims에서 필요한 정보 추출
            String email = (String) claims.get("email");
            String role = (String) claims.get("role");
            String phone = (String) claims.get("phone");
            String loginType = (String) claims.get("loginType");

            // Custom Principal 생성
            Principal userPrincipal = new CustomerPrincipal(email, role, phone, loginType);

            // SecurityContext 설정
            UsernamePasswordAuthenticationToken authenticationToken =
                    // new UsernamePasswordAuthenticationToken(userPrincipal, null, List.of(new SimpleGrantedAuthority("ROLE_" + role)));
                    new UsernamePasswordAuthenticationToken(userPrincipal, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(authenticationToken);

            // 다음 필터로 요청 전달
            filterChain.doFilter(request, response);

        } catch (JwtException e) {
            log.error("JWT Exception: {}", e.getMessage());
            makeError(response, Map.of("status", 401, "msg", e.getClass().getSimpleName()));
        }
    }
    private void makeError(HttpServletResponse response, Map<String, Object> map) {

        Gson gson = new Gson();
        String jsonStr = gson.toJson(map);

        response.setStatus((int)map.get("status"));
        response.setContentType("application/json");

        try {
            PrintWriter out  = response.getWriter();
            out.println(jsonStr);
            out.close();
        } catch (IOException e) {


            throw new RuntimeException(e);
        }


    }


}
