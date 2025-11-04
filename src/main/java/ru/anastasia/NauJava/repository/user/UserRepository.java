package ru.anastasia.NauJava.repository.user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.anastasia.NauJava.entity.user.User;

import java.util.Optional;

/**
 * Репозиторий пользователей
 */
@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    /**
     * Получить пользователя по username
     *
     * @param username Имя пользователя
     * @return Пользователь
     */
    Optional<User> findByUsername(String username);
}
