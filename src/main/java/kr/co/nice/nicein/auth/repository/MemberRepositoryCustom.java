package kr.co.nice.nicein.auth.repository;

import kr.co.nice.nicein.groupware.entity.Code;
import kr.co.nice.nicein.manage.dto.AccountDto;
import kr.co.nice.nicein.manage.dto.AccountInfoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MemberRepositoryCustom {

    Page<AccountDto> searchAccount(String searchText, Pageable pageable);
    AccountInfoDto getAccountInfo(String account);
    List<Code> getOptionList();
    List<Code> getRwList();
}
