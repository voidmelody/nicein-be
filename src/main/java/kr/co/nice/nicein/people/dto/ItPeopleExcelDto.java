package kr.co.nice.nicein.people.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class ItPeopleExcelDto {
    private String companyName;
    private String deptFullName;
    private String deptName;
    private String username;
    private String positionName;
    private Date careerStart;
    private Date niceStart;
    private Date cmpStart;
    private String type;

    private String positions;

    private String detail;
    private String note;
    private String userEmail;
    private String userId;
}
