package kr.co.nice.nicein.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Authority {
    ROLE_USER("USER"),
    ROLE_ADMIN("ADMIN"),
//    ROLE_GROUPIT("GROUPIT"),
    ROLE_RPA("RPA"),
    ROLE_PM("PM");


    private String value;
}
