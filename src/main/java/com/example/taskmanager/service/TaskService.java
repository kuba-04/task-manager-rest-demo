package com.example.taskmanager.service;

import com.example.taskmanager.db.TaskRepository;
import com.example.taskmanager.db.UserRepository;
import com.example.taskmanager.domain.Task;
import com.example.taskmanager.domain.TaskId;
import com.example.taskmanager.domain.TaskStatus;
import com.example.taskmanager.domain.UserId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public void addTask(Task task) throws UserNotFoundException {
        validateUsersExist(task.getAssignedUsers());
        taskRepository.save(task);
    }

    public void changeStatus(TaskId taskId, TaskStatus status) throws TaskNotFoundException {
        final var task = taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException(taskId));
        task.changeStatus(status);
        taskRepository.save(task);
    }

    public void assignUsers(TaskId taskId, List<UserId> users) throws TaskNotFoundException, UserNotFoundException {
        final var task = taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException(taskId));
        validateUsersExist(users);
        users.forEach(task::assignUser);
        taskRepository.save(task);
    }

    public void editTask(TaskId taskId, TaskEditDto taskEditDto) throws TaskNotFoundException, UserNotFoundException {
        final var task = taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException(taskId));
        if (taskEditDto.title().isPresent()) {
            task.changeTitle(taskEditDto.title().get());
        }
        if (taskEditDto.description().isPresent()) {
            task.changeDescription(taskEditDto.description().get());
        }
        if (taskEditDto.deadline().isPresent()) {
            task.changeDeadline(taskEditDto.deadline().get());
        }
        validateUsersExist(taskEditDto.users());

        taskRepository.save(task);
    }

    public void deleteTask(TaskId taskId) {
        taskRepository.deleteById(taskId);
    }

    public Page<Task> findTasks(TaskSearchParams searchParams, Pageable pageable) {
        return taskRepository.findBySearchParams(
                searchParams.id(),
                searchParams.title(),
                searchParams.description(),
                searchParams.taskStatus(),
                searchParams.deadlineFrom(),
                searchParams.deadlineTo(),
                searchParams.assignedUserId(),
                pageable
        );
    }

    private void validateUsersExist(List<UserId> userIds) throws UserNotFoundException {
        for (UserId userId : userIds) {
            if (!userRepository.existsById(userId)) {
                throw new UserNotFoundException(userId);
            }
        }
    }
}
