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


}
