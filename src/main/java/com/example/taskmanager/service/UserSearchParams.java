package com.example.taskmanager.service;

import com.example.taskmanager.domain.UserId;

public record UserSearchParams(UserId id, String firstName, String lastName, String email) {
}
