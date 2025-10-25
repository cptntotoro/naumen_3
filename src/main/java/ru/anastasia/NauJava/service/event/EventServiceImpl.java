package ru.anastasia.NauJava.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.entity.enums.EventType;
import ru.anastasia.NauJava.entity.event.Event;
import ru.anastasia.NauJava.repository.contact.ContactRepository;
import ru.anastasia.NauJava.repository.event.EventRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    /**
     * Репозиторий событий
     */
    private final EventRepository eventRepository;

    /**
     * Репозиторий контактов
     */
    private final ContactRepository contactRepository;

    @Override
    @Transactional
    public Event addBirthday(Long contactId, LocalDate birthday) {
        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new RuntimeException("Не найден контакт с id: " + contactId));
        Event event = Event.builder()
                .eventType(EventType.BIRTHDAY)
                .eventDate(birthday)
                .build();
        event.setContact(contact);
        return eventRepository.save(event);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<LocalDate, List<Event>> getUpcomingEvents(int daysAhead) {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(daysAhead);
        List<Event> events = eventRepository.findByEventDateBetween(start, end);
        return events.stream().collect(Collectors.groupingBy(Event::getEventDate));
    }


    @Override
    @Transactional(readOnly = true)
    public List<Contact> getBirthdaysThisWeek() {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(7);
        List<Event> events = eventRepository.findByEventTypeAndEventDateBetween(EventType.BIRTHDAY, start, end);
        return events.stream().map(Event::getContact).distinct().collect(Collectors.toList());
    }

    @Transactional
    @Override
    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> findByEventTypeAndEventDateBetween(EventType type, LocalDate start, LocalDate end) {
        return eventRepository.findByEventTypeAndEventDateBetween(type, start, end);
    }

    @Override
    public Event findById(Long id) {
        return eventRepository.findById(id).orElse(null);
    }

    @Override
    public Event update(Event event) {
        Long id = event.getId();

        return eventRepository.findById(id)
                .map(ev -> eventRepository.save(event))
                .orElseThrow(() -> new RuntimeException("Не найден контакт с id: " + id));
    }

    @Override
    public void delete(Long id) {
        eventRepository.deleteById(id);
    }
}
