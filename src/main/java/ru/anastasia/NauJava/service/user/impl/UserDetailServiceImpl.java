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

        try {
            User user = userService.findByUsername(username);

            log.debug("Пользователь найден: ID: {}, активен: {}", user.getId(), user.getIsActive());

            if (!user.getIsActive()) {
                log.warn("Попытка аутентификации неактивного пользователя: '{}'", username);
            }

            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .authorities(user.getRoles().stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                            .collect(Collectors.toList()))
                    .disabled(!user.getIsActive())
                    .build();

        } catch (RuntimeException e) {
            log.warn("Пользователь не найден: '{}'", username);
            throw new UsernameNotFoundException("Пользователь не найден: " + username, e);
        }
    }
}