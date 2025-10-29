package ru.anastasia.NauJava.service.event;

import ru.anastasia.NauJava.dto.event.EventCreateDto;
import ru.anastasia.NauJava.entity.enums.EventType;
import ru.anastasia.NauJava.entity.event.Event;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Сервис событий контактов
 */
public interface EventService {

    /**
     * Получить предстоящие события
     *
     * @param daysAhead Количество дней вперед
     * @return Карта событий по датам
     */
    Map<LocalDate, List<Event>> getUpcomingEvents(int daysAhead);

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
     * Получить события по типу и диапазону дат
     *
     * @param type  Тип события
     * @param start Начальная дата
     * @param end   Конечная дата
     * @return Список событий
     */
    List<Event> findByEventTypeAndEventDateBetween(EventType type, LocalDate start, LocalDate end);

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
     * Удаллить событие
     *
     * @param id Идентификатор
     */
    void delete(Long id);
}
