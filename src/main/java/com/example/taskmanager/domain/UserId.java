package com.example.taskmanager.domain;

import jakarta.persistence.Column;

import java.util.UUID;

public record UserId(@Column(name = "id") UUID id) {
    public static UserId generate() {
        return new UserId(UUID.randomUUID());
    }
}