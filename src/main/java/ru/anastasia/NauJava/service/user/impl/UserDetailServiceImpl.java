package ru.anastasia.NauJava.service.user.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ru.anastasia.NauJava.entity.user.User;
import ru.anastasia.NauJava.service.user.UserService;

import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

    /**
     * Сервис пользователей
     */
    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Загрузка пользователя для аутентификации: '{}'", username);

        long startTime = System.currentTimeMillis();

        try {
            User user = userService.findByUsername(username);
            log.debug("Пользователь найден: ID: {}, активен: {}",
                    user.getId(), user.getIsActive());

            // Собираем роли для отладки
            String roles = user.getRoles().stream()
                    .map(role -> "ROLE_" + role.name())
                    .collect(Collectors.joining(", "));

            log.trace("Роли пользователя '{}': {}", username, roles);

            UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .authorities(user.getRoles().stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                            .collect(Collectors.toList()))
                    .disabled(!user.getIsActive())
                    .build();

            long executionTime = System.currentTimeMillis() - startTime;

            if (!user.getIsActive()) {
                log.warn("Попытка аутентификации неактивного пользователя: '{}'. Время выполнения: {} мс",
                        username, executionTime);
            } else {
                log.debug("UserDetails успешно создан для пользователя '{}'. Роли: {}, время выполнения: {} мс",
                        username, roles, executionTime);
            }

            return userDetails;

        } catch (UsernameNotFoundException e) {
            long errorTime = System.currentTimeMillis() - startTime;
            log.warn("Пользователь не найден: '{}'. Время поиска: {} мс", username, errorTime);
            throw e;

        } catch (Exception e) {
            long errorTime = System.currentTimeMillis() - startTime;
            log.error("Ошибка при загрузке пользователя '{}'. Время выполнения: {} мс. Причина: {}",
                    username, errorTime, e.getMessage(), e);
            throw new UsernameNotFoundException("Ошибка при загрузке пользователя: " + username, e);
        }
    }
}