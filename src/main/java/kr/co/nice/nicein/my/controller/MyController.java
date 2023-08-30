package kr.co.nice.nicein.my.controller;

import ch.qos.logback.core.encoder.EchoEncoder;
import jakarta.transaction.Transactional;
import kr.co.nice.nicein.common.ErrorWriter;
import kr.co.nice.nicein.my.dto.MyResponseDto;
import kr.co.nice.nicein.auth.entity.Member;
import kr.co.nice.nicein.auth.service.AuthService;
import kr.co.nice.nicein.my.service.MyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@Transactional
@RestController
@RequestMapping("/my")
@RequiredArgsConstructor
public class MyController {
    private final MyService memberService;
    private final ErrorWriter ew;

    @GetMapping()
    public ResponseEntity getUser(@AuthenticationPrincipal Member member){
        try{
            Map<String, Object> data = new HashMap<>();
            MyResponseDto responseDto = memberService.getMember(member);
            data.put("data", responseDto);
            return new ResponseEntity(data, HttpStatus.OK);
        }catch(Exception e){
            log.error(ew.getPrintStackTrace(e));
            return  new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}