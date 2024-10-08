package com.example.todo.filter;

import com.example.todo.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class JwtExceptionFilter extends OncePerRequestFilter {

    // OncePerRequestFilter을 상속 받아 요청 한번에 동작 한번만 하도록

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        try {
            // 예외가 발생하지 않으면 필터를 통과~
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            // 토큰이 만료되었을 시 Auth Filter에서 예외가 발생 -> 앞에 있는 Exception Filter로 전달.
            log.warn("ExpiredJwtException 발생함!");
            setErrorResponse(response, ErrorCode.EXPIRED_TOKEN);
        } catch (JwtException e) {
            log.warn("JwtException 발생함!");
            setErrorResponse(response, ErrorCode.INVALID_TOKEN);
        } catch (IllegalArgumentException e) {
            log.warn("토큰이 전달되지 않음!");
            setErrorResponse(response, ErrorCode.INVALID_AUTH);
        }
    }

    public void setErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType("application/json; charset=UTF-8");

        //Map 생성 및 데이터 추가
        Map<String, Object> responsMap = new HashMap<>();
        responsMap.put("message", errorCode.getMassage());
        responsMap.put("code", errorCode.getHttpStatus());

        // Map을 JSON 문자열로 변환
        String jsonString = new ObjectMapper().writeValueAsString(responsMap);

        // JSON 데이터를 응답객체에 실어서 브라우저로 바로 전달.
        response.getWriter().write(jsonString);

    }

}
