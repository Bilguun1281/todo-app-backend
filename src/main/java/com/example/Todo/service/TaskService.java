package com.example.Todo.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Todo.model.Task;
import com.example.Todo.repository.TaskRepository;
import com.example.Todo.repository.UserRepository;

import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    // Fetch tasks for a user by their userId
    public List<Task> getTasksByUserId(Long userId) {
        return taskRepository.findByUserId(userId);  // Fetch tasks associated with the userId
    }

    // Create a new task
    public Task createTask(Task task, Long userId) {
        task.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found")));  // Set user from the userId
        return taskRepository.save(task);  // Save the task for the user
        
    }

    // Update an existing task
    public Task updateTask(Long taskId, Task taskDetails) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
  
        task.setText(taskDetails.getText());
 
        
        return taskRepository.save(task);  // Save the updated task
    }

    // Delete a task
    public void deleteTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        taskRepository.delete(task);  // Delete the task
    }
}
