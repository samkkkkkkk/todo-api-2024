package com.example.todo.service;

import com.example.todo.dto.request.TodoCreateRequestDTO;
import com.example.todo.dto.request.TodoModifyRequestDTO;
import com.example.todo.dto.response.TodoDetailResponseDTO;
import com.example.todo.dto.response.TodoListResponseDTO;
import com.example.todo.entity.Todo;
import com.example.todo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoListResponseDTO create(TodoCreateRequestDTO requestDTO) throws Exception{

        todoRepository.save(requestDTO.toEntity());
        log.info("할 일 저장 완료! 제목: {}", requestDTO.getTitle());
        return retrieve();
    }

    // 할 일 목록 가져오기
    public TodoListResponseDTO retrieve() throws Exception{
        // JPQL을 이용해서 createTime을 기준으로 오름차정렬 하는 방법
//        List<Todo> entityList = todoRepository.findAllByOrderByCreateTime();
        List<Todo> entityList = todoRepository.findAll();
        List<TodoDetailResponseDTO> dtoList = entityList.stream()
//                .map(entity -> new TodoDetailResponseDTO(entity))
                .map(TodoDetailResponseDTO::new)
                // stream의 sort를 이용해서 createTime을 기준으로 오름차정렬하는 방법
                .sorted(Comparator.comparing(TodoDetailResponseDTO::getCreateTime))
                .collect(Collectors.toList());

        return TodoListResponseDTO
                .builder()
                .todos(dtoList)
                .build();

    }

    
    // 할 일 삭제
    public TodoListResponseDTO delete(final String todoId) throws Exception {

        todoRepository.findById(todoId).orElseThrow(
                () -> {
                    log.error("id가 존재하지 않아 삭제에 실패했습니다. - ID: {}", todoId);
                    throw new RuntimeException("id가 존재하지 않아 삭제에 실패했습니다.");
                }
        );
            todoRepository.deleteById(todoId);

        return retrieve();
    }


    // 할 일 수정
    public TodoListResponseDTO update(final TodoModifyRequestDTO requestDTO) throws Exception {
        Optional<Todo> targetEntity
                = todoRepository.findById(requestDTO.getId());

        targetEntity.ifPresent(todo -> {
            todo.setDone(requestDTO.isDone());
            todoRepository.save(todo);
        });

        return retrieve();
    }
}
