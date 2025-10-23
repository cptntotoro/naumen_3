package ru.anastasia.NauJava.repository.contact;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.anastasia.NauJava.entity.contact.Event;
import ru.anastasia.NauJava.entity.enums.EventType;

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
     * Получить события по типу и месяц
     *
     * @param eventType Тип события
     * @param startDate Дата начала (включительно)
     * @param endDate   Дата окончания (включительно)
     * @return Список событий
     */
    List<Event> findByEventTypeAndEventDateBetween(EventType eventType, LocalDate startDate, LocalDate endDate);
}
