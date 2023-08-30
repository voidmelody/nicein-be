package kr.co.nice.nicein.people.entity;

import jakarta.persistence.*;
import kr.co.nice.nicein.common.entity.TimeBase;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="it_employee")
public class ItEmployee extends TimeBase {

    @Id
    private String userId;

    private LocalDate careerStart;
    private LocalDate niceStart;
    private LocalDate cmpStart;
    private LocalDate cmpEnd;

    private String itType;
    private String detail;
    private String license;
    private String note;

    private String cmpEndReason;

    @Override
    public void prePersist() {
        super.prePersist();
    }

    @Override
    public void preUpdate() {
        super.preUpdate();
    }
}
