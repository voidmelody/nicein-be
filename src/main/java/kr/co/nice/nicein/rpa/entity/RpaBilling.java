package kr.co.nice.nicein.rpa.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "vrpa_billing")
public class RpaBilling {

    @Id
    private Long billingId;

    private String companyId;
    private String companyName;
    private String taskName;
    private String taskId;
    private String billingTaskName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Byte taskAction;
    private Integer taskSaveTime;
}
