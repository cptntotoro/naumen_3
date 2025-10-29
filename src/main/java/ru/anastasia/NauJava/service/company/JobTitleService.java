package ru.anastasia.NauJava.service.company;

import ru.anastasia.NauJava.entity.company.JobTitle;

import java.util.List;

/**
 * Сервис должностей
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
     * @param jobTitle Должность
     * @return Обновленная должность
     */
    JobTitle update(JobTitle jobTitle);

    /**
     * Удалить должность
     *
     * @param id Идентификатор должности
     */
    void delete(Long id);

    /**
     * Получить должность по идентификатору
     *
     * @param id Идентификатор должности
     * @return Должность
     */
    JobTitle findById(Long id);
}