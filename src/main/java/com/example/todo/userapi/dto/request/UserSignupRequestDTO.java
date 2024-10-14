package com.example.todo.userapi.dto.request;

import com.example.todo.userapi.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter
@ToString
@EqualsAndHashCode(of = "email")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSignupRequestDTO {

    @NotBlank
    @Size(min = 2, max = 5)
    private String userName;

    @NotBlank
    private String password;

    @NotBlank
    @Size(min = 8, max = 20)
    private String email;

    public User toEntity(String uploadedFilePath) {
        return User.builder()
                .userName(this.userName)
                .email(this.email)
                .password(this.password)
                .profileImg(uploadedFilePath)
                .build();
    }

}
