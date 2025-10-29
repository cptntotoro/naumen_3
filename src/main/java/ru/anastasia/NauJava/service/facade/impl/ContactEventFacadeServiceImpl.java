package ru.anastasia.NauJava.service.facade.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.anastasia.NauJava.dto.event.EventCreateDto;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.entity.event.Event;
import ru.anastasia.NauJava.exception.event.IllegalEventStateException;
import ru.anastasia.NauJava.service.contact.ContactService;
import ru.anastasia.NauJava.service.event.EventService;
import ru.anastasia.NauJava.service.facade.ContactEventFacadeService;
import ru.anastasia.NauJava.service.facade.dto.ContactWithBirthday;
import ru.anastasia.NauJava.service.facade.dto.ContactWithEvents;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ContactEventFacadeServiceImpl implements ContactEventFacadeService {
    /**
     * Сервис контактов
     */
    private final ContactService contactService;

    /**
     * Сервис управления событиями контактов
     */
    private final EventService eventService;

    @Transactional(readOnly = true)
    @Override
    public ContactWithEvents getContactWithEvents(Long contactId) {
        Contact contact = contactService.findById(contactId);
        List<Event> events = eventService.findByContactId(contactId);
        return new ContactWithEvents(contact, events);
    }

    @Override
    public List<Event> addEventsToContact(Long contactId, List<EventCreateDto> events) {
        contactService.findById(contactId);

        List<Event> createdEvents = new ArrayList<>();
        for (EventCreateDto eventDto : events) {
            Event event = eventService.create(contactId, eventDto);
            createdEvents.add(event);
        }

        return createdEvents;
    }

    @Transactional(readOnly = true)
    @Override
    public ContactWithBirthday getContactWithBirthday(Long contactId) {
        Contact contact = contactService.findById(contactId);
        Event birthday = eventService.findBirthdayByContactId(contactId);
        return new ContactWithBirthday(contact, birthday);
    }

    @Override
    public Event addBirthdayToContact(Long contactId, EventCreateDto birthdayRequest) {
        if (birthdayRequest.getEventType() != ru.anastasia.NauJava.entity.enums.EventType.BIRTHDAY) {
            throw new IllegalEventStateException("Тип события должен быть BIRTHDAY");
        }

        return eventService.create(contactId, birthdayRequest);
    }

}
