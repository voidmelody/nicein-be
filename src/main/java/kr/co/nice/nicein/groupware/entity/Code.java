package kr.co.nice.nicein.groupware.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Code {

    @Id
    private Long id;

    private String category;
    private String code;
    private String value;
    private Long order;
    private String useYn;
    private String desc;
}
