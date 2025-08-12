package com.example.taskmanager.api.tasks;

import com.example.taskmanager.domain.TaskStatus;

import java.time.LocalDateTime;
import java.util.List;

public record TaskResponse(
        String id,
        String title,
        String description,
        LocalDateTime deadline,
        TaskStatus taskStatus,
        List<String> users
) {
}
