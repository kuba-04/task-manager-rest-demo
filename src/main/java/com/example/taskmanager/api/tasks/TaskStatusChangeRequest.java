package com.example.taskmanager.api.tasks;

import com.example.taskmanager.domain.TaskStatus;

public record TaskStatusChangeRequest(TaskStatus status) {
}
