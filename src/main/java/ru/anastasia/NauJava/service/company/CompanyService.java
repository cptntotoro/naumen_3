package ru.anastasia.NauJava.service.company;

import ru.anastasia.NauJava.entity.company.Company;

import java.util.List;

/**
 * Сервис управления компаниями
 */
public interface CompanyService {

    /**
     * Создать компанию
     *
     * @param name Название компании
     * @return Компания
     */
    Company create(String name);

    /**
     * Получить компанию по названию
     *
     * @param name Название
     * @return Компания
     */
    Company findByName(String name);

    /**
     * Получить все компании
     *
     * @return Список компаний
     */
    List<Company> findAll();

    /**
     * Обновить компанию
     *
     * @param id      Идентификатор компании
     * @param name    Новое название компании
     * @param website Новый адрес сайта
     * @return Обновленная компания
     */
    Company update(Long id, String name, String website);

    /**
     * Удалить компанию
     *
     * @param id Идентификатор компании
     */
    void delete(Long id);
}