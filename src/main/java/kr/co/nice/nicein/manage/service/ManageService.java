package kr.co.nice.nicein.manage.service;

import kr.co.nice.nicein.groupware.entity.Code;
import kr.co.nice.nicein.groupware.entity.History;
import kr.co.nice.nicein.groupware.repository.HistoryRepository;
import kr.co.nice.nicein.manage.dto.PeopleHistoryDto;
import kr.co.nice.nicein.manage.entity.AccountHistory;
import kr.co.nice.nicein.manage.entity.MasterAccountHistory;
import kr.co.nice.nicein.auth.entity.Member;
import kr.co.nice.nicein.manage.repository.AccountHistoryRepository;

import kr.co.nice.nicein.manage.repository.MasterAccountHistoryRepository;
import kr.co.nice.nicein.auth.repository.MemberRepository;
import kr.co.nice.nicein.groupware.repository.CodeRepository;
import kr.co.nice.nicein.groupware.repository.EmployeeRepository;
import kr.co.nice.nicein.manage.dto.AccountDto;
import kr.co.nice.nicein.manage.dto.AccountInfoDto;
import kr.co.nice.nicein.manage.dto.PwDto;
import kr.co.nice.nicein.security.Authority;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;


import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class ManageService {
    private final MemberRepository memberRepository;
    private final EmployeeRepository employeeRepository;
    private final AccountHistoryRepository accountHistoryRepository;
    private final MasterAccountHistoryRepository masterAccountHistoryRepository;
    private final CodeRepository codeRepository;
    private final HistoryRepository historyRepository;
    private final PasswordEncoder passwordEncoder;

    private final String defaultPassword = "admin00";

    public String changePw(Member member, PwDto pwDto) throws Exception {
        // 1. 현재 비밀번호가 일치한지 확인
        if(!passwordEncoder.matches(pwDto.getOldPw(), member.getPassword())){
            throw new AuthenticationException("비밀번호가 일치하지 않습니다.");
        }
        // 2. 현재 비밀번호가 새로운 비밀번호와 다른지(중복X) 확인
        if(pwDto.getOldPw().equals(pwDto.getNewPw())){
            throw new IllegalArgumentException("기존 비밀번호와 새로운 비밀번호가 동일합니다.");
        }

        // 3. 새로운 비밀번호와 확인 비밀번호가 일치한지 확인.
        if(!pwDto.getNewPw().equals(pwDto.getConfirmPw())){
            throw new IllegalArgumentException("변경할 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
        }

        // 4. 새로운 비밀번호를 Member에 업데이트.
        member.setPassword(passwordEncoder.encode(pwDto.getNewPw()));
        memberRepository.save(member);

        return "변경되었습니다.";
    }

    public Page<AccountDto> searchAccount(String searchText, Pageable pageable){
        Page<AccountDto> accountDtos = memberRepository.searchAccount(searchText, pageable);
        return accountDtos;
    }

    public AccountInfoDto getAccountInfo(String account){
        AccountInfoDto accountInfo = memberRepository.getAccountInfo(account);
        return accountInfo;
    }

    public String updateAccount(Member loginMember, AccountInfoDto account){
        String successCode;
        Optional<Member> existedMember = memberRepository.findByUsername(account.getAccount());
        // DB에 존재하지 않는 경우. 즉, 생성
        if(existedMember.isEmpty()){
            successCode = createAccount(loginMember,account);
        }else{
            // 수정하는 경우. 권한 이력 저장.
            Member member = existedMember.get();
            if(!member.getAuthOption().equals(account.getOptionCode())){
                String content = convertAuthOptionCodeToValue(member.getAuthOption()) + " → " + convertAuthOptionCodeToValue(account.getOptionCode());
                AccountHistory authOptionHistory = AccountHistory.builder()
                        .category("auth_change")
                        .adminUserId(loginMember.getEmployee().getUserId())
                        .targetUserId(employeeRepository.findByLoginId(account.getAccount()).get().getUserId())
                        .timestamp(LocalDateTime.now())
                        .content(content)
                        .build();
                accountHistoryRepository.save(authOptionHistory);
            }
            if(!member.getRole().toString().equals(account.getRole())){
                String content = member.getRole().toString() + " → " + account.getRole();
                AccountHistory roleHistory = AccountHistory.builder()
                        .category("auth_change")
                        .adminUserId(loginMember.getEmployee().getUserId())
                        .targetUserId(employeeRepository.findByLoginId(account.getAccount()).get().getUserId())
                        .timestamp(LocalDateTime.now())
                        .content(content)
                        .build();
                accountHistoryRepository.save(roleHistory);
            }
            if(!member.getAuthRw().equals(account.getRwCode())){
                String content = convertAuthRwCodeToValue(member.getAuthRw()) + " → " + convertAuthRwCodeToValue(account.getRwCode());
                AccountHistory authRwHistory = AccountHistory.builder()
                        .category("auth_change")
                        .adminUserId(loginMember.getEmployee().getUserId())
                        .targetUserId(employeeRepository.findByLoginId(account.getAccount()).get().getUserId())
                        .timestamp(LocalDateTime.now())
                        .content(content)
                        .build();
                accountHistoryRepository.save(authRwHistory);
            }

            member.setUsername(account.getAccount());
            member.setRole(Authority.valueOf(account.getRole()));
            member.setName(account.getUsername());
            member.setDescription(account.getNote());
            member.setAuthOption(account.getOptionCode());
            member.setAuthRw(account.getRwCode());
            member.setTargetCompanyId(account.getTargetCompanyId());
            member.setOtpYn(account.getOtpYn());

            // 비밀번호를 재설정했을 경우
            if(account.getPassword() != null){
                member.setPassword(passwordEncoder.encode(account.getPassword()));
            }
            // 그룹웨어 인원일 경우
            if(employeeRepository.findByLoginId(account.getAccount()).isPresent()){
                member.setEmployee(employeeRepository.findByLoginId(account.getAccount()).get());
            }
            memberRepository.save(member);
            successCode = "Ok";
        }
        return successCode;
    }

    public String createAccount(Member loginMember, AccountInfoDto account){
        if(account.getPassword() == null || account.getPassword().isEmpty() || account.getPassword().isBlank()){
            account.setPassword(defaultPassword);
        }
        Member member = Member.builder()
                .username(account.getAccount())
                .password(passwordEncoder.encode(account.getPassword()))
                .role(Authority.valueOf(account.getRole()))
                .name(account.getUsername())
                .description(account.getNote())
                .authOption(account.getOptionCode())
                .authRw(account.getRwCode())
                .targetCompanyId(account.getTargetCompanyId())
                .otpYn(account.getOtpYn())
                .build();
        if(employeeRepository.findByLoginId(account.getAccount()).isPresent()){
            member.setEmployee(employeeRepository.findByLoginId(account.getAccount()).get());
        }
        memberRepository.save(member);
        return "OK";
    }

    public Page<MasterAccountHistory> getAuthChangeHistory(LocalDate startDate, LocalDate endDate, String searchText, Pageable pageable){
        Page<MasterAccountHistory> authChangeHistory = masterAccountHistoryRepository.getAccountHistory(startDate, endDate, searchText, pageable);
        return authChangeHistory;
    }

    public Page<MasterAccountHistory> getLoginHistory(LocalDate startDate, LocalDate endDate, String searchText, Pageable pageable){
        Page<MasterAccountHistory> loginHistory = masterAccountHistoryRepository.getLoginHistory(startDate, endDate, searchText, pageable);
        return loginHistory;
    }

    public Page<MasterAccountHistory> getExcelHistory(LocalDate startDate, LocalDate endDate, String searchText, Pageable pageable){
        Page<MasterAccountHistory> excelHistory = masterAccountHistoryRepository.getExcelHistory(startDate, endDate, searchText, pageable);
        return excelHistory;
    }

    public Page<PeopleHistoryDto> getPeopleHistory(String typeCode, Boolean excludeGroupWare, LocalDate startDate, LocalDate endDate, String searchText, Pageable pageable){
        Page<PeopleHistoryDto> peopleHistory = historyRepository.getPeopleHistory(typeCode, excludeGroupWare, startDate, endDate, searchText, pageable);
        return peopleHistory;
    }

    public List<Code> getTypeList(){
        List<Code> typeList = codeRepository.findByCategory("type");
        return typeList;
    }

    public List<String> getCategoryList(){
        List<String> categoryList = codeRepository.getCategoryList();
        return categoryList;
    }

    public Page<Code> getCode(String category, String searchText, Pageable pageable){
        Page<Code> code = codeRepository.getCode(category, searchText, pageable);
        return code;
    }

    public String convertAuthOptionCodeToValue(String code){
        return codeRepository.convertAuthOptionCodeToValue(code);
    }

    public String convertAuthRwCodeToValue(String code){
        return codeRepository.convertAuthRwCodeToValue(code);
    }

}
