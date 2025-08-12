package com.example.taskmanager.api.users;

import com.example.taskmanager.domain.User;
import com.example.taskmanager.domain.UserId;
import com.example.taskmanager.service.TaskSearchParams;
import com.example.taskmanager.service.UserSearchParams;
import com.example.taskmanager.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void should_create_new_user() throws Exception {
        // Given
        AddUserRequest request = new AddUserRequest("Alice", "Smith", "as@acme.com");
        String userJson = objectMapper.writeValueAsString(request);

        // When & Then
        final var responseLocationHeader = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getHeader("Location");
        final var id = responseLocationHeader.replace("/api/users/", "");

        verify(userService).addUser(User.create(new UserId(UUID.fromString(id)), request.firstName(), request.lastName(), request.email()));
    }

    @Test
    void should_delete_user() throws Exception {
        // Given
        final var userId = UserId.generate();

        // When & Then
        mockMvc.perform(delete("/api/users/" + userId.id().toString()))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(userId);
    }

    @Test
    void should_return_400_for_bad_userid_on_delete() throws Exception {
        // Given
        final var userId = "invalid-id";

        // When & Then
        mockMvc.perform(delete("/api/users/" + userId))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_search_users_with_params() throws Exception {
        // Given
        final var userId = UserId.generate();
        final var user = User.create(userId, "Alice", "Smith", "as@acme.com");
        final var page = new PageImpl<>(List.of(user));

        when(userService.findUsers(any(UserSearchParams.class), any(Pageable.class)))
                .thenReturn(page);

        // When & Then
        final var jsonContent = objectMapper.writeValueAsString(
                new PageImpl<>(List.of(new UserResponse(user.getId().id().toString(), user.getFirstName(), user.getLastName(), user.getEmail()))));
        mockMvc.perform(get("/api/users")
                .param("firstName", "Alice")
                .param("lastName", "Smith")
                .param("email", "ac@acme.com")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(content().json(jsonContent));
    }
}