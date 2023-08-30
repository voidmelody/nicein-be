package kr.co.nice.nicein.common.vo;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TableStatus {
    YES("Y"),
    NO("N");

    private final String value;
}
