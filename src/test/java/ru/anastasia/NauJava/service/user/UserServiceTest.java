package ru.anastasia.NauJava.service.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.anastasia.NauJava.entity.user.User;
import ru.anastasia.NauJava.repository.user.UserRepository;
import ru.anastasia.NauJava.service.user.impl.UserServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void findByUsernameSuccess() {
        String username = "мария_сидорова";
        User user = User.builder()
                .id(1L)
                .username(username)
                .firstName("Мария")
                .lastName("Сидорова")
                .build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        User result = userService.findByUsername(username);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("мария_сидорова", result.getUsername());
        assertEquals("Мария", result.getFirstName());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void findByUsernameNotFoundThrowsException() {
        String username = "несуществующий";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.findByUsername(username));
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void userExistsReturnsTrue() {
        String username = "существующий";
        User user = User.builder().username(username).build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        boolean result = userService.userExists(username);

        assertTrue(result);
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void userExistsReturnsFalse() {
        String username = "несуществующий";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        boolean result = userService.userExists(username);

        assertFalse(result);
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void countTotalSuccess() {
        when(userRepository.count()).thenReturn(42L);

        Long result = userService.countTotal();

        assertEquals(42L, result);
        verify(userRepository, times(1)).count();
    }

    @Test
    void countTotalZero() {
        when(userRepository.count()).thenReturn(0L);

        Long result = userService.countTotal();

        assertEquals(0L, result);
        verify(userRepository, times(1)).count();
    }
}
