package kr.co.nice.nicein.excel.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.time.LocalDate;

@Entity
@Getter
@Immutable
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "vexcel_rpa")
public class ExcelRpa {

    @Id
    private Integer id;

    private String taskId;
    private String taskCompanyName;
    private String deptName;
    private String positionName;
    private String account;
    private String taskName;
    private String taskSaveTime;
    private String manager;
    private String schedule;
    private String botName;
    private String useYn;
    private LocalDate created_at;
    private String scheduleCycle;
    private String category;
    private String mailUserCompanyName;
    private String companyId;
    private String employeeDeptFullName;
    private String username;
    private String userEmail;

}
