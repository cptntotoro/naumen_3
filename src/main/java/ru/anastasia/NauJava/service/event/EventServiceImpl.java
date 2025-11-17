package ru.anastasia.NauJava.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.anastasia.NauJava.dto.event.EventCreateDto;
import ru.anastasia.NauJava.entity.enums.EventType;
import ru.anastasia.NauJava.entity.event.Event;
import ru.anastasia.NauJava.exception.event.EventNotFoundException;
import ru.anastasia.NauJava.exception.event.IllegalEventStateException;
import ru.anastasia.NauJava.mapper.event.EventMapper;
import ru.anastasia.NauJava.repository.event.EventRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

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
        validateEventRequest(request);
        validateBirthdayCreation(contactId, request);

        Event event = eventMapper.eventCreateDtoToEvent(request);
        return eventRepository.save(event);
    }

    @Override
    public Event findById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Событие не найдено с id: " + id));
    }

    @Override
    public Event update(Event event) {
        Event existing = findById(event.getId());
        validateEvent(event);

        existing.setEventType(event.getEventType());
        existing.setCustomEventName(event.getCustomEventName());
        existing.setEventDate(event.getEventDate());
        existing.setNotes(event.getNotes());
        existing.setYearlyRecurrence(event.getYearlyRecurrence());

        return eventRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        eventRepository.deleteById(id);
    }

    @Override
    public Event findBirthdayByContactId(Long contactId) {
        List<Event> birthdayEvents = eventRepository.findByContactIdAndEventType(contactId, EventType.BIRTHDAY);
        return birthdayEvents.isEmpty() ? null : birthdayEvents.getFirst();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<LocalDate, List<Event>> getUpcomingEvents(int daysAhead) {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(daysAhead);
        List<Event> events = eventRepository.findByEventDateBetween(start, end);
        return events.stream()
                .collect(Collectors.groupingBy(Event::getEventDate));
    }

    @Transactional(readOnly = true)
    @Override
    public List<Event> findByContactId(Long contactId) {
        return eventRepository.findByContactId(contactId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Event> findUpcomingBirthdays(int daysAhead) {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(daysAhead);
        return eventRepository.findByEventTypeAndEventDateBetween(EventType.BIRTHDAY, start, end);
    }

    @Transactional(readOnly = true)
    @Override
    public Long countUpcomingBirthdays(int daysAhead) {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(daysAhead);
        return eventRepository.countByEventTypeAndEventDateBetween(EventType.BIRTHDAY, start, end);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> findByEventTypeAndEventDateBetween(EventType type, LocalDate start, LocalDate end) {
        return eventRepository.findByEventTypeAndEventDateBetween(type, start, end);
    }

    public boolean hasBirthday(Long contactId) {
        return eventRepository.existsByContactIdAndEventType(contactId, EventType.BIRTHDAY);
    }

    private void validateEvent(Event event) {
        if (event.getEventType() == EventType.CUSTOM &&
                (event.getCustomEventName() == null || event.getCustomEventName().trim().isEmpty())) {
            throw new IllegalEventStateException("Для кастомного события должно быть указано название");
        }

        if (event.getEventType() != EventType.CUSTOM &&
                event.getCustomEventName() != null && !event.getCustomEventName().trim().isEmpty()) {
            throw new IllegalEventStateException("Название кастомного события должно быть пустым для стандартных событий");
        }
    }

    private void validateEventRequest(EventCreateDto request) {
        if (request.getEventType() == EventType.CUSTOM &&
                isBlank(request.getCustomEventName())) {
            throw new IllegalEventStateException("Для кастомного события должно быть указано название");
        }

        if (request.getEventType() != EventType.CUSTOM &&
                isNotBlank(request.getCustomEventName())) {
            throw new IllegalEventStateException("Название кастомного события должно быть пустым для стандартных событий");
        }
    }

    private void validateBirthdayCreation(Long contactId, EventCreateDto newEvent) {
        if (newEvent.getEventType() == EventType.BIRTHDAY && hasBirthday(contactId)) {
            throw new IllegalEventStateException("У контакта уже есть день рождения. Можно иметь только одно событие типа 'День рождения'");
        }
    }
}
