package com.example.taskmanager.api.tasks;

import com.example.taskmanager.domain.UserId;
import com.example.taskmanager.service.TaskEditDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public record EditTaskRequest(
        String title,
        String description,
        LocalDateTime deadline,
        List<UUID> users
) {
    public TaskEditDto toDto() {
        return new TaskEditDto(
                Optional.ofNullable(title),
                Optional.ofNullable(description),
                Optional.ofNullable(deadline),
                Optional.ofNullable(users)
                        .map(uuids -> uuids.stream().map(UserId::new).toList())
                        .orElse(List.of()));
    }
}
