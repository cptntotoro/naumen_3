package ru.anastasia.NauJava.service.user;

import ru.anastasia.NauJava.entity.user.User;

/**
 * Сервис пользователей
 */
public interface UserService {

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

    /**
     * Получить число пользователей
     *
     * @return Число пользователей
     */
    Long countTotal();

    /**
     * Зарегистрировать нового пользователя
     *
     * @param user Пользователь
     * @return Пользователь
     */
    User registerUser(User user);
}
