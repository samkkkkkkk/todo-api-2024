package com.example.todo.auth;

import com.example.todo.userapi.entiy.Role;
import lombok.*;

@Getter @ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenUserInfo {

    private String userId;
    private String email;
    private Role role;

}
