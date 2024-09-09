package com.example.todo.userapi.repository;

import com.example.todo.userapi.entiy.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    // 이메일 중복 체크
//    @Query("SELECT COUNT(*) FROM USER u WHERE u.email = :email") -> JPQL

    // 쿼리 메서드 방식
    Boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmail(String email);
}
