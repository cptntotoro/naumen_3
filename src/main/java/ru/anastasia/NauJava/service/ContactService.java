package ru.anastasia.NauJava.service;

import ru.anastasia.NauJava.entity.Contact;

import java.util.List;

/**
 * Сервис контактов
 */
public interface ContactService {

    /**
     * Добавить контакт
     *
     * @param name Имя
     * @param phone Телефон
     * @param email Адрес электронной почты
     */
    void addContact(String name, String phone, String email);

    /**
     * Найти контакт по идентификатору
     *
     * @param id Идентификатор
     * @return Контакт
     */
    Contact findById(Long id);

    /**
     * Удаить контакт по идентификатору
     *
     * @param id Идентификатор
     */
    void deleteById(Long id);

    /**
     * Обновить контакт
     *
     * @param id Идентификатор
     * @param name Имя
     * @param phone Телефон
     * @param email Адрес электронной почты
     */
    void updateContact(Long id, String name, String phone, String email);

    /**
     * Получить все контакты
     *
     * @return Список контактов
     */
    List<Contact> listAll();

    /**
     * Найти контакты по имени
     *
     * @param name Имя
     * @return Список контактов
     */
    List<Contact> searchByName(String name);
}
