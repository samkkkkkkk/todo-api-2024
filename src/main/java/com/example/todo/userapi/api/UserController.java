package com.example.todo.userapi.api;

import com.example.todo.auth.TokenUserInfo;
import com.example.todo.userapi.dto.request.LoginRequestDTO;
import com.example.todo.userapi.dto.request.UserSignupRequestDTO;
import com.example.todo.userapi.dto.response.LoginResponseDTO;
import com.example.todo.userapi.dto.response.UserSignupResponseDTO;
import com.example.todo.userapi.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;

    // 이메일 중복 확인 요청 처리
    // GET: /api/auth/check?email=zzz@xxx.com
    // jpa: pk로 조회하는 메서드는 기본 제공되지만, 다른 컬럼으로 조회하는 메서드는 제공되지 않습니다.
    @GetMapping("/check")
    public ResponseEntity<?> duplicatedEmail(
            @RequestParam(value = "email") String email
    ) {
        if (email.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("이메일이 없습니다.");
        }

        boolean resultFlag = userService.isDuplicated(email);
        log.info("중복여부: {}", resultFlag);
        return ResponseEntity.ok().body(resultFlag);

    }

    // 회원가입 요청
    @PostMapping
    public ResponseEntity<?> userJoin(
            @Validated @RequestBody UserSignupRequestDTO requestDTO,
            BindingResult result
    ) {
        log.info("회원가입 요청이 들어옴!");

        ResponseEntity<List<FieldError>> resultEntity = getFieldErrorResponseEntity(result);
        if (resultEntity != null) return resultEntity;

        try {
            UserSignupResponseDTO responseDTO = userService.create(requestDTO);
            return ResponseEntity.ok()
                    .body(responseDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }

    }

    // 로그인 요청 처리 메서드 선언하세요.
    // LoginRequestDTO 클래스를 생성해서 요청 값을 받아 주세요.
    // 서비스로 넘겨서, 로그인 유효성을 검증하세요. (비밀번호 암호화되어 있어요.)
    // 로그인 결과를 응답 상태 코드로 구분해서 보내주세요.
    // 로그인이 성공했다면 200, 로그인 실패라면 400을 보내세요. (에러 메세지를 상황에 따라 다르게 전달해 주세요.)
    @PostMapping("/signin")
    public ResponseEntity<?> signin(
            @Validated @RequestBody LoginRequestDTO dto,
            BindingResult result
            ) {
        log.info("api/autu/signin POST방식 요청이 들어옴 - userInfo: {}", dto);

        ResponseEntity<List<FieldError>> response = getFieldErrorResponseEntity(result);
        if (response != null) return response;

        try {
            LoginResponseDTO responseDTO = userService.authenticate(dto);
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }


    }

    // 일반 회원을 프리미엄 회원으로 승격하는 요청 처리
    @PutMapping("/promote")
    // 권한검사 (해당 권한이 아니라면 인가처리 거부 -> 403 상태 리턴)
    // 메서드 호출 전에 검사 -> 요청 당시 토큰에 있는 user 정보가 ROLE_COMMON 권한을가지고 있는지를 검사.
    @PreAuthorize("hasRole('ROLE_COMMON)")
    public ResponseEntity<?> promote(
            @AuthenticationPrincipal TokenUserInfo userInfo
            ) {
        log.info("/api/auth/promote - PUT!");
        return "";

    }


    // 입력값 검증 메서드
    private static ResponseEntity<List<FieldError>> getFieldErrorResponseEntity(BindingResult result) {
        if (result.hasErrors()) {
            log.warn(result.toString());
            return ResponseEntity.badRequest()
                    .body(result.getFieldErrors());
        }
        return null;
    }

}
