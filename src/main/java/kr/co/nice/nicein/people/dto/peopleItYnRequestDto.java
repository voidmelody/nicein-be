package kr.co.nice.nicein.people.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class peopleItYnRequestDto {
    private String userId;
    private Boolean itYn;
}
