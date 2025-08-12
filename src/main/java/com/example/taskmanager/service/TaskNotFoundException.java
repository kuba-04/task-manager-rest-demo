package com.example.taskmanager.service;

import com.example.taskmanager.domain.TaskId;

public class TaskNotFoundException extends Exception {
    public TaskNotFoundException(TaskId taskId) {
        super(String.format("Task %s not found", taskId.toString()));
    }
}
