package kr.co.nice.nicein.rpa.dto;

import kr.co.nice.nicein.groupware.entity.Company;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpaTaskResponseDto {
    private String taskId;
    private CompanyDto company;
    private String taskName;
    private Integer taskSaveTime;
    private Integer rpatype01;
    private Integer rpatype02;
    private Integer rpatype03;
    private Integer rpatype04;
    private String manager;
    private String schedule;
    private String botName;
    private String useYn;
}
