package com.example.todo.dto.response;

import com.example.todo.entity.Todo;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoDetailResponseDTO {

    private String id;
    private String title;
    private Boolean done;
    private LocalDateTime createTime;

    // 엔터티를 DTO로 변경하는 생성자
    public TodoDetailResponseDTO(Todo todo) {
        this.id = todo.getTodoId();
        this.title = todo.getTitle();
        this.done = todo.isDone();
        this.createTime = todo.getCreateTime();
    }
}
