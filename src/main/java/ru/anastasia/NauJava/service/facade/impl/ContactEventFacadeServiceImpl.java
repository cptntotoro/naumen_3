package ru.anastasia.NauJava.service.facade.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.anastasia.NauJava.dto.event.EventCreateDto;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.entity.enums.EventType;
import ru.anastasia.NauJava.entity.event.Event;
import ru.anastasia.NauJava.exception.event.IllegalEventStateException;
import ru.anastasia.NauJava.service.contact.ContactService;
import ru.anastasia.NauJava.service.event.EventService;
import ru.anastasia.NauJava.service.facade.ContactEventFacadeService;
import ru.anastasia.NauJava.service.facade.dto.ContactWithBirthday;
import ru.anastasia.NauJava.service.facade.dto.ContactWithEvents;

import java.util.ArrayList;
import java.util.List;

@Slf4j
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
        log.debug("Получение контакта с событиями по ID: {}", contactId);

        Contact contact = contactService.findById(contactId);
        List<Event> events = eventService.findByContactId(contactId);

        ContactWithEvents result = new ContactWithEvents(contact, events);

        log.debug("Успешно получен контакт ID: {} с {} событиями",
                contactId, events.size());

        return result;
    }

    @Override
    public List<Event> addEventsToContact(Long contactId, List<EventCreateDto> events) {
        log.info("Добавление {} событий к контакту ID: {}", events.size(), contactId);

        Contact contact = contactService.findById(contactId);
        log.debug("Контакт найден: ID: {}, имя: {}", contactId, contact.getFullName());

        List<Event> createdEvents = new ArrayList<>();
        for (EventCreateDto eventDto : events) {
            log.trace("Создание события типа: {} для контакта ID: {}",
                    eventDto.getEventType(), contactId);

            Event event = eventService.create(contactId, eventDto);
            createdEvents.add(event);

            log.debug("Событие создано: ID: {}, тип: {}, дата: {}",
                    event.getId(), event.getEventType(), event.getEventDate());
        }

        log.info("Успешно добавлено {} событий к контакту ID: {}",
                createdEvents.size(), contactId);

        return createdEvents;
    }

    @Transactional(readOnly = true)
    @Override
    public ContactWithBirthday getContactWithBirthday(Long contactId) {
        log.debug("Получение контакта с информацией о дне рождения по ID: {}", contactId);

        Contact contact = contactService.findById(contactId);
        Event birthday = eventService.findBirthdayByContactId(contactId);

        ContactWithBirthday result = new ContactWithBirthday(contact, birthday);

        if (birthday != null) {
            log.debug("Найден контакт ID: {} с днем рождения. Дата: {}",
                    contactId, birthday.getEventDate());
        } else {
            log.debug("Контакт ID: {} не имеет дня рождения", contactId);
        }

        return result;
    }

    @Override
    public Event addBirthdayToContact(Long contactId, EventCreateDto birthdayRequest) {
        log.info("Добавление дня рождения для контакта ID: {}", contactId);

        if (birthdayRequest.getEventType() != EventType.BIRTHDAY) {
            log.warn("Попытка добавить не Birthday событие для контакта ID: {}. Полученный тип: {}",
                    contactId, birthdayRequest.getEventType());
            throw new IllegalEventStateException("Тип события должен быть BIRTHDAY");
        }

        log.debug("Валидация типа события прошла успешно для контакта ID: {}", contactId);

        Contact contact = contactService.findById(contactId);
        log.debug("Контакт найден: ID: {}, имя: {}", contactId, contact.getFullName());

        Event birthdayEvent = eventService.create(contactId, birthdayRequest);

        log.info("День рождения успешно добавлен для контакта ID: {}. Дата: {}, ID события: {}",
                contactId, birthdayEvent.getEventDate(), birthdayEvent.getId());

        return birthdayEvent;
    }
}