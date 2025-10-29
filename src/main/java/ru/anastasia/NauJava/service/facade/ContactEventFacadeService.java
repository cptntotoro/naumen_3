package ru.anastasia.NauJava.service.facade;

import ru.anastasia.NauJava.dto.event.EventCreateDto;
import ru.anastasia.NauJava.entity.event.Event;
import ru.anastasia.NauJava.service.facade.dto.ContactWithBirthday;
import ru.anastasia.NauJava.service.facade.dto.ContactWithEvents;

import java.util.List;

/**
 * Фасад для операций с контактами и событиями
 */
public interface ContactEventFacadeService {

    /**
     * Получить контакт с его событиями
     *
     * @param contactId Идентификатор контакта
     * @return Контакт с событиями
     */
    ContactWithEvents getContactWithEvents(Long contactId);

    /**
     * Добавить события к контакту
     *
     * @param events    Список DTO создания событий
     * @param contactId Идентификатор контакта
     * @return Список событий
     */
    List<Event> addEventsToContact(Long contactId, List<EventCreateDto> events);

    /**
     * Получить контакт с днем рождения
     *
     * @param contactId Идентификатор контакта
     * @return Контакт с днем рождения
     */
    ContactWithBirthday getContactWithBirthday(Long contactId);

    /**
     * Добавить день рождения к контакту
     *
     * @param contactId       Идентификатор контакта
     * @param birthdayRequest DTO создания события
     * @return Событие
     */
    Event addBirthdayToContact(Long contactId, EventCreateDto birthdayRequest);
}
