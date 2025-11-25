package ru.anastasia.NauJava.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.anastasia.NauJava.entity.user.User;
import ru.anastasia.NauJava.entity.user.UserRole;
import ru.anastasia.NauJava.service.user.UserService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AdminInitializer {

    /**
     * Сервис пользователей
     */
    private final UserService userService;

    @EventListener(ApplicationReadyEvent.class)
    public void initAdmin() {
        String adminUsername = "admin";

        if (userService.userExists(adminUsername)) {
            return;
        }

        User admin = User.builder()
                .username("admin")
                .password("admin")
                .firstName("System")
                .lastName("Administrator")
                .isActive(true)
                .roles(List.of(UserRole.ADMIN))
                .build();

        userService.registerUser(admin);
    }
}