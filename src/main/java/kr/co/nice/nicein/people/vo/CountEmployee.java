package kr.co.nice.nicein.people.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@AllArgsConstructor
public class CountEmployee {
    private Long countEmployeeInGroupware;
    private Long countEmployeeInManual;
    private Long countItEmployeeInGroupware;
    private Long countItEmployeeInManual;
    private Long sumItPeople;
}
