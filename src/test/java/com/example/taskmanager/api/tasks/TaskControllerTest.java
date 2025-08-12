package com.example.taskmanager.api.tasks;

import com.example.taskmanager.domain.Task;
import com.example.taskmanager.domain.TaskId;
import com.example.taskmanager.domain.TaskStatus;
import com.example.taskmanager.domain.UserId;
import com.example.taskmanager.service.TaskEditDto;
import com.example.taskmanager.service.TaskSearchParams;
import com.example.taskmanager.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void should_create_new_task() throws Exception {
        // Given
        final var request = new TaskCreationRequest(
                "Fix bug", "Fix issue #1", LocalDateTime.now().plusDays(1), List.of());
        final var taskJson = objectMapper.writeValueAsString(request);

        // When & Then
        final var responseLocationHeader = mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getHeader("Location");
        final var id = responseLocationHeader.replace("/api/tasks/", "");

        verify(taskService).addTask(Task.create(new TaskId(UUID.fromString(id)), request.title(), request.description(), request.deadline(), List.of()));
    }

    @Test
    void should_delete_task() throws Exception {
        // Given
        final var taskId = TaskId.generate();

        // When & Then
        mockMvc.perform(delete("/api/tasks/" + taskId.id().toString()))
                .andExpect(status().isNoContent());

        verify(taskService).deleteTask(taskId);
    }

    @Test
    void should_return_400_for_bad_taskId_on_delete() throws Exception {
        // Given
        final var taskId = "invalid-id";

        // When & Then
        mockMvc.perform(delete("/api/tasks/" + taskId))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_change_task_status() throws Exception {
        // Given
        final var taskId = TaskId.generate();
        final var request = new TaskStatusChangeRequest(TaskStatus.Completed);
        final var requestJson = objectMapper.writeValueAsString(request);

        // When & Then
        mockMvc.perform(patch("/api/tasks/" + taskId.id().toString() + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNoContent());

        verify(taskService).changeStatus(taskId, TaskStatus.Completed);
    }

    @Test
    void should_assign_users_to_task() throws Exception {
        // Given
        final var taskId = TaskId.generate();
        final var userIds = List.of(UserId.generate());
        final var request = new UserAssignmentRequest(userIds.stream().map(id -> id.id().toString()).toList());
        final var requestJson = objectMapper.writeValueAsString(request);

        // When & Then
        mockMvc.perform(patch("/api/tasks/" + taskId.id().toString() + "/assign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNoContent());

        verify(taskService).assignUsers(taskId, userIds);
    }

    @Test
    void should_edit_task() throws Exception {
        // Given
        final var taskId = TaskId.generate();
        final var request = new EditTaskRequest("Updated title", "Updated description", LocalDateTime.now().plusDays(5), List.of());
        final var dto = request.toDto();
        final var taskJson = objectMapper.writeValueAsString(request);

        // When & Then
        mockMvc.perform(put("/api/tasks/" + taskId.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson))
                .andExpect(status().isNoContent());

        verify(taskService).editTask(taskId, dto);
    }

    @Test
    void should_find_tasks() throws Exception {
        // Given
        final var taskId = TaskId.generate();
        final var task = Task.create(taskId, "Test task", "Description",
                LocalDateTime.now(), List.of());
        final var page = new PageImpl<>(List.of(task));

        when(taskService.findTasks(any(TaskSearchParams.class), any(Pageable.class)))
                .thenReturn(page);

        // When & Then
        final var jsonContent = objectMapper.writeValueAsString(
                new PageImpl<>(List.of(new TaskResponse(
                        task.getId().id().toString(),
                        task.getTitle(),
                        task.getDescription(),
                        task.getDeadline(),
                        task.getTaskStatus(),
                        task.getAssignedUsers().stream().map(u -> u.id().toString()).toList()))));
        mockMvc.perform(get("/api/tasks")
                        .param("title", "Test task")
                        .param("taskStatus", TaskStatus.Active.name())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonContent));
    }
}