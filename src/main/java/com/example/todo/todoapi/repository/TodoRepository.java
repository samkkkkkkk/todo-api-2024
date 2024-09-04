package com.example.todo.todoapi.repository;

import com.example.todo.todoapi.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, String> {

    List<Todo> findAllByOrderByCreateTime();
}
