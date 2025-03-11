package com.example.Todo.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Todo.model.Task;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // Find tasks by userId (to ensure users only get their tasks)
    List<Task> findByUserId(Long userId);

    // Optional: If you want more custom queries, you can add them here
}
