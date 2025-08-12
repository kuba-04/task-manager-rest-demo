package com.example.taskmanager.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void should_create_new_task() {
        // given
        final var id = TaskId.generate();
        final var title = "task_1";
        final var description = "Finish reading the book";
        final var deadline = LocalDateTime.of(2025, 9, 1, 12, 0 ,0);
        final var userId = UserId.generate();

        // when
        final var task = Task.create(id, title, description, deadline, List.of(userId));

        // then
        assertEquals(id, task.getId());
        assertEquals(title, task.getTitle());
        assertEquals(description, task.getDescription());
        assertEquals(deadline, task.getDeadline());
        assertEquals(TaskStatus.New, task.getTaskStatus());
        assertEquals(List.of(userId), task.getAssignedUsers());
    }

    @Test
    void should_create_new_task_with_minimum_values_required() {
        // given
        final var id = TaskId.generate();
        final var title = "task_1";

        // then
        assertDoesNotThrow(() -> Task.create(id, title, null, null, List.of()));
    }

    @Test
    void should_fail_to_create_task_with_empty_id() {
        // given
        final TaskId id = null;

        // then
        assertThrows(IllegalArgumentException.class,
                () -> Task.create(id, "task_1", "desc", null, List.of()));
    }

    @Test
    void should_fail_to_create_task_with_empty_title() {
        // given
        final var title = "";

        // then
        assertThrows(IllegalArgumentException.class,
                () -> Task.create(TaskId.generate(), title, "desc", null, List.of()));
    }

    @Test
    void should_assign_more_users_to_one_task() {
        // given
        final var user1 = UserId.generate();
        final var user2 = UserId.generate();

        final var task = Task.create(TaskId.generate(), "task_1", null, null, List.of(user1));

        // when
        task.assignUser(user2);

        // then
        assertEquals(List.of(user1, user2), task.getAssignedUsers());
    }

    @Test
    void should_assign_users_only_once_to_the_same_task() {
        // given
        final var user = UserId.generate();

        final var task = Task.create(TaskId.generate(), "task_1", null, null, List.of(user));

        // when
        task.assignUser(user);

        // then
        assertEquals(1, task.getAssignedUsers().size());
    }

    @Test
    void should_change_status() {
        // given
        final var task = Task.create(TaskId.generate(), "task_1", null, null, List.of());
        assertEquals(TaskStatus.New, task.getTaskStatus());

        // when
        task.changeStatus(TaskStatus.Active);

        // then
        assertEquals(TaskStatus.Active, task.getTaskStatus());

        // when
        task.changeStatus(TaskStatus.Completed);

        // then
        assertEquals(TaskStatus.Completed, task.getTaskStatus());
    }

}