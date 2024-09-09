package com.example.todo.config;

import com.example.todo.filter.JWTAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JWTAuthFilter jwtAuthFilter;

    // 시큐리티 기본 설정 (권한처리, 초기 로그인 화면 없애기 ...)
    @Bean // 라이브러리 클래스 같은 내가 만들지않은 객체를 등록해서 주입받기 위한 아노테이션.
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrfConfig -> csrfConfig.disable()) // CSRF 토큰 공격을 방지하기 위한 장치 해제.
                .cors(Customizer.withDefaults())
                // 세션 관리 상태를 STATELESS로 설정해서 spring security가 제공하는 세션 생성 및 관리 기능 사용하지 않겠다.
                .sessionManagement(httpSecuritySessionManagementConfigurer ->
                        httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // spring에서 제공하는 기본 로그인 폼 사용 안함, http 기반 기본 인증도 안 쓰겠다.
                .formLogin(form -> form.disable())
                .httpBasic(AbstractHttpConfigurer::disable)
                // 우리가 만든 jwtAuthFilte를 UsernamePasswordAuthenticationFilter보다 먼저 동작하도록 설정.
                // security를 사용하면, 서버가 가동될 때 기본적으로 제공하는 여러가지 필터가 세팅이 되는데,
                // jwtAuthFilter를 먼저 배치해서, 얘를 통과하면 인증이 완료가 되도록 처리
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                // '/api/todos'라는 요청이 post로 들어오고, Role 값이 ADMIN인 경우 권한 검사 없이 허용하겠다.
//                                .requestMatchers(HttpMethod.POST, "/api/todos").hasRole("ADMIN")
                                // '/api/auth'로 시작하는 요청과 '/' 요청은 권한 검사 없이 허용하겠다.
                                .requestMatchers("/", "/api/auth/**")
                                .permitAll()
                                // 위에서 따로 설정하지 않은 나머지 요청들은 권한 검사가 필요하다.
                                .anyRequest().authenticated()

                );

        return http.build();
    }

    // 비밀번호 암호화 객체를 빈 등록
    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

}