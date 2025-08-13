package com.example.taskmanager.api.tasks;

import com.example.taskmanager.domain.Task;
import com.example.taskmanager.domain.TaskId;
import com.example.taskmanager.domain.TaskStatus;
import com.example.taskmanager.domain.UserId;
import com.example.taskmanager.service.TaskNotFoundException;
import com.example.taskmanager.service.TaskSearchParams;
import com.example.taskmanager.service.TaskService;
import com.example.taskmanager.service.UserNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Function;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @GetMapping("/{taskId}")
    public TaskResponse findById(@PathVariable String taskId) {
        UUID uuid;
        try {
             uuid = UUID.fromString(taskId);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return service.findById(new TaskId(uuid))
                .map(toResponse())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public Page<TaskResponse> findTasks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) TaskStatus taskStatus,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime deadlineFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime deadlineTo,
            @RequestParam(required = false) UserId assignedUserId,
            Pageable pageable) {
        
        final var params = new TaskSearchParams(
            title, description, taskStatus,
            deadlineFrom, deadlineTo, assignedUserId
        );

        return service.findTasks(params, pageable).map(toResponse());
    }

    private static Function<Task, TaskResponse> toResponse() {
        return task -> new TaskResponse(
                task.getId().id().toString(),
                task.getTitle(),
                task.getDescription(),
                task.getDeadline(),
                task.getTaskStatus(),
                task.getAssignedUsers().stream().map(userId -> userId.id().toString()).toList()
        );
    }

    @PostMapping
    public ResponseEntity<TaskResponse> addTask(@RequestBody TaskCreationRequest request) {
        final var taskId = TaskId.generate();
        final var userIds = request.users().stream().map(UserId::new).toList();
        final var task = Task.create(taskId, request.title(), request.description(), request.deadline(), userIds);

        try {
            service.addTask(task);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

        return ResponseEntity.created(URI.create("/api/tasks/" + taskId.id().toString())).build();
    }

    @PatchMapping("/{taskId}/status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeTaskStatus(
            @PathVariable String taskId,
            @RequestBody TaskStatusChangeRequest request) {
        try {
            final var id = UUID.fromString(taskId);
            service.changeStatus(new TaskId(id), request.status());
        } catch (TaskNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PatchMapping("/{taskId}/assign")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void assignUsersToTask(
            @PathVariable String taskId,
            @RequestBody UserAssignmentRequest request) {
        try {
            final var userIds = request.userIds().stream()
                    .map(userId -> new UserId(UUID.fromString(userId)))
                    .toList();
            final var id = UUID.fromString(taskId);
            service.assignUsers(new TaskId(id), userIds);
        } catch (TaskNotFoundException | UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void editTask(
            @PathVariable String taskId,
            @RequestBody EditTaskRequest request) {
        try {
            final var id = UUID.fromString(taskId);
            service.editTask(new TaskId(id), request.toDto());
        } catch (TaskNotFoundException | UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable String taskId) {
        try {
            final var id = UUID.fromString(taskId);
            service.deleteTask(new TaskId(id));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}