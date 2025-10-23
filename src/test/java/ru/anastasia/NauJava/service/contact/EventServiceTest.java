package ru.anastasia.NauJava.service.contact;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.entity.contact.Event;
import ru.anastasia.NauJava.entity.enums.EventType;
import ru.anastasia.NauJava.repository.contact.ContactRepository;
import ru.anastasia.NauJava.repository.contact.EventRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class EventServiceTest {

    @Autowired
    private EventService eventService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Test
    void testAddBirthday_Success() {
        Contact contact = Contact.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .build();

        contactRepository.save(contact);

        LocalDate birthday = LocalDate.now();
        Event event = eventService.addBirthday(contact.getId(), birthday);

        assertNotNull(event.getId());
        assertEquals(EventType.BIRTHDAY, event.getEventType());
        assertEquals(birthday, event.getEventDate());
        assertEquals(contact.getId(), event.getContact().getId());
        assertTrue(eventRepository.findById(event.getId()).isPresent());
    }

    @Test
    void testAddBirthday_ContactNotFound() {
        Long nonExistentContactId = 999L;
        LocalDate birthday = LocalDate.now();

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                eventService.addBirthday(nonExistentContactId, birthday));

        assertEquals("Не найден контакт с id: " + nonExistentContactId, exception.getMessage());
    }

    @Test
    void testGetUpcomingEvents_Success() {
        Contact contact = Contact.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .build();

        contactRepository.save(contact);

        LocalDate eventDate = LocalDate.now().plusDays(3);

        Event event = Event.builder()
                .contact(contact)
                .eventType(EventType.ANNIVERSARY)
                .eventDate(eventDate)
                .build();

        eventRepository.save(event);

        Map<LocalDate, List<Event>> upcomingEvents = eventService.getUpcomingEvents(5);

        assertFalse(upcomingEvents.isEmpty());
        assertTrue(upcomingEvents.containsKey(eventDate));
        assertEquals(event.getId(), upcomingEvents.get(eventDate).getFirst().getId());
    }

    @Test
    void testGetUpcomingEvents_NoEvents() {
        Map<LocalDate, List<Event>> upcomingEvents = eventService.getUpcomingEvents(5);

        assertTrue(upcomingEvents.isEmpty());
    }

    @Test
    void testGetBirthdaysThisWeek_Success() {
        Contact contact = Contact.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .build();

        contactRepository.save(contact);

        LocalDate birthday = LocalDate.now().plusDays(2);

        Event event = Event.builder()
                .contact(contact)
                .eventType(EventType.BIRTHDAY)
                .eventDate(birthday)
                .build();

        eventRepository.save(event);

        List<Contact> birthdayContacts = eventService.getBirthdaysThisWeek();

        assertFalse(birthdayContacts.isEmpty());
        assertEquals(contact.getId(), birthdayContacts.getFirst().getId());
    }

    @Test
    void testGetBirthdaysThisWeek_NoBirthdays() {
        Contact contact = Contact.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .build();

        contactRepository.save(contact);

        Event event = Event.builder()
                .contact(contact)
                .eventType(EventType.ANNIVERSARY)
                .eventDate(LocalDate.now().plusDays(2))
                .build();

        eventRepository.save(event);

        List<Contact> birthdayContacts = eventService.getBirthdaysThisWeek();

        assertTrue(birthdayContacts.isEmpty());
    }

    @Test
    void testCreateEvent_Success() {
        Contact contact = Contact.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .build();

        contactRepository.save(contact);

        Event event = Event.builder()
                .contact(contact)
                .eventType(EventType.ANNIVERSARY)
                .eventDate(LocalDate.now())
                .notes("Встреча с клиентом" + UUID.randomUUID())
                .build();

        Event savedEvent = eventService.createEvent(event);

        assertNotNull(savedEvent.getId());
        assertEquals(EventType.ANNIVERSARY, savedEvent.getEventType());
        assertEquals(event.getEventDate(), savedEvent.getEventDate());
        assertEquals(event.getNotes(), savedEvent.getNotes());
        assertTrue(eventRepository.findById(savedEvent.getId()).isPresent());
    }

    @Test
    void testFindByEventTypeAndEventDateBetween_Success() {
        Contact contact = Contact.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .build();

        contactRepository.save(contact);

        LocalDate eventDate = LocalDate.now();

        Event event = Event.builder()
                .contact(contact)
                .eventType(EventType.BIRTHDAY)
                .eventDate(eventDate)
                .build();

        eventRepository.save(event);

        LocalDate start = eventDate.minusDays(1);
        LocalDate end = eventDate.plusDays(1);
        List<Event> events = eventService.findByEventTypeAndEventDateBetween(EventType.BIRTHDAY, start, end);

        assertFalse(events.isEmpty());
        assertEquals(eventDate, events.getFirst().getEventDate());
        assertEquals(EventType.BIRTHDAY, events.getFirst().getEventType());
    }

    @Test
    void testFindByEventTypeAndEventDateBetween_NoEvents() {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(7);

        List<Event> events = eventService.findByEventTypeAndEventDateBetween(EventType.BIRTHDAY, start, end);

        assertTrue(events.isEmpty());
    }
}
