package ru.anastasia.NauJava.service.user.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ru.anastasia.NauJava.entity.user.User;
import ru.anastasia.NauJava.service.user.UserService;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserDetailService implements UserDetailsService {

    /**
     * Сервис пользователей
     */
    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findByUsername(username);

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                        .collect(Collectors.toList()))
                .disabled(!user.getIsActive())
                .build();
    }
}
