package ru.anastasia.NauJava.service.contact;

import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.entity.contact.Event;
import ru.anastasia.NauJava.entity.enums.EventType;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Сервис управления событиями контактов
 */
public interface EventService {

    /**
     * Добавить день рождения контакту
     *
     * @param contactId Идентификатор контакта
     * @param birthday  Дата рождения
     * @return Событие
     */
    Event addBirthday(Long contactId, LocalDate birthday);

    /**
     * Получить предстоящие события
     *
     * @param daysAhead Количество дней вперед
     * @return Карта событий по датам
     */
    Map<LocalDate, List<Event>> getUpcomingEvents(int daysAhead);

    /**
     * Получить дни рождения на этой неделе
     *
     * @return Список контактов
     */
    List<Contact> getBirthdaysThisWeek();

    /**
     * Создать событие
     *
     * @param event Событие
     * @return Созданное событие
     */
    Event createEvent(Event event);

    /**
     * Получить события по типу и диапазону дат
     *
     * @param type  Тип события
     * @param start Начальная дата
     * @param end   Конечная дата
     * @return Список событий
     */
    List<Event> findByEventTypeAndEventDateBetween(EventType type, LocalDate start, LocalDate end);
}
