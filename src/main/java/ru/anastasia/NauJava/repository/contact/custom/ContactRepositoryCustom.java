package ru.anastasia.NauJava.repository.contact.custom;

import ru.anastasia.NauJava.entity.contact.Contact;

import java.util.List;

/**
 * Кастомный репозиторий контактов
 */
public interface ContactRepositoryCustom {

    /**
     * Получить контакты по критериям
     *
     * @param firstName Имя
     * @param lastName  Фамилия
     * @param company   Название компании
     * @param jobTitle  Название должности
     * @return Список контактов
     */
    List<Contact> findContactsByComplexCriteria(String firstName, String lastName, String company, String jobTitle);

    /**
     * Получить контакты с близящимися событиями
     *
     * @param daysAhead Дней до события
     * @return Список контактов
     */
    List<Contact> findContactsWithUpcomingEvents(int daysAhead);

    /**
     * Получить контакты по критериям
     *
     * @param firstName   Имя
     * @param lastName    Фамилия
     * @param displayName Имя для отображения
     * @return Список контактов
     */
    List<Contact> findByFirstNameAndLastNameOrDisplayNameCriteria(String firstName, String lastName, String displayName);
}
