package ru.anastasia.NauJava.service.facade;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.anastasia.NauJava.dto.event.EventCreateDto;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.entity.enums.EventType;
import ru.anastasia.NauJava.entity.event.Event;
import ru.anastasia.NauJava.exception.event.IllegalEventStateException;
import ru.anastasia.NauJava.service.contact.ContactService;
import ru.anastasia.NauJava.service.event.EventService;
import ru.anastasia.NauJava.service.facade.dto.ContactWithBirthday;
import ru.anastasia.NauJava.service.facade.dto.ContactWithEvents;
import ru.anastasia.NauJava.service.facade.impl.ContactEventFacadeServiceImpl;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ContactEventFacadeServiceTest {

    @Mock
    private ContactService contactService;

    @Mock
    private EventService eventService;

    @InjectMocks
    private ContactEventFacadeServiceImpl contactEventFacadeService;

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
                .eventType(EventType.ANNIVERSARY)
                .eventDate(LocalDate.now().plusDays(5))
                .notes("Тестовая встреча")
                .yearlyRecurrence(false)
                .contact(createTestContact())
                .build();
    }

    private Event createTestBirthday() {
        return Event.builder()
                .id(2L)
                .eventType(EventType.BIRTHDAY)
                .eventDate(LocalDate.of(1990, 5, 15))
                .yearlyRecurrence(true)
                .contact(createTestContact())
                .build();
    }

    private EventCreateDto createTestEventCreateDto() {
        return EventCreateDto.builder()
                .eventType(EventType.ANNIVERSARY)
                .eventDate(LocalDate.now().plusDays(5))
                .notes("Тестовая встреча")
                .yearlyRecurrence(false)
                .build();
    }

    private EventCreateDto createTestBirthdayCreateDto() {
        return EventCreateDto.builder()
                .eventType(EventType.BIRTHDAY)
                .eventDate(LocalDate.of(1990, 5, 15))
                .yearlyRecurrence(true)
                .build();
    }

    @Test
    void getContactWithEvents_WhenValidData_ShouldReturnContactWithEvents() {
        Long contactId = 1L;
        Contact contact = createTestContact();
        List<Event> events = Arrays.asList(createTestEvent(), createTestEvent());

        when(contactService.findById(contactId)).thenReturn(contact);
        when(eventService.findByContactId(contactId)).thenReturn(events);

        ContactWithEvents result = contactEventFacadeService.getContactWithEvents(contactId);

        assertNotNull(result);
        assertEquals(contact, result.getContact());
        assertEquals(events, result.getEvents());
        assertEquals(2, result.getEvents().size());
        verify(contactService, times(1)).findById(contactId);
        verify(eventService, times(1)).findByContactId(contactId);
    }

    @Test
    void getContactWithEvents_WhenContactNotFound_ShouldThrowException() {
        Long contactId = 1L;

        when(contactService.findById(contactId))
                .thenThrow(new ru.anastasia.NauJava.exception.contact.ContactNotFoundException("Контакт не найден"));

        assertThrows(
                ru.anastasia.NauJava.exception.contact.ContactNotFoundException.class,
                () -> contactEventFacadeService.getContactWithEvents(contactId)
        );

        verify(contactService, times(1)).findById(contactId);
        verify(eventService, never()).findByContactId(anyLong());
    }

    @Test
    void addEventsToContact_WhenValidData_ShouldReturnCreatedEvents() {
        Long contactId = 1L;
        Contact contact = createTestContact();
        List<EventCreateDto> eventDtos = Arrays.asList(
                createTestEventCreateDto(),
                createTestEventCreateDto()
        );
        Event event1 = createTestEvent();
        Event event2 = createTestEvent();
        event2.setId(2L);

        when(contactService.findById(contactId)).thenReturn(contact);
        when(eventService.create(eq(contactId), any(EventCreateDto.class)))
                .thenReturn(event1, event2);

        List<Event> result = contactEventFacadeService.addEventsToContact(contactId, eventDtos);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(contactService, times(1)).findById(contactId);
        verify(eventService, times(2)).create(eq(contactId), any(EventCreateDto.class));
    }

    @Test
    void addEventsToContact_WhenEmptyEventsList_ShouldReturnEmptyList() {
        Long contactId = 1L;
        Contact contact = createTestContact();
        List<EventCreateDto> emptyEventDtos = List.of();

        when(contactService.findById(contactId)).thenReturn(contact);

        List<Event> result = contactEventFacadeService.addEventsToContact(contactId, emptyEventDtos);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(contactService, times(1)).findById(contactId);
        verify(eventService, never()).create(anyLong(), any(EventCreateDto.class));
    }

    @Test
    void getContactWithBirthday_WhenContactWithBirthday_ShouldReturnContactWithBirthday() {
        Long contactId = 1L;
        Contact contact = createTestContact();
        Event birthday = createTestBirthday();

        when(contactService.findById(contactId)).thenReturn(contact);
        when(eventService.findBirthdayByContactId(contactId)).thenReturn(birthday);

        ContactWithBirthday result = contactEventFacadeService.getContactWithBirthday(contactId);

        assertNotNull(result);
        assertEquals(contact, result.getContact());
        assertEquals(birthday, result.getBirthday());
        verify(contactService, times(1)).findById(contactId);
        verify(eventService, times(1)).findBirthdayByContactId(contactId);
    }

    @Test
    void getContactWithBirthday_WhenContactWithoutBirthday_ShouldReturnContactWithNullBirthday() {
        Long contactId = 1L;
        Contact contact = createTestContact();

        when(contactService.findById(contactId)).thenReturn(contact);
        when(eventService.findBirthdayByContactId(contactId)).thenReturn(null);

        ContactWithBirthday result = contactEventFacadeService.getContactWithBirthday(contactId);

        assertNotNull(result);
        assertEquals(contact, result.getContact());
        assertNull(result.getBirthday());
        verify(contactService, times(1)).findById(contactId);
        verify(eventService, times(1)).findBirthdayByContactId(contactId);
    }

    @Test
    void addBirthdayToContact_WhenValidBirthday_ShouldReturnCreatedBirthday() {
        Long contactId = 1L;
        EventCreateDto birthdayDto = createTestBirthdayCreateDto();
        Event birthday = createTestBirthday();

        when(eventService.create(contactId, birthdayDto)).thenReturn(birthday);

        Event result = contactEventFacadeService.addBirthdayToContact(contactId, birthdayDto);

        assertNotNull(result);
        assertEquals(EventType.BIRTHDAY, result.getEventType());
        verify(eventService, times(1)).create(contactId, birthdayDto);
    }

    @Test
    void addBirthdayToContact_WhenNotBirthdayEventType_ShouldThrowIllegalEventStateException() {
        Long contactId = 1L;
        EventCreateDto notBirthdayDto = createTestEventCreateDto();

        IllegalEventStateException exception = assertThrows(
                IllegalEventStateException.class,
                () -> contactEventFacadeService.addBirthdayToContact(contactId, notBirthdayDto)
        );

        assertTrue(exception.getMessage().contains("Тип события должен быть BIRTHDAY"));
        verify(eventService, never()).create(anyLong(), any(EventCreateDto.class));
    }

}
