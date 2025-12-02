package ru.anastasia.NauJava.service.facade;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.entity.enums.EventType;
import ru.anastasia.NauJava.entity.event.Event;
import ru.anastasia.NauJava.service.contact.ContactService;
import ru.anastasia.NauJava.service.event.EventService;
import ru.anastasia.NauJava.service.facade.dto.ContactWithBirthday;
import ru.anastasia.NauJava.service.facade.dto.ContactWithEvents;
import ru.anastasia.NauJava.service.facade.impl.ContactEventViewServiceImpl;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ContactEventViewServiceTest {

    @Mock
    private ContactService contactService;

    @Mock
    private EventService eventService;

    @InjectMocks
    private ContactEventViewServiceImpl contactEventFacadeService;

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
}
