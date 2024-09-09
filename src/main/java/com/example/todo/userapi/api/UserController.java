package com.example.todo.userapi.api;

import com.example.todo.userapi.dto.request.LoginRequestDTO;
import com.example.todo.userapi.dto.request.UserSignupRequestDTO;
import com.example.todo.userapi.dto.response.UserSignupResponseDTO;
import com.example.todo.userapi.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

        if (result.hasErrors()) {
            log.warn(result.toString());
            return ResponseEntity.badRequest()
                    .body(result.getFieldErrors());
        }

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

}
