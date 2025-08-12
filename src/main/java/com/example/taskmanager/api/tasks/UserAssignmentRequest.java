package com.example.taskmanager.api.tasks;

import java.util.List;

public record UserAssignmentRequest(List<String> userIds) {
}
