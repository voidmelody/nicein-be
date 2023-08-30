package kr.co.nice.nicein.rpa.entity;


import jakarta.persistence.*;
import kr.co.nice.nicein.groupware.entity.Company;
import kr.co.nice.nicein.common.entity.TimeBase;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpaTask extends TimeBase {

    @Id
    @Column(name="task_id")
    private String taskId;

    @OneToOne
    @JoinColumn(name="company_id")
    private Company company; // Company 테이블 fk

    private String taskName;
    private Integer taskSaveTime;
    private String manager;
    private String schedule;
    private String botName;
    private String useYn;

    @Override
    public void prePersist() {
        super.prePersist();
    }

    @Override
    public void preUpdate() {
        super.preUpdate();
    }
}
