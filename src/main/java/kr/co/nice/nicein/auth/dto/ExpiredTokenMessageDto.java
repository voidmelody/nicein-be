package kr.co.nice.nicein.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ExpiredTokenMessageDto {
    private Integer status;
    private String message;
    private String code;
}
