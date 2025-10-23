package ru.anastasia.NauJava.repository.contact;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.entity.contact.Event;
import ru.anastasia.NauJava.entity.enums.EventType;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@Transactional
class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Test
    void testFindByEventDateBetween() {
        Contact contact = Contact.builder()
                .firstName("Event")
                .lastName("Test")
                .build();

        Contact savedContact = contactRepository.save(contact);

        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate middleDate = LocalDate.of(2024, 1, 15);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        Event event1 = Event.builder()
                .contact(savedContact)
                .eventType(EventType.BIRTHDAY)
                .eventDate(startDate)
                .build();

        eventRepository.save(event1);

        Event event2 = Event.builder()
                .contact(savedContact)
                .eventType(EventType.ANNIVERSARY)
                .eventDate(middleDate)
                .build();

        eventRepository.save(event2);

        Event event3 = Event.builder()
                .contact(savedContact)
                .eventType(EventType.CUSTOM)
                .eventDate(endDate)
                .build();

        eventRepository.save(event3);

        LocalDate searchStart = LocalDate.of(2024, 1, 10);
        LocalDate searchEnd = LocalDate.of(2024, 1, 20);
        List<Event> foundEvents = eventRepository.findByEventDateBetween(searchStart, searchEnd);

        Assertions.assertNotNull(foundEvents);
        Assertions.assertEquals(1, foundEvents.size());
        Assertions.assertEquals(middleDate, foundEvents.getFirst().getEventDate());
    }

    @Test
    void testFindByEventType() {
        Contact contact = Contact.builder()
                .firstName("Type")
                .lastName("Test")
                .build();

        Contact savedContact = contactRepository.save(contact);

        Event birthdayEvent = Event.builder()
                .contact(savedContact)
                .eventType(EventType.BIRTHDAY)
                .eventDate(LocalDate.now())
                .build();

        eventRepository.save(birthdayEvent);

        Event anniversaryEvent = Event.builder()
                .contact(savedContact)
                .eventType(EventType.ANNIVERSARY)
                .eventDate(LocalDate.now())
                .build();

        eventRepository.save(anniversaryEvent);

        List<Event> birthdayEvents = eventRepository.findByEventType(EventType.BIRTHDAY);

        Assertions.assertNotNull(birthdayEvents);
        birthdayEvents.forEach(event ->
                Assertions.assertEquals(EventType.BIRTHDAY, event.getEventType())
        );
    }
}