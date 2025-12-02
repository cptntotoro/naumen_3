package ru.anastasia.NauJava.service.facade;

import ru.anastasia.NauJava.dto.event.EventCreateDto;
import ru.anastasia.NauJava.dto.event.EventUpdateDto;
import ru.anastasia.NauJava.entity.event.Event;

import java.util.List;

/**
 * Сервис управления событиями контактов (бизнес-логика)
 */
public interface ContactEventManagementService {

    /**
     * Создать событие для контакта
     *
     * @param contactId      Идентификатор контакта
     * @param eventCreateDto DTO создания события
     * @return Созданное событие
     */
    Event createEventForContact(Long contactId, EventCreateDto eventCreateDto);

    /**
     * Обновить событие для контакта
     *
     * @param contactId      Идентификатор контакта
     * @param eventId        Идентификатор события
     * @param eventUpdateDto DTO обновления события
     * @return Событие
     */
    Event updateEventForContact(Long contactId, Long eventId, EventUpdateDto eventUpdateDto);

    /**
     * Удалить событие для контакта
     *
     * @param contactId Идентификатор контакта
     * @param eventId   Идентификатор события
     */
    void deleteEventForContact(Long contactId, Long eventId);

    /**
     * Добавить события к контакту
     *
     * @param events    Список DTO создания событий
     * @param contactId Идентификатор контакта
     * @return Список событий
     */
    List<Event> addEventsToContact(Long contactId, List<EventCreateDto> events);
}
