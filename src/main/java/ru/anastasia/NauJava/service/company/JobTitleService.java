package ru.anastasia.NauJava.service.company;

import ru.anastasia.NauJava.entity.company.JobTitle;

import java.util.List;

/**
 * Сервис управления должностями
 */
public interface JobTitleService {

    /**
     * Создать должность
     *
     * @param title Название должности
     * @return Должность
     */
    JobTitle create(String title);

    /**
     * Получить должность по названию
     *
     * @param title Название должности
     * @return Должность
     */
    JobTitle findByName(String title);

    /**
     * Получить все должности
     *
     * @return Список должностей
     */
    List<JobTitle> findAll();

    /**
     * Обновить должность
     *
     * @param id    Идентификатор должности
     * @param title Новое название должности
     * @return Обновленная должность
     */
    JobTitle update(Long id, String title);

    /**
     * Удалить должность
     *
     * @param id Идентификатор должности
     */
    void delete(Long id);
}