package kr.co.nice.nicein.people.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItPeopleSaveRequestDto {
    private String userId;
    private String detail;
    private String license;
    private String itType;
    private LocalDate careerStart;
    private String cmpEndReason;

    private List<String> positionList;
    private List<String> techList;
}
