package com.example.taskmanager.service;

import com.example.taskmanager.db.UserRepository;
import com.example.taskmanager.domain.User;
import com.example.taskmanager.domain.UserId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public void addUser(User user) {
        repository.save(user);
    }

    public void deleteUser(UserId userId) {
        repository.deleteById(userId);
    }

    public Page<User> findUsers(UserSearchParams userSearchParams, Pageable pageable) {
        return repository.findBySearchParams(
                userSearchParams.firstName(),
                userSearchParams.lastName(),
                userSearchParams.email(),
                pageable
        );
    }

    public Optional<User> findUserById(UserId userId) {
        return repository.findById(userId);
    }
}
