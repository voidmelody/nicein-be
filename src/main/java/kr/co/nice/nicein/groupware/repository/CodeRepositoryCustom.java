package kr.co.nice.nicein.groupware.repository;

import kr.co.nice.nicein.groupware.entity.Code;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CodeRepositoryCustom {
    List<Code> getPositionList();
    List<Code> getTechList();
    List<List<Code>> getTechListOrderByCode();
    List<String> getTechDescList();
    String convertItPositionCodeToValue(String code);
    String convertAuthOptionCodeToValue(String code);
    String convertAuthRwCodeToValue(String code);
    String convertTypeValueToCode(String value);
    List<String> getCategoryList();
    Page<Code> getCode(String category, String searchText, Pageable pageable);
}
