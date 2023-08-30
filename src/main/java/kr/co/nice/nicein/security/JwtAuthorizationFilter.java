package kr.co.nice.nicein.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.nice.nicein.auth.dto.ExpiredTokenMessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthorizationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 1. 헤더 요청 정보에서 토큰 가져오기
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) request);
        log.info("token =" + token);
        // 2. 유효기간 검증
        if(token != null) {
            if (jwtTokenProvider.validateToken(token)) {
                // 3. token 내부의 username을 통해 Authentication 생성
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                // 4. Authentication을 SecurityContextHolder에 세팅.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                log.info("토큰만료");
                ExpiredTokenMessageDto tokenMessageDto = ExpiredTokenMessageDto.builder()
                        .status(HttpServletResponse.SC_UNAUTHORIZED)
                        .code("Expired Token")
                        .message("토큰이 만료되었습니다.")
                        .build();
                request.setAttribute("exception", new ObjectMapper().writer().writeValueAsString(tokenMessageDto));
            }
        }
        chain.doFilter(request,response);
    }
}

