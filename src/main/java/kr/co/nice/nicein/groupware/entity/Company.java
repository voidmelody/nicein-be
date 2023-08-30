package kr.co.nice.nicein.groupware.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import kr.co.nice.nicein.common.entity.TimeBase;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Company extends TimeBase {
    @Id
    private String companyId;

    private String companyName;

    private Long orderNum;

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

