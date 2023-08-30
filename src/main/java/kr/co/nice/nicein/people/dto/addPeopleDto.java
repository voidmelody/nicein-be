package kr.co.nice.nicein.people.dto;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class addPeopleDto {
    String companyId;
    String deptId;
    String positionName;
    String username;
    String userEmail;
    String comPhoneNo;
    String cellPhoneNo;
    String cefBusinessCategory;
    Boolean itYn;

    String type;
    String careerStart;
    String niceStart;
    String cmpStart;
    String cmpEnd;
    String cmpEndReason;
    String detail;
    String license;
    String note;

    List<String> positionList;
    List<String> techList;
}
