package kr.co.nice.nicein.manage.dto;

import lombok.*;

@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountInfoDto {
    private String targetCompanyId;
    private String username;
    private String account;
    private String optionCode;
    private String role;
    private String rwCode;
    private String password;
    private String note;
    private String otpYn;
}
