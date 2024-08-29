package com.example.todo.dto.response;

import com.example.todo.entity.Todo;
import lombok.*;

@Getter @Setter @ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoDetailResponseDTO {

    private String id;
    private String title;
    private Boolean done;

    // 엔터티를 DTO로 변경하는 생성자
    public TodoDetailResponseDTO(Todo todo) {
        this.id = todo.getTodoId();
        this.title = todo.getTitle();
        this.done = todo.isDone();
    }
}
