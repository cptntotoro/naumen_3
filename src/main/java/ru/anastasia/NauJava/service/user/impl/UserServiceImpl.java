package ru.anastasia.NauJava.service.user.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.anastasia.NauJava.entity.user.User;
import ru.anastasia.NauJava.entity.user.UserRole;
import ru.anastasia.NauJava.repository.user.UserRepository;
import ru.anastasia.NauJava.service.user.UserService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    /**
     * Репозиторий пользователей
     */
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public User createUser(User user) {
        log.info("Создание нового пользователя: {}", user.getUsername());

        long startTime = System.currentTimeMillis();

        try {
            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                log.warn("Попытка создания пользователя с пустым паролем: {}", user.getUsername());
                throw new IllegalArgumentException("Пароль не может быть пустым");
            }

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            log.debug("Пароль пользователя '{}' успешно захэширован", user.getUsername());

            if (user.getRoles() == null || user.getRoles().isEmpty()) {
                user.setRoles(List.of(UserRole.USER));
                log.debug("Установлена роль по умолчанию: USER для пользователя: {}", user.getUsername());
            } else {
                log.trace("Роли пользователя '{}': {}", user.getUsername(), user.getRoles());
            }

            if (user.getIsActive() == null) {
                user.setIsActive(true);
                log.debug("Установлен статус активности по умолчанию: true для пользователя: {}", user.getUsername());
            }

            User savedUser = userRepository.save(user);
            long executionTime = System.currentTimeMillis() - startTime;

            log.info("Пользователь успешно создан. ID: {}, username: {}, роли: {}, активен: {}, время выполнения: {} мс",
                    savedUser.getId(), savedUser.getUsername(), savedUser.getRoles(),
                    savedUser.getIsActive(), executionTime);

            return savedUser;

        } catch (Exception e) {
            long errorTime = System.currentTimeMillis() - startTime;
            log.error("Ошибка при создании пользователя '{}'. Время выполнения: {} мс. Причина: {}",
                    user.getUsername(), errorTime, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public User findByUsername(String username) {
        log.debug("Поиск пользователя по username: '{}'", username);

        long startTime = System.currentTimeMillis();

        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> {
                        log.warn("Пользователь не найден по username: '{}'", username);
                        return new RuntimeException("Пользователь не найден");
                    });

            long executionTime = System.currentTimeMillis() - startTime;

            log.debug("Пользователь найден: ID: {}, username: {}, активен: {}, роли: {}, время поиска: {} мс",
                    user.getId(), user.getUsername(), user.getIsActive(),
                    user.getRoles(), executionTime);

            return user;

        } catch (Exception e) {
            long errorTime = System.currentTimeMillis() - startTime;
            log.error("Ошибка при поиске пользователя '{}'. Время выполнения: {} мс. Причина: {}",
                    username, errorTime, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public boolean userExists(String username) {
        log.trace("Проверка существования пользователя: '{}'", username);

        long startTime = System.currentTimeMillis();

        try {
            boolean exists = userRepository.findByUsername(username).isPresent();
            long executionTime = System.currentTimeMillis() - startTime;

            log.trace("Проверка существования пользователя '{}': {}, время выполнения: {} мс",
                    username, exists ? "существует" : "не существует", executionTime);

            return exists;

        } catch (Exception e) {
            long errorTime = System.currentTimeMillis() - startTime;
            log.error("Ошибка при проверке существования пользователя '{}'. Время выполнения: {} мс. Причина: {}",
                    username, errorTime, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Long countTotal() {
        log.debug("Подсчет общего количества пользователей в системе");

        long startTime = System.currentTimeMillis();

        try {
            Long count = userRepository.count();
            long executionTime = System.currentTimeMillis() - startTime;

            log.debug("Общее количество пользователей: {}, время выполнения: {} мс",
                    count, executionTime);

            return count;

        } catch (Exception e) {
            long errorTime = System.currentTimeMillis() - startTime;
            log.error("Ошибка при подсчете количества пользователей. Время выполнения: {} мс. Причина: {}",
                    errorTime, e.getMessage(), e);
            throw e;
        }
    }
}