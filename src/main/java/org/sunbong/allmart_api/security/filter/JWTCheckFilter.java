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
import org.sunbong.allmart_api.security.auth.CustomuserPrincipal;
import org.sunbong.allmart_api.security.util.JWTUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
public class JWTCheckFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        log.info("shouldNotFilter");

        String uri = request.getRequestURI();

        // AntPathMatcher로 경로 매칭
        if (uri.startsWith("/uploads/")) {
            return true;
        }

        if (uri.startsWith("/api/v1/mart")) {
            return true;
        }

        log.info("----------------------------------");
        if(uri.equals("/api/v1/member/makeToken") ||
                uri.equals("/api/v1/member/refreshToken") ||
                uri.equals("/api/v1/member/signUp")) // 회원가입 엔드포인트 추가
        {
            return true;
        }


        return false;
    }

//    @Override //테스트용
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        log.info("doFilterInternal");
//        filterChain.doFilter(request, response);//다음단계로 넘겨주기
//    }

    @Override //테스트할때는 테스트용쓰기
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("doFilterInternal");

        log.info(request.getRequestURI());

        String authHeader = request.getHeader("Authorization");

        String token = null;
        if(authHeader != null && authHeader.startsWith("Bearer ")){
            token = authHeader.substring(7);
        }else {
            makeError(response, Map.of("status",401, "msg","No Access Token") );
            return;
        }

        //JWT validate
        try{

            Map<String, Object> claims = jwtUtil.validateToken(token);
            log.info(claims);

            String email = (String) claims.get("email");
            String role = (String) claims.get("role");

            Principal userPrincipal = new CustomuserPrincipal(email);

            UsernamePasswordAuthenticationToken authenticationToken
                    = new UsernamePasswordAuthenticationToken(userPrincipal, null,
                    List.of(new SimpleGrantedAuthority("ROLE_"+role)));
            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(authenticationToken);

            filterChain.doFilter(request, response);//다음단계로 넘겨주기


        }catch (JwtException e){

            log.info(e.getClass().getName());
            log.info(e.getMessage());
            log.info("-------------");

            String classFullName = e.getClass().getName();

            String shortClassName = classFullName.substring(classFullName.lastIndexOf(".") + 1);

            makeError(response, Map.of("status",401,"msg",shortClassName));

            e.printStackTrace();
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
