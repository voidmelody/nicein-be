package kr.co.nice.nicein.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import kr.co.nice.nicein.auth.dto.TokenDto;
import kr.co.nice.nicein.security.CustomUserDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenProvider {
    // 경로 변경
    @Value("${key.salt}")
    private  String salt;
    private Key secretKey;
    public static final long ACCESS_TOKEN_EXPIRED = 1000L * 60 * 60; // 1초 * 60 * 60 = 1시간
    public static final long REFRESH_TOKEN_EXPIRED = 12000L * 60 * 60; // 12시간


    private final CustomUserDetailService userDetailsService;

    @PostConstruct
    protected void init(){
        secretKey = Keys.hmacShaKeyFor(salt.getBytes(StandardCharsets.UTF_8));
    }

    // 토큰 생성
    public TokenDto createToken(String username, String role){
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("role", role);
        Date now = new Date();
        String accessToken = Jwts.builder().setClaims(claims).setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_EXPIRED))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
        String refreshToken = Jwts.builder().setClaims(claims).setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_EXPIRED))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
        return new TokenDto(accessToken, refreshToken, now.getTime() + ACCESS_TOKEN_EXPIRED);
    }

    // 권한 정보
    // Spring Security 인증 과정에서 권한 확인을 위한 기능
    public Authentication getAuthentication(String token){
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserName(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    //토큰에 담겨 있는 유저 id 획득
    // 만료된 토큰에 대해서는 ExpiredJwtException이 발생하므로 처리.
    public String getUserName(String token){
        try{
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody().getSubject();
        }catch (ExpiredJwtException e) {
            log.error("만료되었습니다.");
            return e.getClaims().getSubject();
        }
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody().getSubject();
    }

    //Authorization Header를 통해 인증
    public String resolveToken(HttpServletRequest request){
        String authorization = request.getHeader("Authorization");
        if(authorization == null || !authorization.substring(0, "BEARER ".length()).equalsIgnoreCase("BEARER ")){
            return null;
        }else{
            String token = authorization.split(" ")[1].trim();
            return token;
        }
    }

    // 토큰 검증
    public boolean validateToken(String token){
        try{
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            // 만료되었을 시 false
            return !claims.getBody().getExpiration().before(new Date());
        }catch(Exception e){
            return false;
        }
    }
}
