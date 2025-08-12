package com.example.taskmanager.service;

import com.example.taskmanager.db.TaskRepository;
import com.example.taskmanager.db.UserRepository;
import com.example.taskmanager.domain.Task;
import com.example.taskmanager.domain.TaskId;
import com.example.taskmanager.domain.TaskStatus;
import com.example.taskmanager.domain.UserId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @InjectMocks
    private TaskService service;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserRepository userRepository;

    @Test
    void should_save_task() throws UserNotFoundException {
        // Given
        final var user = UserId.generate();
        final var task = Task.create(
                TaskId.generate(),
                "Fix all bugs",
                "Just fix them all!",
                LocalDateTime.now().plusDays(7),
                List.of(user)
        );
        final var argumentCaptor = ArgumentCaptor.forClass(Task.class);
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(userRepository.existsById(user)).thenReturn(true);

        // When
        service.addTask(task);

        // Then
        verify(taskRepository).save(argumentCaptor.capture());
        final var capturedArgument = argumentCaptor.getValue();
        assertEquals(task, capturedArgument);
    }

    @Test
    void should_delete_task() {
        // Given
        final var taskId = TaskId.generate();
        final var argumentCaptor = ArgumentCaptor.forClass(TaskId.class);

        // When
        service.deleteTask(taskId);

        // Then
        verify(taskRepository).deleteById(argumentCaptor.capture());
        final var capturedArgument = argumentCaptor.getValue();
        assertEquals(taskId, capturedArgument);
    }

    @Test
    void should_find_task_by_params() {
        // Given
        final var searchParams = new TaskSearchParams(
                TaskId.generate(),
                "Fix bug",
                "Fix login issue",
                TaskStatus.New,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(7),
                UserId.generate()
        );
        final var pageable = PageRequest.of(0, 10);

        when(taskRepository.findBySearchParams(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        // When
        service.findTasks(searchParams, pageable);

        // Then
        final var argumentsCaptor = ArgumentCaptor.forClass(Object.class);
        verify(taskRepository).findBySearchParams(
                (TaskId) argumentsCaptor.capture(),
                (String) argumentsCaptor.capture(),
                (String) argumentsCaptor.capture(),
                (TaskStatus) argumentsCaptor.capture(),
                (LocalDateTime) argumentsCaptor.capture(),
                (LocalDateTime) argumentsCaptor.capture(),
                (UserId) argumentsCaptor.capture(),
                (Pageable) argumentsCaptor.capture()
        );

        final var capturedArgs = argumentsCaptor.getAllValues();
        assertEquals(searchParams.id(), capturedArgs.get(0));
        assertEquals(searchParams.title(), capturedArgs.get(1));
        assertEquals(searchParams.description(), capturedArgs.get(2));
        assertEquals(searchParams.taskStatus(), capturedArgs.get(3));
        assertEquals(searchParams.deadlineFrom(), capturedArgs.get(4));
        assertEquals(searchParams.deadlineTo(), capturedArgs.get(5));
        assertEquals(searchParams.assignedUserId(), capturedArgs.get(6));
    }

    @Test
    void should_update_task_status() throws TaskNotFoundException {
        // Given
        final var taskId = TaskId.generate();
        final var newStatus = TaskStatus.Active;
        final var task = Task.create(taskId, "Task 1", null, null, List.of());

        final var taskIdCaptor = ArgumentCaptor.forClass(TaskId.class);
        final var taskCaptor = ArgumentCaptor.forClass(Task.class);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // When
        service.changeStatus(taskId, newStatus);

        // Then
        verify(taskRepository).findById(taskIdCaptor.capture());
        verify(taskRepository).save(taskCaptor.capture());

        assertEquals(taskId, taskIdCaptor.getValue());
        assertEquals(newStatus, taskCaptor.getValue().getTaskStatus());
    }

    @Test
    void should_assign_user_to_task() throws TaskNotFoundException, UserNotFoundException {
        // Given
        final var taskId = TaskId.generate();
        final var task = Task.create(taskId, "Task 1", null, null, List.of());
        final var userId = UserId.generate();

        final var taskIdCaptor = ArgumentCaptor.forClass(TaskId.class);
        final var taskCaptor = ArgumentCaptor.forClass(Task.class);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.existsById(userId)).thenReturn(true);
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // When
        service.assignUsers(taskId, List.of(userId));

        // Then
        verify(taskRepository).findById(taskIdCaptor.capture());
        verify(taskRepository).save(taskCaptor.capture());

        assertEquals(taskId, taskIdCaptor.getValue());
        assertEquals(List.of(userId), taskCaptor.getValue().getAssignedUsers());
    }

    @Test
    void should_not_assign_non_existing_user_to_task() {
        // Given
        final var taskId = TaskId.generate();
        final var task = Task.create(taskId, "Task 1", null, null, List.of());
        final var userId = UserId.generate();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.existsById(userId)).thenReturn(false);

        // When & then
        assertThrows(UserNotFoundException.class, () -> service.assignUsers(taskId, List.of(userId)));
    }

    @Test
    void should_edit_task() throws TaskNotFoundException, UserNotFoundException {
        // Given
        final var taskId = TaskId.generate();
        final var initialTask = Task.create(taskId, "Task 1", null, null, List.of());
        final var editedTask = Task.create(taskId, "Task #1", "fix bugs", LocalDateTime.now().minusDays(1), List.of());
        final var editTaskDto = new TaskEditDto(
                Optional.of(editedTask.getTitle()),
                Optional.of(editedTask.getDescription()),
                Optional.of(editedTask.getDeadline()),
                editedTask.getAssignedUsers());

        final var taskIdCaptor = ArgumentCaptor.forClass(TaskId.class);
        final var taskCaptor = ArgumentCaptor.forClass(Task.class);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(initialTask));
        when(taskRepository.save(any(Task.class))).thenReturn(initialTask);

        // When
        service.editTask(taskId, editTaskDto);

        // Then
        verify(taskRepository).findById(taskIdCaptor.capture());
        verify(taskRepository).save(taskCaptor.capture());

        assertEquals(taskId, taskIdCaptor.getValue());
        assertEquals(editedTask, taskCaptor.getValue());
    }

    // todo: add more tests for negative scenarios and edge cases
}