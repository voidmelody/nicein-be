package kr.co.nice.nicein.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // 인증 문제 발생 시 해당 부분 호출
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html; charset=UTF-8");

        String exception = (String) request.getAttribute("exception");
        // 필터에 걸린 오류
        if(exception != null){
            response.getWriter().write(exception);
        } else{
            // 일반 인증 예외 처리
            response.getWriter().write("인증되지 않은 사용자입니다.");
        }
    }
}
