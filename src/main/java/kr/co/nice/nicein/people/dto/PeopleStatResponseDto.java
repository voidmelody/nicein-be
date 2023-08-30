package kr.co.nice.nicein.people.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class PeopleStatResponseDto {
    private String companyName;
    private String deptId;
    private Long countEmployeeInGroupware;
    private Long countEmployeeInManual;
    private Long countItEmployeeInGroupware;
    private Long countItEmployeeInManual;
    private Long sumItPeople;
}
