package com.example.todo.userapi.dto.request;

import lombok.*;

@Getter @Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestDTO {

    private String email;
    private String password;

}
