package com.example.taskmanager.service;

import com.example.taskmanager.domain.UserId;

public class UserNotFoundException extends Exception {
    public UserNotFoundException(UserId userId) {
        super(String.format("User %s not found", userId.toString()));
    }
}
