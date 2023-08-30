package kr.co.nice.nicein.excel.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Excel {
    @Id
    private Long id;

    private String category;
    private String tab;
    private Integer index;
    private String field;
    private String tableName;
    private String columnName;
    private String externalYn;
    private Integer cellWidth;
}
