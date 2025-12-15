package ru.anastasia.NauJava.service.event;

import ru.anastasia.NauJava.dto.event.EventCreateDto;
import ru.anastasia.NauJava.entity.event.Event;

import java.util.List;

/**
 * Сервис событий контактов
 */
public interface EventService {

    /**
     * Получить события контакта
     *
     * @param contactId Идентификатор контакта
     * @return Список событий
     */
    List<Event> findByContactId(Long contactId);

    /**
     * Получить предстоящие дни рождения
     *
     * @param daysAhead Число дней
     * @return Список событий
     */
    List<Event> findUpcomingBirthdays(int daysAhead);

    /**
     * Получить день рождения контакта
     *
     * @param contactId Идентификатор контакта
     * @return Событие (день рождения)
     */
    Event findBirthdayByContactId(Long contactId);

    /**
     * Поучить предстоящих число дней рождения от текущей даты и на N дней вперед
     *
     * @param daysAhead Число дней
     * @return Число предстоящих дней рождения
     */
    Long countUpcomingBirthdays(int daysAhead);

    /**
     * Создать событие для контакта
     *
     * @param contactId      Идентификатор контакта
     * @param eventCreateDto DTO создания события
     * @return Событие
     */
    Event create(Long contactId, EventCreateDto eventCreateDto);

    /**
     * Получить событие по идентификатору
     *
     * @param id Идентификатор
     * @return Событие
     */
    Event findById(Long id);

    /**
     * Обновить событие
     *
     * @param event Событие
     * @return Событие
     */
    Event update(Event event);

    /**
     * Удалить событие
     *
     * @param id Идентификатор
     */
    void delete(Long id);

    /**
     * Сохранить событие
     *
     * @param event Событие
     * @return Сохраненное событие
     */
    Event saveEvent(Event event);

    /**
     * Проверить существование другого дня рождения у контакта, исключая событие
     *
     * @param contactId Идентификатор контакта
     * @param eventId   Идентификатор события
     * @return Да / Нет
     */
    boolean hasOtherBirthday(Long contactId, Long eventId);

    /**
     * Проверить существование дня рождения у контакта
     *
     * @param contactId Идентификатор контакта
     * @return Да / Нет
     */
    boolean hasBirthday(Long contactId);
}
