package kr.co.nice.nicein.manage.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PeopleHistoryDto {
    private String userId;
    private String companyName;
    private String username;
    private String type;
    private String content;
    private LocalDateTime updated_at;
    private String editorId;
    private String editorAccount;
    private String editorUsername;
}
