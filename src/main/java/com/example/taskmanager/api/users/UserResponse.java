package com.example.taskmanager.api.users;

public record UserResponse(
        String id,
        String firstName,
        String lastName,
        String email
) {
}
