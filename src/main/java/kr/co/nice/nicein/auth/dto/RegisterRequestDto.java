package kr.co.nice.nicein.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDto {
    private String username;
    private String password;
    private String role;
    private String name;
    private String companyName;
    private String contact;
    private String deptName;
    private String functionName;
    private String description;
}
