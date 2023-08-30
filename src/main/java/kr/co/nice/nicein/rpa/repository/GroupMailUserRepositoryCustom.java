package kr.co.nice.nicein.rpa.repository;

import kr.co.nice.nicein.rpa.dto.GroupMailUserResponseDto;
import kr.co.nice.nicein.rpa.entity.GroupMailUser;
import kr.co.nice.nicein.rpa.entity.RpaTask;

import java.util.List;

public interface GroupMailUserRepositoryCustom {

    List<GroupMailUserResponseDto> findBySearchOption(String companyId, String text);
    List<GroupMailUser> findByRpaTask(RpaTask rpaTask);
//    Long findId(GroupMailUser groupMailUser);


}
