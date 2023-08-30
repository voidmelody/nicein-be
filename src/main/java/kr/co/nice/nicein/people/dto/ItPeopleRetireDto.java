package kr.co.nice.nicein.people.dto;

import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItPeopleRetireDto {
    private String userId;
    private String companyName;
    private String deptName;
    private String username;
    private String type;
    private LocalDate cmpEnd;
    private String cmpEndReason;
}
