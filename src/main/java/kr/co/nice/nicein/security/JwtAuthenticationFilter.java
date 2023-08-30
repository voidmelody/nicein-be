package kr.co.nice.nicein.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.nice.nicein.auth.dto.LoginResponseDto;
import kr.co.nice.nicein.auth.entity.Member;
import kr.co.nice.nicein.auth.dto.LoginRequestDto;
import kr.co.nice.nicein.manage.entity.AccountHistory;
import kr.co.nice.nicein.manage.repository.AccountHistoryRepository;
import kr.co.nice.nicein.manage.service.ManageService;
import kr.co.nice.nicein.my.dto.MyResponseDto;
import kr.co.nice.nicein.auth.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final MemberRepository memberRepository;
    private final AccountHistoryRepository accountHistoryRepository;
    private final PasswordEncoder passwordEncoder;


    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, AuthenticationManager authenticationManager, MemberRepository memberRepository, AccountHistoryRepository accountHistoryRepository, PasswordEncoder passwordEncoder) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
        this.memberRepository = memberRepository;
        this.accountHistoryRepository = accountHistoryRepository;
        this.passwordEncoder = passwordEncoder;
        setFilterProcessesUrl("/auth/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            LoginRequestDto member = objectMapper.readValue(request.getInputStream(), LoginRequestDto.class);
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(member.getUsername(), member.getPassword());

            return authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        Member member = (Member) authResult.getPrincipal();
        LoginResponseDto loginResponseDto = LoginResponseDto.builder()
                .id(member.getMemberId())
                .username(member.getUsername())
                .name(member.getName())
                .description(member.getDescription())
                .role(member.getRole().toString())
                .userId(member.getEmployee().getUserId())
                .token(jwtTokenProvider.createToken(member.getUsername(), member.getRole().toString()))
                .useOtp(member.getOtpYn())
                .build();

        // 비밀번호가 'nice1234!!'일 시 비밀번호 변경화면으로 리다이렉트
        if(passwordEncoder.matches("nice1234!!", member.getPassword())){
            loginResponseDto.setRequirePwChange("Y");
        }

        //response.addHeader(HttpHeaders.AUTHORIZATION, loginResponseDto.getToken().getAccessToken()); // 테스트용도.
        response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + loginResponseDto.getToken().getAccessToken());
        response.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.getOutputStream().write(new ObjectMapper().writeValueAsBytes(loginResponseDto));

        // 저장
        Optional<Member> findMember = memberRepository.findByUsername(member.getUsername());
        findMember.get().setRefreshToken(loginResponseDto.getToken().getRefreshToken());
        memberRepository.save(findMember.get());

        // 로그인 내역 저장
        AccountHistory accountHistory = AccountHistory.builder()
                .category("login_history")
                .adminUserId(member.getEmployee() == null ? null : member.getEmployee().getUserId())
                .timestamp(LocalDateTime.now())
                .build();
        accountHistoryRepository.save(accountHistory);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        super.unsuccessfulAuthentication(request, response, failed);
    }
}
