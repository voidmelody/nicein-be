package kr.co.nice.nicein.people.dto;

import kr.co.nice.nicein.people.entity.ItEmployee;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PeopleResponseDto {
    private String userId;
    private String companyName;
    private String deptName;
    private String positionName;
    private String username;
    private String status;  // String 수정 완료.
    private String userEmail;
    private String comPhoneNo;
    private String cellPhoneNo;
    private String cefBusinessCategory;
    private Boolean itYn;
    private String manRegYn;
    private Boolean hrisYn;

    private ItEmployee itEmployee;
}
