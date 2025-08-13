package com.example.taskmanager.api.tasks;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

// TODO: validations for incoming requests
public record TaskCreationRequest(
        String title,
        String description,
        LocalDateTime deadline,
        List<UUID> users
) {
}
