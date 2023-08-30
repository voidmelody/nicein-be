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
@Table(name = "account_history")
public class AccountHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountHistoryId;

    private String category;
    private String adminUserId;
    private String targetUserId;
    private LocalDateTime timestamp;
    private String content;
}
