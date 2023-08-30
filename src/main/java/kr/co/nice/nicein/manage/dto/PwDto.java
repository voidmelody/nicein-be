package kr.co.nice.nicein.manage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PwDto {
    private String oldPw;
    private String newPw;
    private String confirmPw;
}
