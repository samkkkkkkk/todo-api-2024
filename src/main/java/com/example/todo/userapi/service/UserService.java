package com.example.todo.userapi.service;

import com.example.todo.userapi.dto.request.LoginRequestDTO;
import com.example.todo.userapi.dto.request.UserSignupRequestDTO;
import com.example.todo.userapi.dto.response.UserSignupResponseDTO;
import com.example.todo.userapi.entiy.User;
import com.example.todo.userapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

// 이메일 중복 확인
    public boolean isDuplicated(String email) {

        if (userRepository.existsByEmail(email)) {
            log.warn("이메일이 중복되었습니다. -{}", email);
            return true;
        } else return false;

    }


    // 회원가입
    public UserSignupResponseDTO create(UserSignupRequestDTO requestDTO) throws Exception{

        String email = requestDTO.getEmail();
        if (isDuplicated(email)) {
            throw new RuntimeException("중복된 이메일 입니다.");
        }

        // 패스워드 인코딩
        String encoded = passwordEncoder.encode(requestDTO.getPassword());
        requestDTO.setPassword(encoded);

        // DTO를 Entity로 변환하여 insert진행
        User saved = userRepository.save(requestDTO.toEntity());
        log.info("회원가입이 정상적으로 수행됨! - saved user: {}", saved);

        // 생성자를 통해 Entity를 DTO로 변경하여 Controller에 리턴
        return new UserSignupResponseDTO(saved);
    }

    public String authenticate(final LoginRequestDTO dto) throws Exception{

        // 이메일 존재 여부
        String email = dto.getEmail();
        if(!isDuplicated(email)) {
            throw new RuntimeException("이메일이 존재하지 않습니다.");
        }

        // 이메일을 통해 회원 정보 조회
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new RuntimeException("존재하지 않는 아이디 입니다."));
        
        // 패스워드 검증
        // 입력한 비밀번호, 암호화된 비밀번호를 매개값으로 전달
        String rawPassword = dto.getPassword();
        String encodedPassword = user.getPassword();

        boolean matches = passwordEncoder.matches(dto.getPassword(), encodedPassword);
        if (!matches) throw new RuntimeException("비밀번호가 틀렸습니다.");

        // 로그인 성공 후에 클라이언트에게 뭘 리턴해 줄 것인가?
        // ->JWT를 클라이언트에 발급해 주어야 한다. -> 로그인 유지를 위해!

        return "success";

    }


}
