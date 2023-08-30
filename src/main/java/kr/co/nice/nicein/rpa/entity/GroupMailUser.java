package kr.co.nice.nicein.rpa.entity;


import jakarta.persistence.*;
import kr.co.nice.nicein.groupware.entity.Employee;
import kr.co.nice.nicein.common.entity.TimeBase;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="rpa_group_mail_user")
public class GroupMailUser extends TimeBase {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @ManyToOne
    @JoinColumn(name="task_id")
    private RpaTask rpaTask;

    private String category;

    @OneToOne
    @JoinColumn(name="user_id")
    private Employee employee; // 조직도 테이블 fk

    private String account;
    private String groupMemberYn;
    private String manualCompanyName;
    private String manualUserName;
    private String manualUserEmail;
    private String activeYn;

    @Override
    public void prePersist() {
        super.prePersist();
    }

    @Override
    public void preUpdate() {
        super.preUpdate();
    }
}
