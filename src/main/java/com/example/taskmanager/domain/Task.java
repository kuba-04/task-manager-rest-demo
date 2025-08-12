package com.example.taskmanager.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tasks")
public class Task {
    @EmbeddedId
    private TaskId id;
    private String title;
    private String description;
    private LocalDateTime deadline;
    @Enumerated(EnumType.STRING)
    private TaskStatus taskStatus;
    @ElementCollection
    @CollectionTable(
            name = "task_assigned_users",
            joinColumns = @JoinColumn(name = "task_id")
    )
    private List<String> assignedUsers; // flatten this to string due to hibernate issues with new spring

    // required by hibernate
    private Task() {}

    private Task(TaskId id, String title, String description, LocalDateTime deadline, TaskStatus taskStatus, List<UserId> assignedUsers) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.taskStatus = taskStatus;
        this.assignedUsers = new ArrayList<>(assignedUsers.stream().map(uid -> uid.id().toString()).toList());
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
        if (assignedUsers.contains(userId.id().toString())) {
            // a business decision to ignore this for now instead of throwing an exception
            return;
        }
        assignedUsers.add(userId.id().toString());
    }

    public void changeStatus(TaskStatus newStatus) {
        taskStatus = newStatus;
    }

    public void changeTitle(String newTitle) {
        title = newTitle;
    }

    public void changeDescription(String newDescription) {
        description = newDescription;
    }

    public void changeDeadline(LocalDateTime newDeadline) {
        deadline = newDeadline;
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
        return new ArrayList<>(assignedUsers.stream().map(uid -> new UserId(UUID.fromString(uid))).toList());
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
