package com.example.taskmanager.api.users;

public record AddUserRequest(
        String firstName,
        String lastName,
        String email
) {
}
