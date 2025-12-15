package ru.anastasia.NauJava.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.anastasia.NauJava.dto.event.EventCreateDto;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.entity.enums.EventType;
import ru.anastasia.NauJava.entity.event.Event;
import ru.anastasia.NauJava.exception.event.EventNotFoundException;
import ru.anastasia.NauJava.exception.event.IllegalEventStateException;
import ru.anastasia.NauJava.mapper.event.EventMapper;
import ru.anastasia.NauJava.repository.event.EventRepository;

import java.time.LocalDate;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {
    /**
     * Репозиторий событий
     */
    private final EventRepository eventRepository;

    /**
     * Маппер событий
     */
    private final EventMapper eventMapper;

    @Override
    public Event create(Long contactId, EventCreateDto request) {
        log.debug("Создание события для контакта ID: {}, тип: {}", contactId, request.getEventType());

        validateEventRequest(request);
        validateBirthdayCreation(contactId, request);

        Event event = eventMapper.eventCreateDtoToEvent(request);

        Contact contact = new Contact();
        contact.setId(contactId);
        event.setContact(contact);

        Event savedEvent = eventRepository.save(event);

        log.info("Событие успешно создано. ID: {}, тип: {}, дата: {}",
                savedEvent.getId(), savedEvent.getEventType(), savedEvent.getEventDate());

        return savedEvent;
    }

    @Override
    public Event findById(Long id) {
        log.debug("Поиск события по ID: {}", id);

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Событие не найдено с ID: {}", id);
                    return new EventNotFoundException("Событие не найдено с id: " + id);
                });

        log.debug("Событие найдено: ID: {}, тип: {}", id, event.getEventType());
        return event;
    }

    @Override
    public Event update(Event event) {
        log.debug("Обновление события ID: {}", event.getId());

        Event existingEvent = findById(event.getId());

        existingEvent.setEventType(event.getEventType());
        existingEvent.setCustomEventName(event.getCustomEventName());
        existingEvent.setEventDate(event.getEventDate());
        existingEvent.setNotes(event.getNotes());
        existingEvent.setYearlyRecurrence(event.getYearlyRecurrence());

        validateEvent(existingEvent);

        Event updatedEvent = eventRepository.save(existingEvent);

        log.info("Событие успешно обновлено. ID: {}, тип: {}",
                updatedEvent.getId(), updatedEvent.getEventType());

        return updatedEvent;
    }

    @Override
    public void delete(Long id) {
        log.debug("Удаление события ID: {}", id);

        if (!eventRepository.existsById(id)) {
            log.warn("Попытка удаления несуществующего события ID: {}", id);
            throw new EventNotFoundException("Событие не найдено с id: " + id);
        }

        eventRepository.deleteById(id);
        log.info("Событие успешно удалено. ID: {}", id);
    }

    @Override
    public Event saveEvent(Event event) {
        log.debug("Сохранение события ID: {}, тип: {}",
                event.getId() != null ? event.getId() : "новое",
                event.getEventType());

        validateEvent(event);

        Event savedEvent = eventRepository.save(event);

        log.debug("Событие сохранено. ID: {}, тип: {}",
                savedEvent.getId(), savedEvent.getEventType());

        return savedEvent;
    }

    @Override
    public boolean hasOtherBirthday(Long contactId, Long excludedEventId) {
        log.trace("Проверка наличия другого дня рождения у контакта ID: {}, исключая событие ID: {}",
                contactId, excludedEventId);

        boolean hasOtherBirthday = eventRepository.existsByContactIdAndEventTypeAndIdNot(
                contactId, EventType.BIRTHDAY, excludedEventId);

        log.trace("Контакта ID: {} {} другой день рождения", contactId, hasOtherBirthday ? "имеет" : "не имеет");

        return hasOtherBirthday;
    }

    @Override
    public Event findBirthdayByContactId(Long contactId) {
        log.debug("Поиск дня рождения для контакта ID: {}", contactId);

        List<Event> birthdayEvents = eventRepository.findByContactIdAndEventType(contactId, EventType.BIRTHDAY);
        Event birthday = birthdayEvents.isEmpty() ? null : birthdayEvents.getFirst();

        if (birthday == null) {
            log.debug("День рождения не найден для контакта ID: {}", contactId);
        } else {
            log.debug("День рождения найден для контакта ID: {}, дата: {}", contactId, birthday.getEventDate());
        }

        return birthday;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Event> findByContactId(Long contactId) {
        log.debug("Поиск всех событий для контакта ID: {}", contactId);

        List<Event> events = eventRepository.findByContactId(contactId);
        log.debug("Найдено {} событий для контакта ID: {}", events.size(), contactId);

        return events;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Event> findUpcomingBirthdays(int daysAhead) {
        log.debug("Поиск предстоящих дней рождения на {} дней вперед", daysAhead);

        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(daysAhead);
        List<Event> birthdays = eventRepository.findByEventTypeAndEventDateBetween(EventType.BIRTHDAY, start, end);

        log.debug("Найдено {} дней рождения на период с {} по {}",
                birthdays.size(), start, end);

        return birthdays;
    }

    @Transactional(readOnly = true)
    @Override
    public Long countUpcomingBirthdays(int daysAhead) {
        log.debug("Подсчет количества предстоящих дней рождения на {} дней вперед", daysAhead);

        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(daysAhead);
        Long count = eventRepository.countByEventTypeAndEventDateBetween(EventType.BIRTHDAY, start, end);

        log.debug("Найдено {} дней рождения на период с {} по {}", count, start, end);

        return count;
    }

    public boolean hasBirthday(Long contactId) {
        log.trace("Проверка наличия дня рождения у контакта ID: {}", contactId);

        boolean hasBirthday = eventRepository.existsByContactIdAndEventType(contactId, EventType.BIRTHDAY);
        log.trace("Контакта ID: {} {} день рождения", contactId, hasBirthday ? "имеет" : "не имеет");

        return hasBirthday;
    }

    private void validateEvent(Event event) {
        log.trace("Валидация события ID: {}", event.getId());

        if (event.getEventType() == null) {
            throw new IllegalEventStateException("Тип события обязателен");
        }

        if (event.getEventDate() == null) {
            throw new IllegalEventStateException("Дата события обязательна");
        }

        if (event.getContact() == null) {
            throw new IllegalEventStateException("Событие должно быть связано с контактом");
        }

        if (event.getEventType() == EventType.CUSTOM &&
                isBlank(event.getCustomEventName())) {
            log.warn("Ошибка валидации: для кастомного события не указано название");
            throw new IllegalEventStateException("Для кастомного события должно быть указано название");
        }

        if (event.getEventType() != EventType.CUSTOM &&
                isNotBlank(event.getCustomEventName())) {
            log.warn("Ошибка валидации: название кастомного события указано для стандартного события типа: {}",
                    event.getEventType());
            throw new IllegalEventStateException("Название кастомного события должно быть пустым для стандартных событий");
        }

        log.trace("Валидация события ID: {} прошла успешно", event.getId());
    }

    private void validateEventRequest(EventCreateDto request) {
        log.trace("Валидация запроса на создание события типа: {}", request.getEventType());

        if (request.getEventType() == EventType.CUSTOM &&
                isBlank(request.getCustomEventName())) {
            log.warn("Ошибка валидации: для кастомного события не указано название");
            throw new IllegalEventStateException("Для кастомного события должно быть указано название");
        }

        if (request.getEventType() != EventType.CUSTOM &&
                isNotBlank(request.getCustomEventName())) {
            log.warn("Ошибка валидации: название кастомного события указано для стандартного события типа: {}",
                    request.getEventType());
            throw new IllegalEventStateException("Название кастомного события должно быть пустым для стандартных событий");
        }

        log.trace("Валидация запроса на создание события прошла успешно");
    }

    private void validateBirthdayCreation(Long contactId, EventCreateDto newEvent) {
        log.trace("Проверка возможности создания дня рождения для контакта ID: {}", contactId);

        if (newEvent.getEventType() == EventType.BIRTHDAY && hasBirthday(contactId)) {
            log.warn("Попытка создания второго дня рождения для контакта ID: {}", contactId);
            throw new IllegalEventStateException("У контакта уже есть день рождения. Можно иметь только одно событие типа 'День рождения'");
        }

        log.trace("Проверка возможности создания дня рождения прошла успешно");
    }
}