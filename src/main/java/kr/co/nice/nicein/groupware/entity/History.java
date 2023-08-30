package kr.co.nice.nicein.groupware.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="hr_history")
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="user_id")
    private Employee employee;

    private String editTable;
    private String editField;
    private String preData;
    private String chgData;
    private LocalDateTime updatedAt;
    private String editorId;
    private String fieldName;
    private String type;
    private String content;
}




