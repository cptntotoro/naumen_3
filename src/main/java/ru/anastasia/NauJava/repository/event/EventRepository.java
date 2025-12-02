package ru.anastasia.NauJava.repository.event;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.anastasia.NauJava.entity.enums.EventType;
import ru.anastasia.NauJava.entity.event.Event;

import java.time.LocalDate;
import java.util.List;

/**
 * Репозиторий событий
 */
@Repository
public interface EventRepository extends CrudRepository<Event, Long> {

    /**
     * Получить события по идентификатору контакта
     *
     * @param contactId Идентификатор контакта
     * @return Список событий
     */
    List<Event> findByContactId(Long contactId);

    /**
     * Получить события по типу
     *
     * @param eventType Тип события
     * @return Список событий
     */
    List<Event> findByEventType(EventType eventType);

    /**
     * Получить события в дипазоне дат
     *
     * @param startDate Дата начала (включительно)
     * @param endDate   Дата окончания (включительно)
     * @return Список событий
     */
    List<Event> findByEventDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Получить события по типу и датам "от" и "до"
     *
     * @param eventType Тип события
     * @param startDate Дата начала (включительно)
     * @param endDate   Дата окончания (включительно)
     * @return Список событий
     */
    List<Event> findByEventTypeAndEventDateBetween(EventType eventType, LocalDate startDate, LocalDate endDate);

    /**
     * Получить число событий по типу и датам "от" и "до"
     *
     * @param eventType Тип события
     * @param startDate Дата начала (включительно)
     * @param endDate   Дата окончания (включительно)
     * @return Количество событий
     */
    Long countByEventTypeAndEventDateBetween(EventType eventType, LocalDate startDate, LocalDate endDate);

    /**
     * Получить события контакта по типу события
     *
     * @param contactId Идентификатор контакта
     * @param eventType Тип события
     * @return Список событий
     */
    List<Event> findByContactIdAndEventType(Long contactId, EventType eventType);

    /**
     * Проверить существование события контакта по типу события
     *
     * @param contactId Идентификатор контакта
     * @param eventType Тип события
     * @return Да / Нет
     */
    boolean existsByContactIdAndEventType(Long contactId, EventType eventType);

    /**
     * Проверить существование события контакта определенного типа у контакта, исключая указанный ID
     *
     * @param contactId       Идентификатор контакта
     * @param eventType       Тип события
     * @param excludedEventId Идентификатор события, которое нужно исключить из проверки
     * @return Да / Нет
     */
    boolean existsByContactIdAndEventTypeAndIdNot(Long contactId, EventType eventType, Long excludedEventId);
}
