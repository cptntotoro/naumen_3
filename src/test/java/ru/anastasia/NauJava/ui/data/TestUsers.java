package ru.anastasia.NauJava.ui.data;

import lombok.Getter;

@Getter
public enum TestUsers {
    STANDARD_USER("testuser", "password", "Test", "User"),
    ADMIN_USER("admin", "admin", "Admin", "User"),
    INVALID_USER("invalid_user", "wrong_password", "Invalid", "User");

    private final String username;
    private final String password;
    private final String firstName;
    private final String lastName;

    TestUsers(String username, String password, String firstName, String lastName) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
