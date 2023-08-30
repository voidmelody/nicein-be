package kr.co.nice.nicein.people.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class OrganChartResponseDto {
    private String companyId;
    private String companyName;
    private String deptId;
    private String deptName;
}
