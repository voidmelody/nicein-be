package kr.co.nice.nicein.groupware.entity;

import jakarta.persistence.*;
import kr.co.nice.nicein.common.entity.TimeBase;
import kr.co.nice.nicein.people.entity.ItEmployee;
import kr.co.nice.nicein.people.entity.ItPosition;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Employee extends TimeBase {
    @Id
    @Column(name="user_id")
    private String userId;
//    @Enumerated(value= EnumType.STRING)
    private String addJobType; // 이관하면서 enum -> string으로 했음.

    private String loginId;
    private String username;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private ItEmployee itEmployee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="dept_id", referencedColumnName = "deptId")
    private Department department;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private List<ItPosition> itPositions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="company_id", referencedColumnName = "companyId")
    private Company company;

    private String status;
    private String sabun;
    private String cefBusinessCategory;
    private String cellPhoneNo;
    private String comPhoneNo;
    private String userEmail;
    private String ofcLevelName;
    private String positionName;
    private String functionName;
    private String employeeDeptFullName;
    private String itYn; // it인력여부
    private String managerYn; // 담당자 여부
    private String manRegYn; // 수동등록여부

    // hris와 매핑 사용
    private String hrisId;

    @Override
    public void prePersist() {
        super.prePersist();
    }

    @Override
    public void preUpdate() {
        super.preUpdate();
    }

}
