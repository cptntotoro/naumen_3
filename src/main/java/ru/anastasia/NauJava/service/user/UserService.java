package ru.anastasia.NauJava.service.user;

import ru.anastasia.NauJava.entity.user.User;

/**
 * Сервис пользователей
 */
public interface UserService {

    /**
     * Создать пользователя
     *
     * @param user Пользователь
     */
    void createUser(User user);

    /**
     * Найти пользователя по username
     *
     * @param username Имя пользователя
     * @return Пользователь
     */
    User findByUsername(String username);

    /**
     * Проверить существование пользователя по username
     *
     * @param username Имя пользователя
     * @return Да / Нет
     */
    boolean userExists(String username);
}
