package kr.co.nice.nicein.my.dto;

import kr.co.nice.nicein.auth.dto.TokenDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyResponseDto {
    private String companyName;
    private String name; // 이름
    private String ngMailUseYn; // 메일사용여부
    private String username; // 아이디 or 이메일

}
