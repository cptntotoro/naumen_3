package ru.anastasia.NauJava.service.facade.impl;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.anastasia.NauJava.dto.event.EventCreateDto;
import ru.anastasia.NauJava.dto.event.EventUpdateDto;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.entity.enums.EventType;
import ru.anastasia.NauJava.entity.event.Event;
import ru.anastasia.NauJava.exception.event.IllegalEventStateException;
import ru.anastasia.NauJava.mapper.event.EventMapper;
import ru.anastasia.NauJava.service.contact.ContactService;
import ru.anastasia.NauJava.service.event.EventService;
import ru.anastasia.NauJava.service.facade.ContactEventManagementService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ContactEventManagementServiceImpl implements ContactEventManagementService {

    /**
     * Сервис контактов
     */
    private final ContactService contactService;

    /**
     * Сервис управления событиями контактов
     */
    private final EventService eventService;

    /**
     * Маппер событий
     */
    private final EventMapper eventMapper;

    @Override
    public Event createEventForContact(Long contactId, EventCreateDto eventCreateDto) {
        log.debug("Создание события для контакта ID: {}, тип: {}", contactId, eventCreateDto.getEventType());

        Contact contact = contactService.findById(contactId);

        if (eventCreateDto.getEventType() == EventType.BIRTHDAY && eventService.hasBirthday(contactId)) {
            log.warn("Попытка создания второго дня рождения для контакта ID: {}", contactId);
            throw new IllegalEventStateException("У контакта уже есть день рождения. Можно иметь только одно событие типа 'День рождения'");
        }

        if (eventCreateDto.getEventType() == EventType.CUSTOM &&
                StringUtils.isBlank(eventCreateDto.getCustomEventName())) {
            throw new IllegalEventStateException("Для кастомного события должно быть указано название");
        }

        if (eventCreateDto.getEventType() != EventType.CUSTOM &&
                StringUtils.isNotBlank(eventCreateDto.getCustomEventName())) {
            throw new IllegalEventStateException("Название кастомного события должно быть пустым для стандартных событий");
        }

        Event event = eventMapper.eventCreateDtoToEvent(eventCreateDto);
        event.setContact(contact);

        Event savedEvent = eventService.saveEvent(event);

        log.info("Событие успешно создано. ID: {}, тип: {}, дата: {}",
                savedEvent.getId(), savedEvent.getEventType(), savedEvent.getEventDate());

        return savedEvent;
    }

    @Override
    public Event updateEventForContact(Long contactId, Long eventId, EventUpdateDto eventUpdateDto) {
        log.debug("Обновление события ID: {} для контакта ID: {}", eventId, contactId);

        Contact contact = contactService.findById(contactId);

        Event existingEvent = eventService.findById(eventId);

        if (!existingEvent.getContact().getId().equals(contactId)) {
            log.warn("Событие ID: {} не принадлежит контакту ID: {}", eventId, contactId);
            throw new IllegalEventStateException("Событие не принадлежит указанному контакту");
        }

        if (eventUpdateDto.getEventType() == EventType.BIRTHDAY &&
                existingEvent.getEventType() != EventType.BIRTHDAY) {
            boolean hasOtherBirthday = eventService.hasOtherBirthday(contactId, eventId);
            if (hasOtherBirthday) {
                log.warn("У контакта ID: {} уже есть другой день рождения", contactId);
                throw new IllegalEventStateException("У контакта уже есть день рождения. Можно иметь только одно событие типа 'День рождения'");
            }
        }

        if (eventUpdateDto.getEventType() == EventType.CUSTOM &&
                StringUtils.isBlank(eventUpdateDto.getCustomEventName())) {
            throw new IllegalEventStateException("Для кастомного события должно быть указано название");
        }

        if (eventUpdateDto.getEventType() != EventType.CUSTOM &&
                StringUtils.isNotBlank(eventUpdateDto.getCustomEventName())) {
            throw new IllegalEventStateException("Название кастомного события должно быть пустым для стандартных событий");
        }

        Event event = eventMapper.eventUpdateDtoToEvent(eventUpdateDto);
        event.setId(eventId);
        event.setContact(contact);

        Event updatedEvent = eventService.update(event);

        log.info("Событие успешно обновлено. ID: {}, тип: {}, контакт ID: {}",
                updatedEvent.getId(), updatedEvent.getEventType(), contactId);

        return updatedEvent;
    }

    @Override
    public void deleteEventForContact(Long contactId, Long eventId) {
        log.debug("Удаление события ID: {} для контакта ID: {}", eventId, contactId);

        Event event = eventService.findById(eventId);

        if (!event.getContact().getId().equals(contactId)) {
            log.warn("Событие ID: {} не принадлежит контакту ID: {}", eventId, contactId);
            throw new IllegalEventStateException("Событие не принадлежит указанному контакту");
        }

        eventService.delete(eventId);

        log.info("Событие успешно удалено. ID: {}, контакт ID: {}", eventId, contactId);
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
}