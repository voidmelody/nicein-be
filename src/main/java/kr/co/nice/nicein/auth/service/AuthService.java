package kr.co.nice.nicein.auth.service;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import kr.co.nice.nicein.auth.dto.RegisterRequestDto;
import kr.co.nice.nicein.auth.dto.TokenDto;
import kr.co.nice.nicein.auth.entity.AuthMenu;
import kr.co.nice.nicein.auth.entity.Member;
import kr.co.nice.nicein.auth.repository.AuthMenuRepository;
import kr.co.nice.nicein.auth.repository.MemberRepository;

import kr.co.nice.nicein.common.TOTPTokenGenerator;
import kr.co.nice.nicein.common.TOTPTokenValidation;
import kr.co.nice.nicein.common.mail.MailHandler;
import kr.co.nice.nicein.groupware.entity.Code;
import kr.co.nice.nicein.groupware.entity.Employee;
import kr.co.nice.nicein.groupware.repository.EmployeeRepository;
import kr.co.nice.nicein.security.Authority;
import kr.co.nice.nicein.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

import static io.jsonwebtoken.Claims.ISSUER;


@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final MemberRepository memberRepository;
    private final EmployeeRepository employeeRepository;
    private final AuthMenuRepository authMenuRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final JavaMailSender mailSender;

    public Boolean register(RegisterRequestDto request){
        if(memberRepository.findByUsername(request.getUsername()).isPresent()){
            throw new IllegalStateException("이미 등록된 회원입니다.");
        }
        Employee employee = employeeRepository.findByLoginId(request.getUsername()).orElseThrow();
        Member member = Member.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .role(Authority.valueOf(request.getRole()))
                .description(request.getDescription())
                .authOption("0")
                .authRw("0")
                .otpYn("N")
                .targetCompanyId(employee.getCompany().getCompanyId())
                .build();
        member.setEmployee(employee);
        memberRepository.save(member);
        return true;
    }

    public boolean setOtpAndSendMail(Member member){
        String secretKey = TOTPTokenGenerator.generateSecretKey();
        String account = member.getUsername();
        String issuer = "Nice-IN-OTP";
        String barcodeUrl = TOTPTokenGenerator.getGoogleAuthenticatorBarcode(secretKey,account,issuer);
        String content = makeTextContent(secretKey,barcodeUrl);

        sendEmail(account,"NICE-IN OTP 인증 비밀번호",content);
        member.setOtpKey(secretKey);
        memberRepository.save(member);
        return true;
    }

    public String makeTextContent(String secretKey, String barcodeUrl){
        String firstStr = "<p>안녕하세요. NICE IN 관리자입니다.</p><br/>";
        String secondStr = "<p>Google OTP key : " + secretKey + "</p><br/>";
        String thirdStr = "<p>해당 Key를 Google Authenticator 앱에 입력하여 사용하시면 됩니다.</p><br/>" + "<p>인터넷이 되는 환경의 경우, QR 링크로도 등록이 가능합니다.</p>";
        String fourthStr = "<p>Google OTP QR Code : " + "<a href='" + barcodeUrl + "'>" + barcodeUrl + "</a></p><br/>";
        String firstProcess = "<p>1. Google Authenticator 앱 접속</p><br/>";
        String secondProcess = "<p>2. '코드 추가' 버튼 클릭</p><br/>";
        String thirdProcess = "<p>3. 제공된 Key를 '설정 키 입력' 에 입력하시거나 QR 코드 이미지를 'QR 코드 스캔'에 스캔해주세요.</p><br/>";
        String fourthProcess = "<p>4. 간혹 QR 코드 링크가 오류가 뜨는 경우 상단의 URL을 클릭한 후 다시 ENTER를 눌러주세요.</p>";
        String finalStr = "<p>자세한 사항은 관리자에게 문의해주세요.</p><p>감사합니다.</p>";

        String textContent = firstStr + secondStr + thirdStr + fourthStr + firstProcess + secondProcess + thirdProcess + fourthProcess + finalStr;
        return textContent;
    }

    public boolean otpLogin(Member member, String code){
        TOTPTokenValidation totpTokenValidation = new TOTPTokenValidation();
        return totpTokenValidation.validate(member.getOtpKey(), code);
    }

    public void sendEmail(String to, String subject, String content) {
        try{
            MailHandler mailHandler = new MailHandler(mailSender);
            mailHandler.setTo(to);
            mailHandler.setFrom("nicein@nice.co.kr");
            mailHandler.setSubject(subject);
            mailHandler.setText(content, true);
            mailHandler.send();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public String validRefreshToken(Member member, String token){
        String findMemberRefreshToken = member.getRefreshToken();
        log.info("refresh token : " + member.getRefreshToken());

        if(findMemberRefreshToken == null){
            return null;
        }
        if(findMemberRefreshToken.equals(token)){
            boolean isValid = jwtTokenProvider.validateToken(token);
            if(isValid){
                return token;
            }else{
                return null;
            }
        }
        return null;
    }

    public TokenDto reissue(TokenDto tokenDto) throws Exception {
        log.info("토큰을 재발급합니다");
        String username = jwtTokenProvider.getUserName(tokenDto.getAccessToken());
        log.info(username);
        Member member = memberRepository.findByUsername(username).
                orElseThrow(()-> new BadCredentialsException("잘못된 계정 정보입니다."));
        String refreshToken = validRefreshToken(member, tokenDto.getRefreshToken());
        if(refreshToken != null){
            TokenDto reissueToken = jwtTokenProvider.createToken(username, member.getRole().toString());
            log.info(reissueToken.getAccessToken());
            String reissueRefreshToken = reissueToken.getRefreshToken();
            member.setRefreshToken(reissueRefreshToken);
            memberRepository.save(member);
            log.info("토큰 재발급이 완료되었습니다.");
            return reissueToken;
        }else{
            throw new Exception("로그인을 해주세요.");
        }
    }

    public Boolean logout(Member member, HttpServletRequest request){
        if(member == null){
            return false;
        }
        String accessToken = jwtTokenProvider.resolveToken(request);
        member.setRefreshToken(accessToken);
        memberRepository.save(member);
        return true;
    }

    public AuthMenu getAuthMenu(Member member){
        AuthMenu authMenu = authMenuRepository.findByRole(member.getRole().toString()).get();
        return authMenu;
    }

    public Boolean hasGroupItAuthority(Member member){
        if(member.getAuthOption().equals("0")){
            return true;
        }else{
            return false;
        }
    }

    public Boolean hasWriteAuthority(Member member){
        if(member.getAuthRw().equals("1")){
            return true;
        }else{
            return false;
        }
    }

    public List<Code> getOptionList(){
        List<Code> optionList = memberRepository.getOptionList();
        return optionList;
    }

    public List<Code> getRwList(){
        List<Code> rwList = memberRepository.getRwList();
        return rwList;
    }

}
