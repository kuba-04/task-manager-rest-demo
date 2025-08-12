package com.example.taskmanager.domain;

import java.util.Objects;
import java.util.UUID;

// note: at this point this class could be a record as no modifications are required.
// But given the project domain, it can be anticipated that at least email should be updatable.
// Additionally, we want to keep consistency in object creation pattern
public class User {
    private final UserId id;
    private final String firstName;
    private final String lastName;
    private final String email;

    private User(UserId id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public static User create(UserId id, String firstName, String lastName, String email) {
        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be empty");
        }
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("User first name cannot be empty");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("User last name cannot be empty");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("User email cannot be empty");
        }
        return new User(id, firstName, lastName, email);
    }

    public UserId getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return Objects.equals(id, user.id) &&
               Objects.equals(firstName, user.firstName) &&
               Objects.equals(lastName, user.lastName) &&
               Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, email);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
