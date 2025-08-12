package com.example.taskmanager.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void should_create_New_new_user() {
        // given
        final var id = UserId.generate();
        final var firstName = "Alice";
        final var lastName = "Smith";
        final var email = "as@email.com";

        // when
        final var alice = User.create(id, firstName, lastName, email);

        // then
        assertEquals(id, alice.getId());
        assertEquals(firstName, alice.getFirstName());
        assertEquals(lastName, alice.getLastName());
        assertEquals(email, alice.getEmail());
    }

    @Test
    void should_fail_to_generate_user_with_empty_firstname() {
        // given
        final var id = UserId.generate();
        final var firstName = "";
        final var lastName = "Smith";
        final var email = "as@email.com";

        // then
        assertThrows(IllegalArgumentException.class, () -> User.create(id, firstName, lastName, email));
    }

    @Test
    void should_fail_to_generate_user_with_empty_lastname() {
        // given
        final var id = UserId.generate();
        final var firstName = "Alice";
        final var lastName = "";
        final var email = "as@email.com";

        // then
        assertThrows(IllegalArgumentException.class, () -> User.create(id, firstName, lastName, email));
    }

    @Test
    void should_fail_to_generate_user_with_empty_getEmail() {
        // given
        final var id = UserId.generate();
        final var firstName = "Alice";
        final var lastName = "Smith";
        final String email = "";

        // then
        assertThrows(IllegalArgumentException.class, () -> User.create(id, firstName, lastName, email));
    }

    @Test
    void should_fail_to_generate_user_with_empty_getId() {
        // given
        final UserId id = null;
        final var firstName = "Alice";
        final var lastName = "Smith";
        final String email = "as@acme.com";

        // then
        assertThrows(IllegalArgumentException.class, () -> User.create(id, firstName, lastName, email));
    }

}