package kr.co.nice.nicein.people.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class ItPeopleStatResponseDto {
    private String companyId;
    private String companyName;

    private Long code1;
    private Long code10;
    private Long code21;
    private Long code22;
    private Long code23;
    private Long code25;
    private Long code26;
    private Long subTotal;
    private Long unClass;
    private Long all;
}
