package kr.co.nice.nicein.people.entity;

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
@Table(name = "vmaster_employee")
public class MasterEmployee {

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
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private String itYn;
    private String managerYn;
    private LocalDate careerStart;
    private LocalDate niceStart;
    private LocalDate cmpStart;
    private LocalDate cmpEnd;
    private String itType;
    private String detail;
    private String license;
    private String note;
    private String companyName;
    private String itPosition;
    private String itTech;
    private String hrisId;
}
