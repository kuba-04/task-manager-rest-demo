package com.example.taskmanager.domain;

import jakarta.persistence.Column;

import java.util.UUID;

public record TaskId(@Column(name = "id") UUID id) {
    public static TaskId generate() {
        return new TaskId(UUID.randomUUID());
    }
}