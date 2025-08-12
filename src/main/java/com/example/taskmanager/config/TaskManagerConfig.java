package com.example.taskmanager.config;

import com.example.taskmanager.db.TaskRepository;
import com.example.taskmanager.db.UserRepository;
import com.example.taskmanager.service.TaskService;
import com.example.taskmanager.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TaskManagerConfig {

    @Bean
    public UserService userService(UserRepository repository) {
        return new UserService(repository);
    }

    @Bean
    public TaskService taskService(TaskRepository taskRepository, UserRepository userRepository) {
        return new TaskService(taskRepository, userRepository);
    }
}
