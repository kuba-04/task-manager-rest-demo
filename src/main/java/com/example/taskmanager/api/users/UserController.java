package com.example.taskmanager.api.users;

import com.example.taskmanager.domain.User;
import com.example.taskmanager.domain.UserId;
import com.example.taskmanager.service.DomainObjectValidationException;
import com.example.taskmanager.service.UserSearchParams;
import com.example.taskmanager.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.UUID;
import java.util.function.Function;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/{userId}")
    public UserResponse findUser(@PathVariable String userId) {
        UUID uuid;
        try {
            uuid = UUID.fromString(userId);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return service.findUserById(new UserId(uuid))
                .map(toResponse())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public Page<UserResponse> findUsers(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            Pageable pageable) {

        final var params = new UserSearchParams(
            firstName, lastName, email
        );

        return service.findUsers(params, pageable).map(toResponse());
    }

    private static Function<User, UserResponse> toResponse() {
        return user -> new UserResponse(
                user.getId().id().toString(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail());
    }

    @PostMapping
    public ResponseEntity<UserResponse> addUser(@RequestBody AddUserRequest request) {
        final var userId = UserId.generate();
        try {
            service.addUser(User.create(userId, request.firstName(), request.lastName(), request.email()));
        } catch (DomainObjectValidationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.created(URI.create("/api/users/" + userId.id().toString())).build();
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable String userId) {
        try {
            final var id = UUID.fromString(userId);
            service.deleteUser(new UserId(id));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}