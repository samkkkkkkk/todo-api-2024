package com.example.todo.todoapi.dto.request;

import com.example.todo.todoapi.entity.Todo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.stereotype.Service;

@Service
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoCreateRequestDTO {

    @NotBlank
    @Size(min = 2, max = 30)
    private String title;


    // DTO를 Entity로 변환
    public Todo toEntity() {
        return Todo.builder()
                .title(this.title)
                .build();
    }



}
