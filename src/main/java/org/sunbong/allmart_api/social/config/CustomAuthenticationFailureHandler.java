package org.sunbong.allmart_api.social.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {
        // React 로그인 페이지로 리디렉션
        // 실제 서비스하는 도메인 주소를 넣어야함.
        //response.sendRedirect("");
        String martID = request.getParameter("martID");
        response.sendRedirect("http://localhost:5173/" + martID +"/customer/signin");
        }
}
