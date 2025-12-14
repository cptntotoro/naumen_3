package ru.anastasia.NauJava.service.company;

import ru.anastasia.NauJava.entity.company.Company;

import java.util.List;

/**
 * Сервис компаний
 */
public interface CompanyService {

    /**
     * Создать компанию
     *
     * @param company Компания
     * @return Компания
     */
    Company create(Company company);

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
     * @param company Компания
     * @return Обновленная компания
     */
    Company update(Company company);

    /**
     * Удалить компанию
     *
     * @param id Идентификатор компании
     */
    void delete(Long id);

    /**
     * Получить компанию по идентификатору
     *
     * @param id Идентификатор компании
     * @return Компания
     */
    Company findById(Long id);

    /**
     * Получить число компаний
     *
     * @return Количество компаний
     */
    Long countTotal();

    /**
     * Получить список компаний по совпадению части названия
     *
     * @param trim Часть названия (поисковый запрос)
     * @return Список компаний
     */
    List<Company> findByNameContaining(String trim);
}