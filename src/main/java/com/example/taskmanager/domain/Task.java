package com.example.taskmanager.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Task {
    private final TaskId id;
    private final String title;
    private final String description;
    private final LocalDateTime deadline;
    private TaskStatus taskStatus;
    private final List<UserId> assignedUsers;

    private Task(TaskId id, String title, String description, LocalDateTime deadline, TaskStatus taskStatus, List<UserId> assignedUsers) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.taskStatus = taskStatus;
        this.assignedUsers = new ArrayList<>(assignedUsers);
    }

    public static Task create(TaskId id, String title, String description, LocalDateTime deadline, List<UserId> assignedUsers) {
        if (id == null) {
            throw new IllegalArgumentException("Task ID cannot be empty");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Task title cannot be empty");
        }
        return new Task(id, title, description, deadline, TaskStatus.New, assignedUsers);
    }

    public void assignUser(UserId userId) {
        if (assignedUsers.contains(userId)) {
            // a business decision to ignore this for now instead of throwing an exception
            return;
        }
        assignedUsers.add(userId);
    }

    public void changeStatus(TaskStatus newStatus) {
        taskStatus = newStatus;
    }

    public TaskId getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public List<UserId> getAssignedUsers() {
        return new ArrayList<>(assignedUsers);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return Objects.equals(id, task.id) &&
               Objects.equals(title, task.title) &&
               Objects.equals(description, task.description) &&
               Objects.equals(deadline, task.deadline) &&
               taskStatus == task.taskStatus &&
               Objects.equals(assignedUsers, task.assignedUsers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, deadline, taskStatus, assignedUsers);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", deadline=" + deadline +
                ", taskStatus=" + taskStatus +
                ", assignedUsers=" + assignedUsers +
                '}';
    }
}
