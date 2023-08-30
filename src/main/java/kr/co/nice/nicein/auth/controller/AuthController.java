package kr.co.nice.nicein.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.nice.nicein.auth.entity.AuthMenu;
import kr.co.nice.nicein.auth.service.AuthService;
import kr.co.nice.nicein.auth.entity.Member;
import kr.co.nice.nicein.auth.dto.RegisterRequestDto;
import kr.co.nice.nicein.auth.dto.TokenDto;
import kr.co.nice.nicein.common.ErrorWriter;
import kr.co.nice.nicein.groupware.entity.Code;
import kr.co.nice.nicein.security.Authority;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final ErrorWriter ew;

    // 로그인 ("auth/login")은 UsernamePasswordAuthenticationFilter로 구현.


    //OTP 2차 로그인
    @GetMapping("/otp/login")
    public ResponseEntity otpLogin(@AuthenticationPrincipal Member member, @RequestParam String code){
        try{
            Map<String, Object> data = new HashMap<>();
            boolean otpLoginResult = authService.otpLogin(member, code);
            data.put("data", otpLoginResult);
            return new ResponseEntity(data,HttpStatus.OK);
        }catch (Exception e){
            log.error(ew.getPrintStackTrace(e));
            return  new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/otp/mail")
    public ResponseEntity sendKeyMail(@AuthenticationPrincipal Member member){
        try{
            Map<String,Object> data = new HashMap<>();
            boolean sendResult = authService.setOtpAndSendMail(member);
            data.put("data", sendResult);
            return new ResponseEntity(data,HttpStatus.OK);
        }catch (Exception e){
            log.error(ew.getPrintStackTrace(e));
            return  new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<Boolean> logout(@AuthenticationPrincipal Member member, HttpServletRequest request){
        try{
            Map<String,Object> data = new HashMap<>();
            Boolean isLogout = authService.logout(member, request);
            data.put("data", isLogout);
            return new ResponseEntity(data, HttpStatus.OK);
        }catch (Exception e){
            log.error(ew.getPrintStackTrace(e));
            return  new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 회원가입
    @PostMapping("/register")
    public ResponseEntity signUp(@RequestBody RegisterRequestDto request){
        try{
            Map<String,Object> data = new HashMap<>();
            Boolean isRegister = authService.register(request);
            data.put("data", isRegister);
            return new ResponseEntity(data,HttpStatus.OK);
        }catch(Exception e){
            log.error(ew.getPrintStackTrace(e));
            return  new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // 토큰 재발급
    @PostMapping("/reissue")
    public ResponseEntity reissue(@RequestBody TokenDto token){
        try{
            Map<String,Object> data = new HashMap<>();
            TokenDto reissue = authService.reissue(token);
            data.put("data", reissue);
            return new ResponseEntity(data, HttpStatus.OK);
        }catch (Exception e){
            log.error(ew.getPrintStackTrace(e));
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 권한에 따른 메뉴 접근 제공
    @GetMapping("/menu")
    public ResponseEntity getAuthMenu(@AuthenticationPrincipal Member member){
        try{
            Map<String,Object> data = new HashMap<>();
            AuthMenu authMenu = authService.getAuthMenu(member);
            data.put("data", authMenu);
            return new ResponseEntity(data, HttpStatus.OK);
        }catch (Exception e){
            log.error(ew.getPrintStackTrace(e));
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //GroupIT 권한 옵션 가지고 있는지 여부
    @GetMapping("/option")
    public ResponseEntity hasGroupItAuthority(@AuthenticationPrincipal Member member){
        try{
            Map<String,Object> data = new HashMap<>();
            Boolean authOption = authService.hasGroupItAuthority(member);
            data.put("data", authOption);
            return new ResponseEntity(data, HttpStatus.OK);
        }catch (Exception e){
            log.error(ew.getPrintStackTrace(e));
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 쓰기 권한을 가지고 있는지 여부
    @GetMapping("/rw")
    public ResponseEntity hasWriteAuthority(@AuthenticationPrincipal Member member){
        try{
            Map<String, Object> data = new HashMap<>();
            Boolean authRw = authService.hasWriteAuthority(member);
            data.put("data", authRw);
            return new ResponseEntity(data, HttpStatus.OK);
        }catch (Exception e){
            log.error(ew.getPrintStackTrace(e));
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/optionList")
    public ResponseEntity getOptionList(){
        try{
            Map<String, Object> data = new HashMap<>();
            List<Code> optionList = authService.getOptionList();
            data.put("data", optionList);
            return new ResponseEntity(data, HttpStatus.OK);
        }catch (Exception e){
            log.error(ew.getPrintStackTrace(e));
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/rwList")
    public ResponseEntity getRwList(){
        try{
            Map<String, Object> data = new HashMap<>();
            List<Code> rwList = authService.getRwList();
            data.put("data", rwList);
            return new ResponseEntity(data, HttpStatus.OK);
        }catch (Exception e){
            log.error(ew.getPrintStackTrace(e));
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/roleList")
    public ResponseEntity getRoleList(){
        try{
            Map<String, Object> data = new HashMap<>();
            List<String> roleList = Arrays.stream(Authority.values()).map(Authority::name).toList();
            data.put("data", roleList);
            return new ResponseEntity(data, HttpStatus.OK);
        }catch (Exception e){
            log.error(ew.getPrintStackTrace(e));
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
