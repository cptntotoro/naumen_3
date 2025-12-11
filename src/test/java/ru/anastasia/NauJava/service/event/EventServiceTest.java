package ru.anastasia.NauJava.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.anastasia.NauJava.dto.event.EventCreateDto;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.entity.enums.EventType;
import ru.anastasia.NauJava.entity.event.Event;
import ru.anastasia.NauJava.exception.event.EventNotFoundException;
import ru.anastasia.NauJava.exception.event.IllegalEventStateException;
import ru.anastasia.NauJava.mapper.event.EventMapper;
import ru.anastasia.NauJava.repository.event.EventRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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

    @Test
    void create_WhenValidBirthdayEvent_ShouldReturnSavedEvent() {
        Long contactId = 1L;
        EventCreateDto createDto = createTestEventCreateDto();
        Event event = createTestEvent();
        Event savedEvent = createTestEvent();

        when(eventRepository.existsByContactIdAndEventType(contactId, EventType.BIRTHDAY)).thenReturn(false);
        when(eventMapper.eventCreateDtoToEvent(createDto)).thenReturn(event);
        when(eventRepository.save(event)).thenReturn(savedEvent);

        Event result = eventService.create(contactId, createDto);

        assertNotNull(result);
        assertEquals(savedEvent.getId(), result.getId());
        assertEquals(savedEvent.getEventType(), result.getEventType());
        verify(eventRepository, times(1)).existsByContactIdAndEventType(contactId, EventType.BIRTHDAY);
        verify(eventMapper, times(1)).eventCreateDtoToEvent(createDto);
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    void create_WhenValidCustomEvent_ShouldReturnSavedEvent() {
        Long contactId = 1L;
        EventCreateDto createDto = createCustomEventCreateDto();
        Event event = createCustomEvent();
        Event savedEvent = createCustomEvent();

        when(eventMapper.eventCreateDtoToEvent(createDto)).thenReturn(event);
        when(eventRepository.save(event)).thenReturn(savedEvent);

        Event result = eventService.create(contactId, createDto);

        assertNotNull(result);
        assertEquals(savedEvent.getId(), result.getId());
        assertEquals(savedEvent.getEventType(), result.getEventType());
        assertEquals(savedEvent.getCustomEventName(), result.getCustomEventName());
        verify(eventMapper, times(1)).eventCreateDtoToEvent(createDto);
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    void create_WhenDuplicateBirthday_ShouldThrowIllegalEventStateException() {
        Long contactId = 1L;
        EventCreateDto createDto = createTestEventCreateDto();

        when(eventRepository.existsByContactIdAndEventType(contactId, EventType.BIRTHDAY)).thenReturn(true);

        IllegalEventStateException exception = assertThrows(
                IllegalEventStateException.class,
                () -> eventService.create(contactId, createDto)
        );

        assertTrue(exception.getMessage().contains("У контакта уже есть день рождения"));
        verify(eventRepository, times(1)).existsByContactIdAndEventType(contactId, EventType.BIRTHDAY);
        verify(eventMapper, never()).eventCreateDtoToEvent(any());
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void create_WhenCustomEventWithoutName_ShouldThrowIllegalEventStateException() {
        Long contactId = 1L;
        EventCreateDto createDto = EventCreateDto.builder()
                .eventType(EventType.CUSTOM)
                .customEventName(null)
                .eventDate(LocalDate.now())
                .build();

        IllegalEventStateException exception = assertThrows(
                IllegalEventStateException.class,
                () -> eventService.create(contactId, createDto)
        );

        assertTrue(exception.getMessage().contains("Для кастомного события должно быть указано название"));
        verify(eventRepository, never()).existsByContactIdAndEventType(anyLong(), any());
        verify(eventMapper, never()).eventCreateDtoToEvent(any());
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void create_WhenStandardEventWithCustomName_ShouldThrowIllegalEventStateException() {
        Long contactId = 1L;
        EventCreateDto createDto = EventCreateDto.builder()
                .eventType(EventType.BIRTHDAY)
                .customEventName("Кастомное название")
                .eventDate(LocalDate.now())
                .build();

        IllegalEventStateException exception = assertThrows(
                IllegalEventStateException.class,
                () -> eventService.create(contactId, createDto)
        );

        assertTrue(exception.getMessage().contains("Название кастомного события должно быть пустым для стандартных событий"));
        verify(eventRepository, never()).existsByContactIdAndEventType(anyLong(), any());
        verify(eventMapper, never()).eventCreateDtoToEvent(any());
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void findById_WhenEventExists_ShouldReturnEvent() {
        Long eventId = 1L;
        Event testEvent = createTestEvent();

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(testEvent));

        Event result = eventService.findById(eventId);

        assertNotNull(result);
        assertEquals(testEvent.getId(), result.getId());
        assertEquals(testEvent.getEventType(), result.getEventType());
        verify(eventRepository, times(1)).findById(eventId);
    }

    @Test
    void findById_WhenEventNotExists_ShouldThrowEventNotFoundException() {
        Long nonExistentId = 999L;

        when(eventRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        EventNotFoundException exception = assertThrows(
                EventNotFoundException.class,
                () -> eventService.findById(nonExistentId)
        );

        assertTrue(exception.getMessage().contains("Событие не найдено с id: " + nonExistentId));
        verify(eventRepository, times(1)).findById(nonExistentId);
    }

    @Test
    void update_WhenValidEvent_ShouldReturnUpdatedEvent() {
        Event updatedEvent = createTestEvent();
        updatedEvent.setNotes("Обновленные заметки");
        updatedEvent.setYearlyRecurrence(false);

        Event existingEvent = createTestEvent();
        Event savedEvent = createTestEvent();
        savedEvent.setNotes("Обновленные заметки");
        savedEvent.setYearlyRecurrence(false);

        when(eventRepository.findById(updatedEvent.getId())).thenReturn(Optional.of(existingEvent));
        when(eventRepository.save(existingEvent)).thenReturn(savedEvent);

        Event result = eventService.update(updatedEvent);

        assertNotNull(result);
        assertEquals(updatedEvent.getNotes(), result.getNotes());
        assertEquals(updatedEvent.getYearlyRecurrence(), result.getYearlyRecurrence());
        verify(eventRepository, times(1)).findById(updatedEvent.getId());
        verify(eventRepository, times(1)).save(existingEvent);
    }

    @Test
    void delete_WhenValidId_ShouldCallRepositoryDelete() {
        Long eventId = 1L;

        when(eventRepository.existsById(eventId)).thenReturn(true);
        doNothing().when(eventRepository).deleteById(eventId);

        eventService.delete(eventId);

        verify(eventRepository, times(1)).deleteById(eventId);
    }

    @Test
    void findBirthdayByContactId_WhenBirthdayExists_ShouldReturnBirthdayEvent() {
        Long contactId = 1L;
        List<Event> birthdayEvents = Collections.singletonList(createTestEvent());

        when(eventRepository.findByContactIdAndEventType(contactId, EventType.BIRTHDAY)).thenReturn(birthdayEvents);

        Event result = eventService.findBirthdayByContactId(contactId);

        assertNotNull(result);
        assertEquals(EventType.BIRTHDAY, result.getEventType());
        verify(eventRepository, times(1)).findByContactIdAndEventType(contactId, EventType.BIRTHDAY);
    }

    @Test
    void findBirthdayByContactId_WhenNoBirthday_ShouldReturnNull() {
        Long contactId = 1L;

        when(eventRepository.findByContactIdAndEventType(contactId, EventType.BIRTHDAY)).thenReturn(List.of());

        Event result = eventService.findBirthdayByContactId(contactId);

        assertNull(result);
        verify(eventRepository, times(1)).findByContactIdAndEventType(contactId, EventType.BIRTHDAY);
    }

    @Test
    void getUpcomingEvents_WhenEventsExist_ShouldReturnGroupedEvents() {
        int daysAhead = 7;
        LocalDate today = LocalDate.now();
        Event event1 = createTestEvent();
        event1.setEventDate(today.plusDays(1));
        Event event2 = createCustomEvent();
        event2.setEventDate(today.plusDays(1));
        Event event3 = createTestEvent();
        event3.setEventDate(today.plusDays(3));

        List<Event> events = Arrays.asList(event1, event2, event3);

        when(eventRepository.findByEventDateBetween(today, today.plusDays(daysAhead))).thenReturn(events);

        Map<LocalDate, List<Event>> result = eventService.getUpcomingEvents(daysAhead);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.containsKey(today.plusDays(1)));
        assertTrue(result.containsKey(today.plusDays(3)));
        assertEquals(2, result.get(today.plusDays(1)).size());
        verify(eventRepository, times(1)).findByEventDateBetween(today, today.plusDays(daysAhead));
    }

    @Test
    void getUpcomingEvents_WhenNoEvents_ShouldReturnEmptyMap() {
        int daysAhead = 7;

        when(eventRepository.findByEventDateBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of());

        Map<LocalDate, List<Event>> result = eventService.getUpcomingEvents(daysAhead);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(eventRepository, times(1)).findByEventDateBetween(any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    void findByContactId_WhenEventsExist_ShouldReturnEventsList() {
        Long contactId = 1L;
        List<Event> expectedEvents = Arrays.asList(createTestEvent(), createCustomEvent());

        when(eventRepository.findByContactId(contactId)).thenReturn(expectedEvents);

        List<Event> result = eventService.findByContactId(contactId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(eventRepository, times(1)).findByContactId(contactId);
    }

    @Test
    void findByContactId_WhenNoEvents_ShouldReturnEmptyList() {
        Long contactId = 1L;

        when(eventRepository.findByContactId(contactId)).thenReturn(List.of());

        List<Event> result = eventService.findByContactId(contactId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(eventRepository, times(1)).findByContactId(contactId);
    }

    @Test
    void findUpcomingBirthdays_WhenBirthdaysExist_ShouldReturnBirthdaysList() {
        int daysAhead = 30;
        LocalDate today = LocalDate.now();
        List<Event> expectedBirthdays = Arrays.asList(createTestEvent(), createTestEvent());

        when(eventRepository.findByEventTypeAndEventDateBetween(EventType.BIRTHDAY, today, today.plusDays(daysAhead)))
                .thenReturn(expectedBirthdays);

        List<Event> result = eventService.findUpcomingBirthdays(daysAhead);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(event -> event.getEventType() == EventType.BIRTHDAY));
        verify(eventRepository, times(1)).findByEventTypeAndEventDateBetween(EventType.BIRTHDAY, today, today.plusDays(daysAhead));
    }

    @Test
    void countUpcomingBirthdays_WhenBirthdaysExist_ShouldReturnCount() {
        int daysAhead = 30;
        long expectedCount = 5L;
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(daysAhead);

        when(eventRepository.countByEventTypeAndEventDateBetween(EventType.BIRTHDAY, start, end))
                .thenReturn(expectedCount);

        Long result = eventService.countUpcomingBirthdays(daysAhead);

        assertEquals(expectedCount, result);
        verify(eventRepository, times(1)).countByEventTypeAndEventDateBetween(EventType.BIRTHDAY, start, end);
    }

    @Test
    void findByEventTypeAndEventDateBetween_WhenEventsExist_ShouldReturnEventsList() {
        EventType eventType = EventType.ANNIVERSARY;
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(30);
        List<Event> expectedEvents = Arrays.asList(createTestEvent(), createTestEvent());

        when(eventRepository.findByEventTypeAndEventDateBetween(eventType, startDate, endDate))
                .thenReturn(expectedEvents);

        List<Event> result = eventService.findByEventTypeAndEventDateBetween(eventType, startDate, endDate);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(eventRepository, times(1)).findByEventTypeAndEventDateBetween(eventType, startDate, endDate);
    }

    @Test
    void hasBirthday_WhenBirthdayExists_ShouldReturnTrue() {
        Long contactId = 1L;

        when(eventRepository.existsByContactIdAndEventType(contactId, EventType.BIRTHDAY)).thenReturn(true);

        boolean result = eventService.hasBirthday(contactId);

        assertTrue(result);
        verify(eventRepository, times(1)).existsByContactIdAndEventType(contactId, EventType.BIRTHDAY);
    }

    @Test
    void hasBirthday_WhenNoBirthday_ShouldReturnFalse() {
        Long contactId = 1L;

        when(eventRepository.existsByContactIdAndEventType(contactId, EventType.BIRTHDAY)).thenReturn(false);

        boolean result = eventService.hasBirthday(contactId);

        assertFalse(result);
        verify(eventRepository, times(1)).existsByContactIdAndEventType(contactId, EventType.BIRTHDAY);
    }

    @Test
    void saveEvent_WhenValidNewEvent_ShouldReturnSavedEvent() {
        Event newEvent = Event.builder()
                .eventType(EventType.BIRTHDAY)
                .eventDate(LocalDate.now())
                .notes("Тестовое событие")
                .yearlyRecurrence(true)
                .contact(createTestContact())
                .build();

        Event savedEvent = Event.builder()
                .id(1L)
                .eventType(EventType.BIRTHDAY)
                .eventDate(LocalDate.now())
                .notes("Тестовое событие")
                .yearlyRecurrence(true)
                .contact(createTestContact())
                .build();

        when(eventRepository.save(newEvent)).thenReturn(savedEvent);

        Event result = eventService.saveEvent(newEvent);

        assertNotNull(result);
        assertEquals(savedEvent.getId(), result.getId());
        assertEquals(savedEvent.getEventType(), result.getEventType());
        assertEquals(savedEvent.getNotes(), result.getNotes());
        verify(eventRepository, times(1)).save(newEvent);
    }

    @Test
    void saveEvent_WhenValidExistingEvent_ShouldReturnUpdatedEvent() {
        Event existingEvent = createTestEvent();
        existingEvent.setNotes("Обновленные заметки");

        when(eventRepository.save(existingEvent)).thenReturn(existingEvent);

        Event result = eventService.saveEvent(existingEvent);

        assertNotNull(result);
        assertEquals(existingEvent.getId(), result.getId());
        assertEquals("Обновленные заметки", result.getNotes());
        verify(eventRepository, times(1)).save(existingEvent);
    }

    @Test
    void saveEvent_WhenEventTypeIsNull_ShouldThrowIllegalEventStateException() {
        Event invalidEvent = Event.builder()
                .eventType(null)
                .eventDate(LocalDate.now())
                .contact(createTestContact())
                .build();

        IllegalEventStateException exception = assertThrows(
                IllegalEventStateException.class,
                () -> eventService.saveEvent(invalidEvent)
        );

        assertTrue(exception.getMessage().contains("Тип события обязателен"));
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void saveEvent_WhenEventDateIsNull_ShouldThrowIllegalEventStateException() {
        Event invalidEvent = Event.builder()
                .eventType(EventType.BIRTHDAY)
                .eventDate(null)
                .contact(createTestContact())
                .build();

        IllegalEventStateException exception = assertThrows(
                IllegalEventStateException.class,
                () -> eventService.saveEvent(invalidEvent)
        );

        assertTrue(exception.getMessage().contains("Дата события обязательна"));
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void saveEvent_WhenContactIsNull_ShouldThrowIllegalEventStateException() {
        Event invalidEvent = Event.builder()
                .eventType(EventType.BIRTHDAY)
                .eventDate(LocalDate.now())
                .contact(null)
                .build();

        IllegalEventStateException exception = assertThrows(
                IllegalEventStateException.class,
                () -> eventService.saveEvent(invalidEvent)
        );

        assertTrue(exception.getMessage().contains("Событие должно быть связано с контактом"));
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void saveEvent_WhenCustomEventWithoutName_ShouldThrowIllegalEventStateException() {
        Event invalidEvent = Event.builder()
                .eventType(EventType.CUSTOM)
                .customEventName(null)
                .eventDate(LocalDate.now())
                .contact(createTestContact())
                .build();

        IllegalEventStateException exception = assertThrows(
                IllegalEventStateException.class,
                () -> eventService.saveEvent(invalidEvent)
        );

        assertTrue(exception.getMessage().contains("Для кастомного события должно быть указано название"));
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void saveEvent_WhenStandardEventWithCustomName_ShouldThrowIllegalEventStateException() {
        Event invalidEvent = Event.builder()
                .eventType(EventType.BIRTHDAY)
                .customEventName("Кастомное название")
                .eventDate(LocalDate.now())
                .contact(createTestContact())
                .build();

        IllegalEventStateException exception = assertThrows(
                IllegalEventStateException.class,
                () -> eventService.saveEvent(invalidEvent)
        );

        assertTrue(exception.getMessage().contains("Название кастомного события должно быть пустым для стандартных событий"));
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void saveEvent_WhenValidCustomEventWithName_ShouldReturnSavedEvent() {
        Event customEvent = Event.builder()
                .eventType(EventType.CUSTOM)
                .customEventName("Встреча")
                .eventDate(LocalDate.now().plusDays(5))
                .notes("Важная встреча")
                .yearlyRecurrence(false)
                .contact(createTestContact())
                .build();

        Event savedEvent = Event.builder()
                .id(3L)
                .eventType(EventType.CUSTOM)
                .customEventName("Встреча")
                .eventDate(LocalDate.now().plusDays(5))
                .notes("Важная встреча")
                .yearlyRecurrence(false)
                .contact(createTestContact())
                .build();

        when(eventRepository.save(customEvent)).thenReturn(savedEvent);

        Event result = eventService.saveEvent(customEvent);

        assertNotNull(result);
        assertEquals(savedEvent.getId(), result.getId());
        assertEquals("Встреча", result.getCustomEventName());
        verify(eventRepository, times(1)).save(customEvent);
    }

    @Test
    void saveEvent_WhenValidStandardEventWithoutCustomName_ShouldReturnSavedEvent() {
        Event standardEvent = Event.builder()
                .eventType(EventType.ANNIVERSARY)
                .customEventName(null)
                .eventDate(LocalDate.now().plusDays(7))
                .notes("Годовщина")
                .yearlyRecurrence(true)
                .contact(createTestContact())
                .build();

        Event savedEvent = Event.builder()
                .id(4L)
                .eventType(EventType.ANNIVERSARY)
                .customEventName(null)
                .eventDate(LocalDate.now().plusDays(7))
                .notes("Годовщина")
                .yearlyRecurrence(true)
                .contact(createTestContact())
                .build();

        when(eventRepository.save(standardEvent)).thenReturn(savedEvent);

        Event result = eventService.saveEvent(standardEvent);

        assertNotNull(result);
        assertEquals(savedEvent.getId(), result.getId());
        assertEquals(EventType.ANNIVERSARY, result.getEventType());
        assertNull(result.getCustomEventName());
        verify(eventRepository, times(1)).save(standardEvent);
    }

    @Test
    void hasOtherBirthday_WhenOtherBirthdayExists_ShouldReturnTrue() {
        Long contactId = 1L;
        Long excludedEventId = 1L;

        when(eventRepository.existsByContactIdAndEventTypeAndIdNot(contactId, EventType.BIRTHDAY, excludedEventId))
                .thenReturn(true);

        boolean result = eventService.hasOtherBirthday(contactId, excludedEventId);

        assertTrue(result);
        verify(eventRepository, times(1))
                .existsByContactIdAndEventTypeAndIdNot(contactId, EventType.BIRTHDAY, excludedEventId);
    }

    @Test
    void hasOtherBirthday_WhenNoOtherBirthdayExists_ShouldReturnFalse() {
        Long contactId = 1L;
        Long excludedEventId = 1L;

        when(eventRepository.existsByContactIdAndEventTypeAndIdNot(contactId, EventType.BIRTHDAY, excludedEventId))
                .thenReturn(false);

        boolean result = eventService.hasOtherBirthday(contactId, excludedEventId);

        assertFalse(result);
        verify(eventRepository, times(1))
                .existsByContactIdAndEventTypeAndIdNot(contactId, EventType.BIRTHDAY, excludedEventId);
    }

    @Test
    void hasOtherBirthday_WhenExcludedEventIdIsNull_ShouldCallRepositoryWithNull() {
        Long contactId = 1L;
        Long excludedEventId = null;

        when(eventRepository.existsByContactIdAndEventTypeAndIdNot(contactId, EventType.BIRTHDAY, excludedEventId))
                .thenReturn(false);

        boolean result = eventService.hasOtherBirthday(contactId, excludedEventId);

        assertFalse(result);
        verify(eventRepository, times(1))
                .existsByContactIdAndEventTypeAndIdNot(contactId, EventType.BIRTHDAY, excludedEventId);
    }

    @Test
    void hasOtherBirthday_WhenOnlyOneBirthdayExists_ShouldReturnFalse() {
        Long contactId = 1L;
        Long existingEventId = 1L;

        when(eventRepository.existsByContactIdAndEventTypeAndIdNot(contactId, EventType.BIRTHDAY, existingEventId))
                .thenReturn(false);

        boolean result = eventService.hasOtherBirthday(contactId, existingEventId);

        assertFalse(result);
        verify(eventRepository, times(1))
                .existsByContactIdAndEventTypeAndIdNot(contactId, EventType.BIRTHDAY, existingEventId);
    }

    @Test
    void hasOtherBirthday_WhenMultipleBirthdaysExist_ShouldReturnTrue() {
        Long contactId = 1L;
        Long excludedEventId = 1L;

        when(eventRepository.existsByContactIdAndEventTypeAndIdNot(contactId, EventType.BIRTHDAY, excludedEventId))
                .thenReturn(true);

        boolean result = eventService.hasOtherBirthday(contactId, excludedEventId);

        assertTrue(result);
        verify(eventRepository, times(1))
                .existsByContactIdAndEventTypeAndIdNot(contactId, EventType.BIRTHDAY, excludedEventId);
    }

    @Test
    void hasOtherBirthday_WhenContactHasNoBirthday_ShouldReturnFalse() {
        Long contactId = 999L;
        Long excludedEventId = 1L;

        when(eventRepository.existsByContactIdAndEventTypeAndIdNot(contactId, EventType.BIRTHDAY, excludedEventId))
                .thenReturn(false);

        boolean result = eventService.hasOtherBirthday(contactId, excludedEventId);

        assertFalse(result);
        verify(eventRepository, times(1))
                .existsByContactIdAndEventTypeAndIdNot(contactId, EventType.BIRTHDAY, excludedEventId);
    }

    @Test
    void hasOtherBirthday_WhenCheckingSameEvent_ShouldReturnFalse() {
        Long contactId = 1L;
        Long eventId = 1L;

        // Контакт имеет день рождения с ID=1, но мы исключаем этот же ID
        when(eventRepository.existsByContactIdAndEventTypeAndIdNot(contactId, EventType.BIRTHDAY, eventId))
                .thenReturn(false);

        boolean result = eventService.hasOtherBirthday(contactId, eventId);

        assertFalse(result);
        verify(eventRepository, times(1))
                .existsByContactIdAndEventTypeAndIdNot(contactId, EventType.BIRTHDAY, eventId);
    }
}