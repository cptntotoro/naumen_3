package ru.anastasia.NauJava.dao;

import java.util.List;

/**
 * CRUD интерфейс репозитория
 *
 * @param <T> Сущность
 * @param <ID> Идентификатор
 */
public interface CrudRepository<T, ID> {

    /**
     * Добавить сущность
     *
     * @param entity Сущность
     */
    void create(T entity);

    /**
     * Получить сущность по идентификатору
     *
     * @param id Идентификатор
     * @return Сущность
     */
    T read(ID id);

    /**
     * Обновить сущность
     *
     * @param entity Сущность
     */
    void update(T entity);

    /**
     * Удалить сущность по идентификатору
     *
     * @param id Идентификатор
     */
    void delete(ID id);
}
