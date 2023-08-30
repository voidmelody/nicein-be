package kr.co.nice.nicein.people.dto;


import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HistoryDto {
    private String companyName;
    private String username;
    private String type;
    private String content;
    private LocalDateTime date;
}
