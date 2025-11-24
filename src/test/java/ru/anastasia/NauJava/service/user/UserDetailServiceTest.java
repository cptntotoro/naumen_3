package ru.anastasia.NauJava.service.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.anastasia.NauJava.entity.user.User;
import ru.anastasia.NauJava.entity.user.UserRole;
import ru.anastasia.NauJava.service.user.impl.UserDetailServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserDetailServiceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserDetailServiceImpl userDetailService;

    @Test
    void loadUserByUsernameSuccess() {
        String username = "админ";
        User user = User.builder()
                .username(username)
                .password("зашифрованный_пароль")
                .roles(List.of(UserRole.ADMIN, UserRole.USER))
                .isActive(true)
                .build();

        when(userService.findByUsername(username)).thenReturn(user);

        UserDetails result = userDetailService.loadUserByUsername(username);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals("зашифрованный_пароль", result.getPassword());
        assertTrue(result.isEnabled());
        assertTrue(result.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
        assertTrue(result.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
        verify(userService, times(1)).findByUsername(username);
    }

    @Test
    void loadUserByUsernameWithSingleRole() {
        String username = "пользователь";
        User user = User.builder()
                .username(username)
                .password("пароль")
                .roles(List.of(UserRole.USER))
                .isActive(true)
                .build();

        when(userService.findByUsername(username)).thenReturn(user);

        UserDetails result = userDetailService.loadUserByUsername(username);

        assertNotNull(result);
        assertEquals(1, result.getAuthorities().size());
        assertTrue(result.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
        verify(userService, times(1)).findByUsername(username);
    }

    @Test
    void loadUserByUsernameInactiveUser() {
        String username = "неактивный";
        User user = User.builder()
                .username(username)
                .password("пароль")
                .roles(List.of(UserRole.USER))
                .isActive(false)
                .build();

        when(userService.findByUsername(username)).thenReturn(user);

        UserDetails result = userDetailService.loadUserByUsername(username);

        assertNotNull(result);
        assertFalse(result.isEnabled());
        verify(userService, times(1)).findByUsername(username);
    }

    @Test
    void loadUserByUsernameNotFoundThrowsException() {
        String username = "несуществующий";

        when(userService.findByUsername(username)).thenThrow(new RuntimeException("Пользователь не найден"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userDetailService.loadUserByUsername(username));

        assertTrue(exception.getMessage().contains("Пользователь не найден"));
        verify(userService, times(1)).findByUsername(username);
    }

    @Test
    void loadUserByUsernameWithEmptyRoles() {
        String username = "безролевой";
        User user = User.builder()
                .username(username)
                .password("пароль")
                .roles(List.of())
                .isActive(true)
                .build();

        when(userService.findByUsername(username)).thenReturn(user);

        UserDetails result = userDetailService.loadUserByUsername(username);

        assertNotNull(result);
        assertTrue(result.getAuthorities().isEmpty());
        verify(userService, times(1)).findByUsername(username);
    }
}
