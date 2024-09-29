package com.example.todo.userapi.service;

import com.example.todo.auth.TokenProvider;
import com.example.todo.auth.TokenUserInfo;
import com.example.todo.exception.NoRegisteredArgumentException;
import com.example.todo.userapi.dto.request.LoginRequestDTO;
import com.example.todo.userapi.dto.request.UserSignupRequestDTO;
import com.example.todo.userapi.dto.response.LoginResponseDTO;
import com.example.todo.userapi.dto.response.UserSignupResponseDTO;
import com.example.todo.userapi.entiy.Role;
import com.example.todo.userapi.entiy.User;
import com.example.todo.userapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    @Value("${upload.path}")
    private String uploadRootPath;

    @Value("${kakao.client.id}")
    private String KAKAO_CLIENT_ID;
    @Value("${kakao.client.id}")
    private String KAKAO_REDIERCT_URI;
    @Value("${kakao.client.id}")
    private String KAKAO_CLIENT_SECRET;


// 이메일 중복 확인
    public boolean isDuplicated(String email) {

        if (userRepository.existsByEmail(email)) {
            log.warn("이메일이 중복되었습니다. -{}", email);
            return true;
        } else return false;

    }


    // 회원가입
    public UserSignupResponseDTO create(final UserSignupRequestDTO requestDTO, final String uploadedFilePath){

        String email = requestDTO.getEmail();
        if (isDuplicated(email)) {
            throw new RuntimeException("중복된 이메일 입니다.");
        }

        // 패스워드 인코딩
        String encoded = passwordEncoder.encode(requestDTO.getPassword());
        requestDTO.setPassword(encoded);

        // DTO를 Entity로 변환하여 insert진행
        User saved = userRepository.save(requestDTO.toEntity(uploadedFilePath));
        log.info("회원가입이 정상적으로 수행됨! - saved user: {}", saved);

        // 생성자를 통해 Entity를 DTO로 변경하여 Controller에 리턴
        return new UserSignupResponseDTO(saved);
    }

    public LoginResponseDTO authenticate(final LoginRequestDTO dto) {

        // 이메일을 통해 회원 정보 조회
        User user = userRepository.findByEmail(dto.getEmail()).orElseThrow(
                () -> new RuntimeException("존재하지 않는 아이디 입니다."));
        
        // 패스워드 검증
        // 입력한 비밀번호, 암호화된 비밀번호를 매개값으로 전달
        String rawPassword = dto.getPassword();
        String encodedPassword = user.getPassword();

        boolean matches = passwordEncoder.matches(dto.getPassword(), encodedPassword);
        if (!matches) throw new RuntimeException("비밀번호가 틀렸습니다.");

        // 로그인 성공 후에 클라이언트에게 뭘 리턴해 줄 것인가?
        // ->JWT를 클라이언트에 발급해 주어야 한다. -> 로그인 유지를 위해!
        String token = tokenProvider.createToken(user);

        return new LoginResponseDTO(user, token);

    }


    public LoginResponseDTO promoteToPremium(TokenUserInfo userInfo) {

        User user = userRepository.findById(userInfo.getUserId()).orElseThrow(
                () -> new NoRegisteredArgumentException("회원 조회에 실패했습니다.")
        );

        // 일반(COMMON) 회원이 아니라면 예외 발생
        if (userInfo.getRole() != Role.COMMON) {
            throw new IllegalArgumentException("일반 회원이 아니라면 등급을 상승시킬 수 없습니다.");
        }

        // 등급 변경
        user.changeRole(Role.PREMIUM);
        User saved = userRepository.save(user);

        // 토큰을 재발급 (새롭게 변경된 정보가 반영된)
        String token = tokenProvider.createToken(saved);

        return new LoginResponseDTO(saved, token);


    }

    /**
     * 업로드 된 파일을 서버에 저장하고 저장 경로를 리턴
     * @param profileImage
     * @return 실제로 지정된 이미지 경로
     */
    public String uploadProfileImage(MultipartFile profileImage) throws IOException {
        // 루트 디렉토리가 실존하는 지 확인 후 존재하지 않으면 생성.
        File rootDir = new File(uploadRootPath);
        if (!rootDir.exists()) rootDir.mkdirs();

        // 파일명을 유니크하게 변경 (이름 충돌 가능성을 대비)
        // UUID와 원본 파일명을 결함 -> 규칙은 없음
        String uniqueFileName
                = UUID.randomUUID() + "_" + profileImage.getOriginalFilename();

        // 파일을 저장
        File uploadFile = new File(uploadRootPath + "/" + uniqueFileName);
        profileImage.transferTo(uploadFile);

        return uniqueFileName;

    }

    public String findProfilePath(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException());
        // DB에는 파일명만 저장. -> service가 가지고 있는 Root Path와 연결해서 리턴
        return uploadRootPath + "/" + user.getProfileImg();

    }

    public void kakaoService(String code) {

    }
}
