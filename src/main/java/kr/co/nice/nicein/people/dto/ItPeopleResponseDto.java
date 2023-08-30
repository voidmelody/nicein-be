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
public class ItPeopleResponseDto {
    private String userId;
    private String companyName;
    private String deptName;
    private String positionName;
    private String username;
    private String userEmail;
    private String comPhoneNo;
    private String cellPhoneNo;
    private String cefBusinessCategory;
    private String itYn;
    private String status;

    // IT인력정보
    private String type;
    private String position; // 직무는 it_position 테이블 활용
    private Date careerStart;
    private Date niceStart;
    private Date cmpStart;
    private String detail;
    private String license;
    private String note;

    public ItPeopleResponseDto(String userId, String companyName, String deptName, String positionName, String username, String status, String type, Date careerStart, Date cmpStart) {
        this.userId = userId;
        this.companyName = companyName;
        this.deptName = deptName;
        this.positionName = positionName;
        this.username = username;
        this.status = status;
        this.type = type;
        this.careerStart = careerStart;
        this.cmpStart = cmpStart;
    }
}
