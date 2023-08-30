package kr.co.nice.nicein.rpa.dto;

import lombok.*;

@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GroupMailUserResponseDto {
    private Long mailUserId;
    private String taskId;
    private String taskName;
    private String category; // 구분
    private String companyName;
    private String departmentName;
    private String name;
    private String email;
    private String account;
    private String groupMemberYn;

    public GroupMailUserResponseDto(String companyName, String departmentName, String name, String email) {
        this.companyName = companyName;
        this.departmentName = departmentName;
        this.name = name;
        this.email = email;
    }
}
