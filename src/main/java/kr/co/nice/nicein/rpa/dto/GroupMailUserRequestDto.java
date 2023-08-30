package kr.co.nice.nicein.rpa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupMailUserRequestDto {
    private Long mailUserId;
    private String taskId;
    private String category; // 구분
    private String companyName;
    private String departmentName;
    private String name;
    private String email;
    private String account;
}
