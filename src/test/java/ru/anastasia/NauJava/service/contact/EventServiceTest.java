package ru.anastasia.NauJava.service.contact;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.anastasia.NauJava.dto.event.EventCreateDto;
import ru.anastasia.NauJava.entity.enums.EventType;
import ru.anastasia.NauJava.entity.event.Event;
import ru.anastasia.NauJava.exception.event.EventNotFoundException;
import ru.anastasia.NauJava.exception.event.IllegalEventStateException;
import ru.anastasia.NauJava.mapper.event.EventMapper;
import ru.anastasia.NauJava.repository.event.EventRepository;
import ru.anastasia.NauJava.service.event.EventServiceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventMapper eventMapper;

    @InjectMocks
    private EventServiceImpl eventService;

    @Test
    void create_ShouldReturnEvent_WhenSuccessful() {
        Long contactId = 1L;
        EventCreateDto request = EventCreateDto.builder()
                .eventType(EventType.ANNIVERSARY)
                .eventDate(LocalDate.now())
                .build();
        Event event = Event.builder()
                .eventType(EventType.ANNIVERSARY)
                .eventDate(LocalDate.now())
                .build();
        Event savedEvent = Event.builder()
                .id(1L)
                .eventType(EventType.ANNIVERSARY)
                .eventDate(LocalDate.now())
                .build();

        when(eventMapper.eventCreateDtoToEvent(request)).thenReturn(event);
        when(eventRepository.save(event)).thenReturn(savedEvent);

        Event result = eventService.create(contactId, request);

        assertNotNull(result.getId());
        assertEquals(savedEvent, result);
        verify(eventMapper).eventCreateDtoToEvent(request);
        verify(eventRepository).save(event);
    }

    @Test
    void create_ShouldThrowIllegalEventStateException_WhenCustomEventWithoutName() {
        Long contactId = 1L;
        EventCreateDto request = EventCreateDto.builder()
                .eventType(EventType.CUSTOM)
                .eventDate(LocalDate.now())
                .build();

        IllegalEventStateException exception = assertThrows(
                IllegalEventStateException.class,
                () -> eventService.create(contactId, request)
        );

        assertTrue(exception.getMessage().contains("Для кастомного события должно быть указано название"));
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void create_ShouldThrowIllegalEventStateException_WhenNonCustomEventWithName() {
        Long contactId = 1L;
        EventCreateDto request = EventCreateDto.builder()
                .eventType(EventType.ANNIVERSARY)
                .customEventName("Custom Name")
                .eventDate(LocalDate.now())
                .build();

        IllegalEventStateException exception = assertThrows(
                IllegalEventStateException.class,
                () -> eventService.create(contactId, request)
        );

        assertTrue(exception.getMessage().contains("Название кастомного события должно быть пустым для стандартных событий"));
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void create_ShouldThrowIllegalEventStateException_WhenBirthdayAlreadyExists() {
        Long contactId = 1L;
        EventCreateDto request = EventCreateDto.builder()
                .eventType(EventType.BIRTHDAY)
                .eventDate(LocalDate.now())
                .build();

        when(eventRepository.existsByContactIdAndEventType(contactId, EventType.BIRTHDAY)).thenReturn(true);

        IllegalEventStateException exception = assertThrows(
                IllegalEventStateException.class,
                () -> eventService.create(contactId, request)
        );

        assertTrue(exception.getMessage().contains("У контакта уже есть день рождения"));
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void findById_ShouldReturnEvent_WhenExists() {
        Long id = 1L;
        Event event = Event.builder().id(id).build();

        when(eventRepository.findById(id)).thenReturn(Optional.of(event));

        Event result = eventService.findById(id);

        assertEquals(event, result);
        verify(eventRepository).findById(id);
    }

    @Test
    void findById_ShouldThrowEventNotFoundException_WhenNotExists() {
        Long id = 999L;

        when(eventRepository.findById(id)).thenReturn(Optional.empty());

        EventNotFoundException exception = assertThrows(
                EventNotFoundException.class,
                () -> eventService.findById(id)
        );

        assertTrue(exception.getMessage().contains("Событие не найдено с id: " + id));
        verify(eventRepository).findById(id);
    }

    @Test
    void update_ShouldReturnUpdatedEvent_WhenExists() {
        Long id = 1L;
        Event existingEvent = Event.builder()
                .id(id)
                .eventType(EventType.ANNIVERSARY)
                .customEventName(null)
                .eventDate(LocalDate.now())
                .notes("Old notes")
                .yearlyRecurrence(false)
                .build();
        Event updateEvent = Event.builder()
                .id(id)
                .eventType(EventType.CUSTOM)
                .customEventName("Custom Event")
                .eventDate(LocalDate.now().plusDays(1))
                .notes("New notes")
                .yearlyRecurrence(true)
                .build();
        Event updatedEvent = Event.builder()
                .id(id)
                .eventType(EventType.CUSTOM)
                .customEventName("Custom Event")
                .eventDate(LocalDate.now().plusDays(1))
                .notes("New notes")
                .yearlyRecurrence(true)
                .build();

        when(eventRepository.findById(id)).thenReturn(Optional.of(existingEvent));
        when(eventRepository.save(existingEvent)).thenReturn(updatedEvent);

        Event result = eventService.update(updateEvent);

        assertEquals(EventType.CUSTOM, result.getEventType());
        assertEquals("Custom Event", result.getCustomEventName());
        assertEquals("New notes", result.getNotes());
        assertTrue(result.getYearlyRecurrence());
        verify(eventRepository).findById(id);
        verify(eventRepository).save(existingEvent);
    }

    @Test
    void update_ShouldThrowEventNotFoundException_WhenNotExists() {
        Event event = Event.builder().id(999L).build();

        when(eventRepository.findById(999L)).thenReturn(Optional.empty());

        EventNotFoundException exception = assertThrows(
                EventNotFoundException.class,
                () -> eventService.update(event)
        );

        assertTrue(exception.getMessage().contains("Событие не найдено с id: " + event.getId()));
        verify(eventRepository).findById(999L);
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void update_ShouldThrowIllegalEventStateException_WhenCustomEventWithoutName() {
        Long id = 1L;
        Event existingEvent = Event.builder().id(id).eventType(EventType.ANNIVERSARY).build();
        Event updateEvent = Event.builder().id(id).eventType(EventType.CUSTOM).customEventName("").build();

        when(eventRepository.findById(id)).thenReturn(Optional.of(existingEvent));

        IllegalEventStateException exception = assertThrows(
                IllegalEventStateException.class,
                () -> eventService.update(updateEvent)
        );

        assertTrue(exception.getMessage().contains("Для кастомного события должно быть указано название"));
        verify(eventRepository).findById(id);
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void delete_ShouldCallRepositoryDelete() {
        Long id = 1L;
        doNothing().when(eventRepository).deleteById(id);

        eventService.delete(id);

        verify(eventRepository).deleteById(id);
    }

    @Test
    void findBirthdayByContactId_ShouldReturnBirthday_WhenExists() {
        Long contactId = 1L;
        Event birthday = Event.builder().id(1L).eventType(EventType.BIRTHDAY).build();
        List<Event> birthdayEvents = List.of(birthday);

        when(eventRepository.findByContactIdAndEventType(contactId, EventType.BIRTHDAY)).thenReturn(birthdayEvents);

        Event result = eventService.findBirthdayByContactId(contactId);

        assertEquals(birthday, result);
        verify(eventRepository).findByContactIdAndEventType(contactId, EventType.BIRTHDAY);
    }

    @Test
    void findBirthdayByContactId_ShouldReturnNull_WhenNotExists() {
        Long contactId = 999L;

        when(eventRepository.findByContactIdAndEventType(contactId, EventType.BIRTHDAY)).thenReturn(List.of());

        Event result = eventService.findBirthdayByContactId(contactId);

        assertNull(result);
        verify(eventRepository).findByContactIdAndEventType(contactId, EventType.BIRTHDAY);
    }

    @Test
    void getUpcomingEvents_ShouldReturnGroupedEvents() {
        int daysAhead = 7;
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(daysAhead);

        Event event1 = Event.builder().id(1L).eventDate(start).build();
        Event event2 = Event.builder().id(2L).eventDate(start.plusDays(1)).build();
        Event event3 = Event.builder().id(3L).eventDate(start.plusDays(1)).build();
        List<Event> events = List.of(event1, event2, event3);

        when(eventRepository.findByEventDateBetween(start, end)).thenReturn(events);

        Map<LocalDate, List<Event>> result = eventService.getUpcomingEvents(daysAhead);

        assertEquals(2, result.size());
        assertEquals(1, result.get(start).size());
        assertEquals(2, result.get(start.plusDays(1)).size());
        verify(eventRepository).findByEventDateBetween(start, end);
    }

    @Test
    void findByContactId_ShouldReturnEvents() {
        Long contactId = 1L;
        Event event1 = Event.builder().id(1L).build();
        Event event2 = Event.builder().id(2L).build();
        List<Event> expectedEvents = List.of(event1, event2);

        when(eventRepository.findByContactId(contactId)).thenReturn(expectedEvents);

        List<Event> result = eventService.findByContactId(contactId);

        assertEquals(expectedEvents, result);
        verify(eventRepository).findByContactId(contactId);
    }

    @Test
    void findUpcomingBirthdays_ShouldReturnBirthdays() {
        int daysAhead = 7;
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(daysAhead);
        Event birthday1 = Event.builder().id(1L).eventType(EventType.BIRTHDAY).build();
        Event birthday2 = Event.builder().id(2L).eventType(EventType.BIRTHDAY).build();
        List<Event> expectedBirthdays = List.of(birthday1, birthday2);

        when(eventRepository.findByEventTypeAndEventDateBetween(EventType.BIRTHDAY, start, end))
                .thenReturn(expectedBirthdays);

        List<Event> result = eventService.findUpcomingBirthdays(daysAhead);

        assertEquals(expectedBirthdays, result);
        verify(eventRepository).findByEventTypeAndEventDateBetween(EventType.BIRTHDAY, start, end);
    }

    @Test
    void findByEventTypeAndEventDateBetween_ShouldReturnEvents() {
        EventType type = EventType.ANNIVERSARY;
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(30);
        Event event1 = Event.builder().id(1L).eventType(type).build();
        Event event2 = Event.builder().id(2L).eventType(type).build();
        List<Event> expectedEvents = List.of(event1, event2);

        when(eventRepository.findByEventTypeAndEventDateBetween(type, start, end)).thenReturn(expectedEvents);

        List<Event> result = eventService.findByEventTypeAndEventDateBetween(type, start, end);

        assertEquals(expectedEvents, result);
        verify(eventRepository).findByEventTypeAndEventDateBetween(type, start, end);
    }

    @Test
    void hasBirthday_ShouldReturnTrue_WhenBirthdayExists() {
        Long contactId = 1L;

        when(eventRepository.existsByContactIdAndEventType(contactId, EventType.BIRTHDAY)).thenReturn(true);

        boolean result = eventService.hasBirthday(contactId);

        assertTrue(result);
        verify(eventRepository).existsByContactIdAndEventType(contactId, EventType.BIRTHDAY);
    }

    @Test
    void hasBirthday_ShouldReturnFalse_WhenBirthdayNotExists() {
        Long contactId = 999L;

        when(eventRepository.existsByContactIdAndEventType(contactId, EventType.BIRTHDAY)).thenReturn(false);

        boolean result = eventService.hasBirthday(contactId);

        assertFalse(result);
        verify(eventRepository).existsByContactIdAndEventType(contactId, EventType.BIRTHDAY);
    }
}