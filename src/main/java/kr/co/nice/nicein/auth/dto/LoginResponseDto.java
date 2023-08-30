package kr.co.nice.nicein.auth.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoginResponseDto {
    private Long id;
    private String username;
    private String name;
    private String description;

    private String role;
    private String userId;
    private TokenDto token;
    private String useOtp;

    private String requirePwChange;
}
