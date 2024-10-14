package com.example.todo.userapi.dto.response;

import com.example.todo.userapi.entity.User;
import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSignupResponseDTO {

    private String userName;
    private String email;

    // entity를 dto로 변환해주는 생성자
    public UserSignupResponseDTO(User user) {
        this.userName = user.getUserName();
        this.email = user.getEmail();
    }
}
