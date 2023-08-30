package kr.co.nice.nicein.excel.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.Immutable;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Immutable
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "vexcel_employee")
public class ExcelEmployee {

    @Id
    private String userId;

    private String addJobType;
    private String cefBusinessCategory;
    private String cellPhoneNo;
    private String comPhoneNo;
    private String employeeDeptFullName;
    private String functionName;
    private String loginId;
    private String ofcLevelName;
    private String positionName;
    private String sabun;
    private String status;
    private String userEmail;
    private String username;
    private String companyId;
    private String deptName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String itYn;
    private String managerYn;
    private String manRegYn;
    private Date careerStart;
    private Date niceStart;
    private Date cmpStart;
    private Date cmpEnd;

    private String itType;
    private String detail;
    private String license;
    private String note;

    private String companyName;
    private String itPosition;
    private String itTech;
}
