package ru.anastasia.NauJava.service.facade;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.anastasia.NauJava.dto.event.EventCreateDto;
import ru.anastasia.NauJava.dto.event.EventUpdateDto;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.entity.enums.EventType;
import ru.anastasia.NauJava.entity.event.Event;
import ru.anastasia.NauJava.exception.event.IllegalEventStateException;
import ru.anastasia.NauJava.mapper.event.EventMapper;
import ru.anastasia.NauJava.service.contact.ContactService;
import ru.anastasia.NauJava.service.event.EventService;
import ru.anastasia.NauJava.service.facade.impl.ContactEventManagementServiceImpl;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ContactEventManagementServiceTest {

    @Mock
    private ContactService contactService;

    @Mock
    private EventService eventService;

    @Mock
    private EventMapper eventMapper;

    @InjectMocks
    private ContactEventManagementServiceImpl contactEventManagementService;

    private Contact createTestContact() {
        return Contact.builder()
                .id(1L)
                .firstName("Иван")
                .lastName("Иванов")
                .build();
    }

    private Event createTestEvent() {
        return Event.builder()
                .id(1L)
                .eventType(EventType.BIRTHDAY)
                .eventDate(LocalDate.of(1990, 5, 15))
                .notes("День рождения")
                .yearlyRecurrence(true)
                .contact(createTestContact())
                .build();
    }

    private Event createCustomEvent() {
        return Event.builder()
                .id(2L)
                .eventType(EventType.CUSTOM)
                .customEventName("Встреча выпускников")
                .eventDate(LocalDate.now().plusDays(10))
                .notes("Встреча через 10 дней")
                .yearlyRecurrence(false)
                .contact(createTestContact())
                .build();
    }

    private EventCreateDto createTestEventCreateDto() {
        return EventCreateDto.builder()
                .eventType(EventType.BIRTHDAY)
                .eventDate(LocalDate.of(1990, 5, 15))
                .notes("День рождения")
                .yearlyRecurrence(true)
                .build();
    }

    private EventCreateDto createCustomEventCreateDto() {
        return EventCreateDto.builder()
                .eventType(EventType.CUSTOM)
                .customEventName("Встреча выпускников")
                .eventDate(LocalDate.now().plusDays(10))
                .notes("Встреча через 10 дней")
                .yearlyRecurrence(false)
                .build();
    }

    private EventUpdateDto createTestEventUpdateDto() {
        return EventUpdateDto.builder()
                .eventType(EventType.BIRTHDAY)
                .eventDate(LocalDate.of(1990, 5, 15))
                .notes("Обновленный день рождения")
                .yearlyRecurrence(false)
                .build();
    }

    @Test
    void createEventForContact_WhenValidBirthdayEvent_ShouldReturnSavedEvent() {
        Long contactId = 1L;
        EventCreateDto eventCreateDto = createTestEventCreateDto();
        Contact contact = createTestContact();
        Event event = createTestEvent();
        Event savedEvent = createTestEvent();

        when(contactService.findById(contactId)).thenReturn(contact);
        when(eventService.hasBirthday(contactId)).thenReturn(false);
        when(eventMapper.eventCreateDtoToEvent(eventCreateDto)).thenReturn(event);
        when(eventService.saveEvent(event)).thenReturn(savedEvent);

        Event result = contactEventManagementService.createEventForContact(contactId, eventCreateDto);

        assertNotNull(result);
        assertEquals(savedEvent.getId(), result.getId());
        verify(contactService, times(1)).findById(contactId);
        verify(eventService, times(1)).hasBirthday(contactId);
        verify(eventMapper, times(1)).eventCreateDtoToEvent(eventCreateDto);
        verify(eventService, times(1)).saveEvent(event);
    }

    @Test
    void createEventForContact_WhenDuplicateBirthday_ShouldThrowIllegalEventStateException() {
        Long contactId = 1L;
        EventCreateDto eventCreateDto = createTestEventCreateDto();
        Contact contact = createTestContact();

        when(contactService.findById(contactId)).thenReturn(contact);
        when(eventService.hasBirthday(contactId)).thenReturn(true);

        IllegalEventStateException exception = assertThrows(
                IllegalEventStateException.class,
                () -> contactEventManagementService.createEventForContact(contactId, eventCreateDto)
        );

        assertTrue(exception.getMessage().contains("У контакта уже есть день рождения"));
        verify(contactService, times(1)).findById(contactId);
        verify(eventService, times(1)).hasBirthday(contactId);
        verify(eventMapper, never()).eventCreateDtoToEvent(any());
        verify(eventService, never()).saveEvent(any(Event.class));
    }

    @Test
    void createEventForContact_WhenCustomEventWithoutName_ShouldThrowIllegalEventStateException() {
        Long contactId = 1L;
        EventCreateDto eventCreateDto = EventCreateDto.builder()
                .eventType(EventType.CUSTOM)
                .customEventName("")
                .eventDate(LocalDate.now())
                .build();
        Contact contact = createTestContact();

        when(contactService.findById(contactId)).thenReturn(contact);

        IllegalEventStateException exception = assertThrows(
                IllegalEventStateException.class,
                () -> contactEventManagementService.createEventForContact(contactId, eventCreateDto)
        );

        assertTrue(exception.getMessage().contains("Для кастомного события должно быть указано название"));
        verify(contactService, times(1)).findById(contactId);
        verify(eventMapper, never()).eventCreateDtoToEvent(any());
        verify(eventService, never()).saveEvent(any(Event.class));
    }

    @Test
    void createEventForContact_WhenStandardEventWithCustomName_ShouldThrowIllegalEventStateException() {
        Long contactId = 1L;
        EventCreateDto eventCreateDto = EventCreateDto.builder()
                .eventType(EventType.BIRTHDAY)
                .customEventName("Кастомное название")
                .eventDate(LocalDate.now())
                .build();
        Contact contact = createTestContact();

        when(contactService.findById(contactId)).thenReturn(contact);
        when(eventService.hasBirthday(contactId)).thenReturn(false);

        IllegalEventStateException exception = assertThrows(
                IllegalEventStateException.class,
                () -> contactEventManagementService.createEventForContact(contactId, eventCreateDto)
        );

        assertTrue(exception.getMessage().contains("Название кастомного события должно быть пустым для стандартных событий"));
        verify(contactService, times(1)).findById(contactId);
        verify(eventService, times(1)).hasBirthday(contactId);
        verify(eventMapper, never()).eventCreateDtoToEvent(any());
        verify(eventService, never()).saveEvent(any(Event.class));
    }

    @Test
    void createEventForContact_WhenValidCustomEvent_ShouldReturnSavedEvent() {
        Long contactId = 1L;
        EventCreateDto eventCreateDto = createCustomEventCreateDto();
        Contact contact = createTestContact();
        Event event = createCustomEvent();
        Event savedEvent = createCustomEvent();

        when(contactService.findById(contactId)).thenReturn(contact);
        when(eventMapper.eventCreateDtoToEvent(eventCreateDto)).thenReturn(event);
        when(eventService.saveEvent(event)).thenReturn(savedEvent);

        Event result = contactEventManagementService.createEventForContact(contactId, eventCreateDto);

        assertNotNull(result);
        assertEquals(savedEvent.getId(), result.getId());
        assertEquals(EventType.CUSTOM, result.getEventType());
        verify(contactService, times(1)).findById(contactId);
    }

    @Test
    void updateEventForContact_WhenValidUpdate_ShouldReturnUpdatedEvent() {
        Long contactId = 1L;
        Long eventId = 1L;
        EventUpdateDto eventUpdateDto = createTestEventUpdateDto();
        Contact contact = createTestContact();
        Event existingEvent = createTestEvent();
        Event updatedEvent = createTestEvent();
        updatedEvent.setNotes("Обновленные заметки");

        when(contactService.findById(contactId)).thenReturn(contact);
        when(eventService.findById(eventId)).thenReturn(existingEvent);
        when(eventMapper.eventUpdateDtoToEvent(eventUpdateDto)).thenReturn(updatedEvent);
        when(eventService.update(any(Event.class))).thenReturn(updatedEvent);

        Event result = contactEventManagementService.updateEventForContact(contactId, eventId, eventUpdateDto);

        assertNotNull(result);
        assertEquals(updatedEvent.getNotes(), result.getNotes());
        verify(contactService, times(1)).findById(contactId);
        verify(eventService, times(1)).findById(eventId);
        verify(eventMapper, times(1)).eventUpdateDtoToEvent(eventUpdateDto);
        verify(eventService, times(1)).update(any(Event.class));
    }

    @Test
    void updateEventForContact_WhenEventBelongsToDifferentContact_ShouldThrowIllegalEventStateException() {
        Long contactId = 1L;
        Long eventId = 1L;
        EventUpdateDto eventUpdateDto = createTestEventUpdateDto();
        Contact contact = createTestContact();
        Event existingEvent = createTestEvent();
        existingEvent.getContact().setId(999L);

        when(contactService.findById(contactId)).thenReturn(contact);
        when(eventService.findById(eventId)).thenReturn(existingEvent);

        IllegalEventStateException exception = assertThrows(
                IllegalEventStateException.class,
                () -> contactEventManagementService.updateEventForContact(contactId, eventId, eventUpdateDto)
        );

        assertTrue(exception.getMessage().contains("Событие не принадлежит указанному контакту"));
        verify(contactService, times(1)).findById(contactId);
        verify(eventService, times(1)).findById(eventId);
        verify(eventMapper, never()).eventUpdateDtoToEvent(any());
        verify(eventService, never()).update(any(Event.class));
    }

    @Test
    void updateEventForContact_WhenChangingToBirthdayWithExistingBirthday_ShouldThrowIllegalEventStateException() {
        Long contactId = 1L;
        Long eventId = 1L;
        EventUpdateDto eventUpdateDto = EventUpdateDto.builder()
                .eventType(EventType.BIRTHDAY)
                .eventDate(LocalDate.now())
                .build();
        Contact contact = createTestContact();
        Event existingEvent = createCustomEvent();
        existingEvent.setEventType(EventType.CUSTOM);

        when(contactService.findById(contactId)).thenReturn(contact);
        when(eventService.findById(eventId)).thenReturn(existingEvent);
        when(eventService.hasOtherBirthday(contactId, eventId)).thenReturn(true);

        IllegalEventStateException exception = assertThrows(
                IllegalEventStateException.class,
                () -> contactEventManagementService.updateEventForContact(contactId, eventId, eventUpdateDto)
        );

        assertTrue(exception.getMessage().contains("У контакта уже есть день рождения"));
        verify(contactService, times(1)).findById(contactId);
        verify(eventService, times(1)).findById(eventId);
        verify(eventService, times(1)).hasOtherBirthday(contactId, eventId);
        verify(eventMapper, never()).eventUpdateDtoToEvent(any());
        verify(eventService, never()).update(any(Event.class));
    }

    @Test
    void updateEventForContact_WhenChangingToBirthdayWithoutExistingBirthday_ShouldReturnUpdatedEvent() {
        Long contactId = 1L;
        Long eventId = 1L;
        EventUpdateDto eventUpdateDto = EventUpdateDto.builder()
                .eventType(EventType.BIRTHDAY)
                .eventDate(LocalDate.now())
                .build();
        Contact contact = createTestContact();
        Event existingEvent = createCustomEvent();
        existingEvent.setEventType(EventType.CUSTOM);
        Event updatedEvent = createTestEvent();

        when(contactService.findById(contactId)).thenReturn(contact);
        when(eventService.findById(eventId)).thenReturn(existingEvent);
        when(eventService.hasOtherBirthday(contactId, eventId)).thenReturn(false);
        when(eventMapper.eventUpdateDtoToEvent(eventUpdateDto)).thenReturn(updatedEvent);
        when(eventService.update(any(Event.class))).thenReturn(updatedEvent);

        Event result = contactEventManagementService.updateEventForContact(contactId, eventId, eventUpdateDto);

        assertNotNull(result);
        assertEquals(EventType.BIRTHDAY, result.getEventType());
        verify(contactService, times(1)).findById(contactId);
        verify(eventService, times(1)).findById(eventId);
        verify(eventService, times(1)).hasOtherBirthday(contactId, eventId);
        verify(eventMapper, times(1)).eventUpdateDtoToEvent(eventUpdateDto);
        verify(eventService, times(1)).update(any(Event.class));
    }

    @Test
    void updateEventForContact_WhenCustomEventWithoutName_ShouldThrowIllegalEventStateException() {
        Long contactId = 1L;
        Long eventId = 1L;
        EventUpdateDto eventUpdateDto = EventUpdateDto.builder()
                .eventType(EventType.CUSTOM)
                .customEventName("")
                .eventDate(LocalDate.now())
                .build();
        Contact contact = createTestContact();
        Event existingEvent = createTestEvent();

        when(contactService.findById(contactId)).thenReturn(contact);
        when(eventService.findById(eventId)).thenReturn(existingEvent);

        IllegalEventStateException exception = assertThrows(
                IllegalEventStateException.class,
                () -> contactEventManagementService.updateEventForContact(contactId, eventId, eventUpdateDto)
        );

        assertTrue(exception.getMessage().contains("Для кастомного события должно быть указано название"));
        verify(contactService, times(1)).findById(contactId);
        verify(eventService, times(1)).findById(eventId);
        verify(eventMapper, never()).eventUpdateDtoToEvent(any());
        verify(eventService, never()).update(any(Event.class));
    }

    @Test
    void deleteEventForContact_WhenValidEvent_ShouldDeleteEvent() {
        Long contactId = 1L;
        Long eventId = 1L;
        Event event = createTestEvent();

        when(eventService.findById(eventId)).thenReturn(event);
        doNothing().when(eventService).delete(eventId);

        contactEventManagementService.deleteEventForContact(contactId, eventId);

        verify(eventService, times(1)).findById(eventId);
        verify(eventService, times(1)).delete(eventId);
    }

    @Test
    void deleteEventForContact_WhenEventBelongsToDifferentContact_ShouldThrowIllegalEventStateException() {
        Long contactId = 1L;
        Long eventId = 1L;
        Event event = createTestEvent();
        event.getContact().setId(999L);

        when(eventService.findById(eventId)).thenReturn(event);

        IllegalEventStateException exception = assertThrows(
                IllegalEventStateException.class,
                () -> contactEventManagementService.deleteEventForContact(contactId, eventId)
        );

        assertTrue(exception.getMessage().contains("Событие не принадлежит указанному контакту"));
        verify(eventService, times(1)).findById(eventId);
        verify(eventService, never()).delete(anyLong());
    }

    @Test
    void addEventsToContact_WhenValidEvents_ShouldReturnCreatedEvents() {
        Long contactId = 1L;
        List<EventCreateDto> eventDtos = Arrays.asList(
                createTestEventCreateDto(),
                createCustomEventCreateDto()
        );
        Contact contact = createTestContact();
        Event event1 = createTestEvent();
        Event event2 = createCustomEvent();

        when(contactService.findById(contactId)).thenReturn(contact);
        when(eventService.create(eq(contactId), any(EventCreateDto.class)))
                .thenReturn(event1, event2);

        List<Event> result = contactEventManagementService.addEventsToContact(contactId, eventDtos);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(contactService, times(1)).findById(contactId);
        verify(eventService, times(2)).create(eq(contactId), any(EventCreateDto.class));
    }

    @Test
    void addEventsToContact_WhenEmptyList_ShouldReturnEmptyList() {
        Long contactId = 1L;
        List<EventCreateDto> eventDtos = Collections.emptyList();
        Contact contact = createTestContact();

        when(contactService.findById(contactId)).thenReturn(contact);

        List<Event> result = contactEventManagementService.addEventsToContact(contactId, eventDtos);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(contactService, times(1)).findById(contactId);
        verify(eventService, never()).create(anyLong(), any(EventCreateDto.class));
    }

    @Test
    void addEventsToContact_WhenDuplicateBirthdayInBatch_ShouldThrowException() {
        Long contactId = 1L;
        List<EventCreateDto> eventDtos = Arrays.asList(
                createTestEventCreateDto(),
                createTestEventCreateDto()
        );
        Contact contact = createTestContact();
        Event event1 = createTestEvent();

        when(contactService.findById(contactId)).thenReturn(contact);
        when(eventService.create(eq(contactId), any(EventCreateDto.class)))
                .thenReturn(event1)
                .thenThrow(new IllegalEventStateException("У контакта уже есть день рождения"));

        assertThrows(
                IllegalEventStateException.class,
                () -> contactEventManagementService.addEventsToContact(contactId, eventDtos)
        );

        verify(contactService, times(1)).findById(contactId);
        verify(eventService, times(2)).create(eq(contactId), any(EventCreateDto.class));
    }

    @Test
    void updateEventForContact_WhenKeepingSameBirthdayType_ShouldNotCheckForOtherBirthday() {
        Long contactId = 1L;
        Long eventId = 1L;
        EventUpdateDto eventUpdateDto = EventUpdateDto.builder()
                .eventType(EventType.BIRTHDAY)
                .eventDate(LocalDate.now().plusDays(1))
                .notes("Новая дата дня рождения")
                .build();
        Contact contact = createTestContact();
        Event existingEvent = createTestEvent();
        Event updatedEvent = createTestEvent();
        updatedEvent.setEventDate(LocalDate.now().plusDays(1));

        when(contactService.findById(contactId)).thenReturn(contact);
        when(eventService.findById(eventId)).thenReturn(existingEvent);
        when(eventMapper.eventUpdateDtoToEvent(eventUpdateDto)).thenReturn(updatedEvent);
        when(eventService.update(any(Event.class))).thenReturn(updatedEvent);

        Event result = contactEventManagementService.updateEventForContact(contactId, eventId, eventUpdateDto);

        assertNotNull(result);
        assertEquals(LocalDate.now().plusDays(1), result.getEventDate());
        verify(contactService, times(1)).findById(contactId);
        verify(eventService, times(1)).findById(eventId);
        verify(eventService, never()).hasOtherBirthday(anyLong(), anyLong());
        verify(eventMapper, times(1)).eventUpdateDtoToEvent(eventUpdateDto);
        verify(eventService, times(1)).update(any(Event.class));
    }

    @Test
    void createEventForContact_WhenEventTypeIsNull_ShouldThrowExceptionInSaveEvent() {
        Long contactId = 1L;
        EventCreateDto eventCreateDto = EventCreateDto.builder()
                .eventType(null)
                .eventDate(LocalDate.now())
                .build();
        Contact contact = createTestContact();

        when(contactService.findById(contactId)).thenReturn(contact);

        assertThrows(
                Exception.class,
                () -> contactEventManagementService.createEventForContact(contactId, eventCreateDto)
        );

        verify(contactService, times(1)).findById(contactId);
    }
}
