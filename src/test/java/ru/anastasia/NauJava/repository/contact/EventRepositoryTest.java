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
        Contact contact = new Contact();
        contact.setFirstName("Event");
        contact.setLastName("Test");
        Contact savedContact = contactRepository.save(contact);

        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate middleDate = LocalDate.of(2024, 1, 15);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        Event event1 = new Event();
        event1.setContact(savedContact);
        event1.setEventType(EventType.BIRTHDAY);
        event1.setEventDate(startDate);
        eventRepository.save(event1);

        Event event2 = new Event();
        event2.setContact(savedContact);
        event2.setEventType(EventType.ANNIVERSARY);
        event2.setEventDate(middleDate);
        eventRepository.save(event2);

        Event event3 = new Event();
        event3.setContact(savedContact);
        event3.setEventType(EventType.CUSTOM);
        event3.setEventDate(endDate);
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
        Contact contact = new Contact();
        contact.setFirstName("Type");
        contact.setLastName("Test");
        Contact savedContact = contactRepository.save(contact);

        Event birthdayEvent = new Event();
        birthdayEvent.setContact(savedContact);
        birthdayEvent.setEventType(EventType.BIRTHDAY);
        birthdayEvent.setEventDate(LocalDate.now());
        eventRepository.save(birthdayEvent);

        Event anniversaryEvent = new Event();
        anniversaryEvent.setContact(savedContact);
        anniversaryEvent.setEventType(EventType.ANNIVERSARY);
        anniversaryEvent.setEventDate(LocalDate.now());
        eventRepository.save(anniversaryEvent);

        List<Event> birthdayEvents = eventRepository.findByEventType(EventType.BIRTHDAY);

        Assertions.assertNotNull(birthdayEvents);
        birthdayEvents.forEach(event ->
                Assertions.assertEquals(EventType.BIRTHDAY, event.getEventType())
        );
    }
}