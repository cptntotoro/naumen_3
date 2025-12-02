package ru.anastasia.NauJava.service.facade;

import ru.anastasia.NauJava.service.facade.dto.ContactWithBirthday;
import ru.anastasia.NauJava.service.facade.dto.ContactWithEvents;

/**
 * Сервис представления данных контактов с событиями
 */
public interface ContactEventViewService {

    /**
     * Получить контакт с его событиями
     *
     * @param contactId Идентификатор контакта
     * @return Контакт с событиями
     */
    ContactWithEvents getContactWithEvents(Long contactId);

    /**
     * Получить контакт с днем рождения
     *
     * @param contactId Идентификатор контакта
     * @return Контакт с днем рождения
     */
    ContactWithBirthday getContactWithBirthday(Long contactId);
}
