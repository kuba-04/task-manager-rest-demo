package com.example.taskmanager.service;

import com.example.taskmanager.domain.UserId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public record TaskEditDto(
        Optional<String> title,
        Optional<String> description,
        Optional<LocalDateTime> deadline,
        List<UserId> users
) {
}
