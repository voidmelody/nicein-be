package kr.co.nice.nicein.excel.repository;

import kr.co.nice.nicein.excel.entity.Excel;
import kr.co.nice.nicein.groupware.entity.Employee;
import kr.co.nice.nicein.rpa.entity.GroupMailUser;

import java.util.List;

public interface ExcelRepositoryCustom {

    List<Excel> findByCategoryOrderByIndex(String category);
}
