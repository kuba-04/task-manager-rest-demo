package com.example.taskmanager.service;

import com.example.taskmanager.db.UserRepository;
import com.example.taskmanager.domain.User;
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

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService service;
    @Mock
    private UserRepository repository;

    @Test
    void should_save_user() {
        // Given
        final var user = User.create(UserId.generate(), "Alice", "Smith", "alice@acme.com");
        final var argumentCaptor = ArgumentCaptor.forClass(User.class);

        when(repository.save(any(User.class))).thenReturn(user);

        // When
        service.addUser(user);

        // Then
        verify(repository).save(argumentCaptor.capture());
        final var capturedArgument = argumentCaptor.getValue();
        assertEquals(user, capturedArgument);
    }

    @Test
    void should_delete_user() {
        // Given
        final var userId = UserId.generate();
        final var argumentCaptor = ArgumentCaptor.forClass(UserId.class);

        // When
        service.deleteUser(userId);

        // Then
        verify(repository).deleteById(argumentCaptor.capture());
        final var capturedArgument = argumentCaptor.getValue();
        assertEquals(userId, capturedArgument);
    }

    @Test
    void should_find_user_by_params() {
        // Given
        final var searchParams = new UserSearchParams("Alice", "Smith", "alice@acme.com");
        final var pageable = PageRequest.of(0, 10);

        when(repository.findBySearchParams(any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        // When
        service.findUsers(searchParams, pageable);

        // Then
        final var argumentsCaptor = ArgumentCaptor.forClass(Object.class);
        verify(repository).findBySearchParams(
                (String) argumentsCaptor.capture(),
                (String) argumentsCaptor.capture(),
                (String) argumentsCaptor.capture(),
                (Pageable) argumentsCaptor.capture()
        );

        final var capturedArgs = argumentsCaptor.getAllValues();
        assertEquals(searchParams.firstName(), capturedArgs.get(0));
        assertEquals(searchParams.lastName(), capturedArgs.get(1));
        assertEquals(searchParams.email(), capturedArgs.get(2));
    }

}