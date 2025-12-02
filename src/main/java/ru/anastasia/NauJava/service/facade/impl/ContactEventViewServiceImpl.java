package ru.anastasia.NauJava.service.facade.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.entity.event.Event;
import ru.anastasia.NauJava.service.contact.ContactService;
import ru.anastasia.NauJava.service.event.EventService;
import ru.anastasia.NauJava.service.facade.ContactEventViewService;
import ru.anastasia.NauJava.service.facade.dto.ContactWithBirthday;
import ru.anastasia.NauJava.service.facade.dto.ContactWithEvents;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ContactEventViewServiceImpl implements ContactEventViewService {

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
}