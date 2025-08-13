package com.example.taskmanager.service;

import com.example.taskmanager.domain.TaskStatus;
import com.example.taskmanager.domain.UserId;

import java.time.LocalDateTime;

public record TaskSearchParams(
    String title,
    String description,
    TaskStatus taskStatus,
    LocalDateTime deadlineFrom,
    LocalDateTime deadlineTo,
    UserId assignedUserId
) {}