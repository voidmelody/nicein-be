package kr.co.nice.nicein.manage.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "vaccount_history")
public class MasterAccountHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountHistoryId;

    private String category;
    private String adminCompanyName;
    private String adminAccount;
    private String adminUsername;
    private String targetAccount;
    private String authChangeContent;
    private LocalDateTime timestamp;
    private String userCompanyName;
    private String userDeptName;
    private String username;
    private String userAccount;
    private String authOption;
    private String excelContent;

}
