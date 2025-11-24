package ru.anastasia.NauJava.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.anastasia.NauJava.entity.user.User;
import ru.anastasia.NauJava.entity.user.UserRole;
import ru.anastasia.NauJava.service.user.UserService;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final UserService userService;

    @EventListener(ApplicationReadyEvent.class)
    public void initData() {

        // Проверяем существование тестового пользователя
        if (!userService.userExists("testuser")) {
            User user = User.builder()
                    .username("testuser")
                    .password("password")
                    .firstName("Test")
                    .lastName("User")
                    .isActive(true)
                    .roles(Collections.singletonList(UserRole.USER))
                    .build();

            userService.registerUser(user);
        }

        // Проверяем существование администратора
        if (!userService.userExists("admin")) {
            User admin = User.builder()
                    .username("admin")
                    .password("admin")
                    .firstName("Admin")
                    .lastName("User")
                    .isActive(true)
                    .roles(Collections.singletonList(UserRole.ADMIN))
                    .build();

            userService.registerUser(admin);
        }
    }
}