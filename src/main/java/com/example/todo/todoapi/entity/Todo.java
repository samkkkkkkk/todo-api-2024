package com.example.todo.todoapi.entity;

import com.example.todo.userapi.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_todo")
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String todoId;

    @Column(nullable = false, length = 30)
    private String title; //할일

    private boolean done; //할 일 완료 여부

    @CreationTimestamp
    private LocalDateTime createTime; // 등록시간

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


}
