package kr.co.nice.nicein.manage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto {
    private String targetCompanyName;
    private String username;
    private String userEmail;
    private String duty;
    private String note;
}
