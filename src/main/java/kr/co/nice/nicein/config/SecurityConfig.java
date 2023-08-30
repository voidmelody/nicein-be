package kr.co.nice.nicein.config;

import kr.co.nice.nicein.manage.repository.AccountHistoryRepository;
import kr.co.nice.nicein.security.CustomAccessDeniedHandler;
import kr.co.nice.nicein.security.CustomAuthenticationEntryPoint;
import kr.co.nice.nicein.security.JwtAuthenticationFilter;
import kr.co.nice.nicein.security.JwtAuthorizationFilter;
import kr.co.nice.nicein.auth.repository.MemberRepository;
import kr.co.nice.nicein.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig{
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final AccountHistoryRepository accountHistoryRepository;

    @Bean
    public AuthenticationManager authenticationManager() throws Exception
    {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity
                // 문자열을 Base64로 인코딩 전달
                .httpBasic().disable()
                // 쿠키 기반이 아닌 JWT 기반이므로 사용 X
                .csrf().disable()
                .cors().configurationSource(corsConfigurationSource())
                .and()
                //Spring Security 세션 정책 : 세션을 생성 및 사용하지 않음
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // 조건 별로 요청 허용 / 제한 설정
                .authorizeHttpRequests()
                // 회원 가입과 로그인은 모두 승인
                .requestMatchers("/auth/**", "/hris/**").permitAll()
                .anyRequest().authenticated()
                .and()
                // login 주소가 호출되면 인증 및 토큰 발행 필터 추가, 로그인 성공 시 이력 추가
                .addFilter(new JwtAuthenticationFilter(jwtTokenProvider, authenticationManager(), memberRepository,accountHistoryRepository, passwordEncoder()))
                // JWT 토큰검사
                .addFilterBefore(new JwtAuthorizationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
//                // 에러 헨들링
                .exceptionHandling()
                .accessDeniedHandler(new CustomAccessDeniedHandler())
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint());
        return httpSecurity.build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

//        configuration.addAllowedOrigin("*");
        configuration.addAllowedOriginPattern("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);
        configuration.addExposedHeader("Authorization");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
