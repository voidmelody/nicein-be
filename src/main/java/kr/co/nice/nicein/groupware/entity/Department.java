package kr.co.nice.nicein.groupware.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import kr.co.nice.nicein.common.entity.TimeBase;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Department extends TimeBase {
    @Id
    private String deptId;

    @ManyToOne
    @JoinColumn(name="company_id")
    private Company company; // 부서 & 회사 연관관계 매핑

    private String deptName;
    private String deptShortName;
    private String treeId;
    private String upTreeId;
    private Integer depth;
    private Integer sortNo;
    private String useYn;
    private String mgrUserId;
    private String deptFullName;

    @Override
    public void prePersist() {
        super.prePersist();
    }

    @Override
    public void preUpdate() {
        super.preUpdate();
    }
}

