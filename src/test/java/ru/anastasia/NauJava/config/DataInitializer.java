package ru.anastasia.NauJava.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.anastasia.NauJava.entity.user.User;
import ru.anastasia.NauJava.repository.user.UserRepository;
import ru.anastasia.NauJava.service.user.UserService;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @EventListener(ApplicationReadyEvent.class)
    public void initData() {
        // Создание тестового пользователя
        boolean testUserExists = userRepository.findByUsername("testuser").isPresent();
        if (!testUserExists) {
            User user = new User();
            user.setUsername("testuser");
            user.setPassword(passwordEncoder.encode("password"));
            user.setFirstName("Test");
            user.setLastName("User");
            user.setIsActive(true);
            userService.createUser(user);
        }


        // Создание администратора
        boolean adminExists = userRepository.findByUsername("admin").isPresent();
        if (!adminExists) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setIsActive(true);
            userService.createUser(admin);
        }
    }
}