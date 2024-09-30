package com.example.todo.userapi.dto.response;

import com.example.todo.userapi.entiy.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @ToString
@Builder
public class KakaoUserDTO {

    private long id;
    @JsonProperty("connected_at")
    private LocalDateTime connectedAt;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Setter @Getter
    @ToString
    public static class KakaoAccount {
        private String email;
        private Profile profile;

        @Getter @Setter
        @ToString
        public static class Profile {
            private String nickname;

            @JsonProperty("profile_image_url")
            private String profileImageUrl;
        }
    }

    public User toEntity(String accessToken) {
        return User.builder()
                .email(this.kakaoAccount.getEmail())
                .userName(this.kakaoAccount.profile.getNickname())
                .password("password!")
                .profileImg(this.kakaoAccount.profile.getProfileImageUrl())
                .accessToken(accessToken)
                .build();
    }

}
