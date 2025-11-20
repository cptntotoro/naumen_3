package ru.anastasia.NauJava.service.contact;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.anastasia.NauJava.config.AppConfig;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.entity.enums.EventType;
import ru.anastasia.NauJava.entity.event.Event;
import ru.anastasia.NauJava.exception.contact.ContactNotFoundException;
import ru.anastasia.NauJava.repository.contact.ContactRepository;
import ru.anastasia.NauJava.service.contact.impl.ContactServiceImpl;
import ru.anastasia.NauJava.service.event.EventService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest {

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private EventService eventService;

    @Mock
    private AppConfig appConfig;

    @InjectMocks
    private ContactServiceImpl contactService;

    private Contact createTestContact() {
        return Contact.builder()
                .id(1L)
                .firstName("Иван")
                .lastName("Иванов")
                .displayName("Иван Иванов")
                .avatarUrl("https://example.com/avatar.jpg")
                .isFavorite(true)
                .build();
    }

    private Contact createAnotherTestContact() {
        return Contact.builder()
                .id(2L)
                .firstName("Петр")
                .lastName("Петров")
                .displayName("Петр Петров")
                .avatarUrl("https://example.com/avatar2.jpg")
                .isFavorite(false)
                .build();
    }

    private Event createBirthdayEvent(Contact contact) {
        return Event.builder()
                .id(1L)
                .eventType(EventType.BIRTHDAY)
                .eventDate(LocalDate.now())
                .contact(contact)
                .build();
    }

    // Тесты для метода add()

    @Test
    void add_WhenValidNames_ShouldReturnSavedContact() {
        // Подготовка тестовых данных
        String firstName = "Иван";
        String lastName = "Иванов";
        Contact savedContact = createTestContact();

        // Настройка моков
        when(contactRepository.save(any(Contact.class))).thenReturn(savedContact);

        // Выполнение тестируемого метода
        Contact result = contactService.add(firstName, lastName);

        // Проверки
        assertNotNull(result);
        assertEquals(savedContact.getId(), result.getId());
        assertEquals(savedContact.getFirstName(), result.getFirstName());
        assertEquals(savedContact.getLastName(), result.getLastName());
        verify(contactRepository, times(1)).save(any(Contact.class));
    }

    // Тесты для метода findById()

    @Test
    void findById_WhenContactExists_ShouldReturnContact() {
        // Подготовка тестовых данных
        Long contactId = 1L;
        Contact testContact = createTestContact();

        // Настройка моков
        when(contactRepository.findById(contactId)).thenReturn(Optional.of(testContact));

        // Выполнение тестируемого метода
        Contact result = contactService.findById(contactId);

        // Проверки
        assertNotNull(result);
        assertEquals(testContact.getId(), result.getId());
        assertEquals(testContact.getFirstName(), result.getFirstName());
        verify(contactRepository, times(1)).findById(contactId);
    }

    @Test
    void findById_WhenContactNotExists_ShouldThrowContactNotFoundException() {
        // Подготовка тестовых данных
        Long nonExistentId = 999L;

        // Настройка моков
        when(contactRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Проверка исключения
        ContactNotFoundException exception = assertThrows(
                ContactNotFoundException.class,
                () -> contactService.findById(nonExistentId)
        );

        // Проверки
        assertTrue(exception.getMessage().contains("Не найден контакт с id: " + nonExistentId));
        verify(contactRepository, times(1)).findById(nonExistentId);
    }

    // Тесты для метода deleteById()

    @Test
    void deleteById_WhenValidId_ShouldCallRepositoryDelete() {
        // Подготовка тестовых данных
        Long contactId = 1L;

        // Настройка моков
        doNothing().when(contactRepository).deleteById(contactId);

        // Выполнение тестируемого метода
        contactService.deleteById(contactId);

        // Проверки
        verify(contactRepository, times(1)).deleteById(contactId);
    }

    // Тесты для метода update() с двумя параметрами

    @Test
    void update_WithTwoParameters_WhenValidData_ShouldReturnUpdatedContact() {
        // Подготовка тестовых данных
        Long contactId = 1L;
        String newFirstName = "НовоеИмя";
        String newLastName = "НоваяФамилия";
        Contact existingContact = createTestContact();
        Contact savedContact = Contact.builder()
                .id(contactId)
                .firstName(newFirstName)
                .lastName(newLastName)
                .build();

        // Настройка моков
        when(contactRepository.findById(contactId)).thenReturn(Optional.of(existingContact));
        when(contactRepository.save(existingContact)).thenReturn(savedContact);

        // Выполнение тестируемого метода
        Contact result = contactService.update(contactId, newFirstName, newLastName);

        // Проверки
        assertNotNull(result);
        assertEquals(newFirstName, result.getFirstName());
        assertEquals(newLastName, result.getLastName());
        verify(contactRepository, times(1)).findById(contactId);
        verify(contactRepository, times(1)).save(existingContact);
    }

    @Test
    void update_WithTwoParameters_WhenContactNotFound_ShouldThrowContactNotFoundException() {
        // Подготовка тестовых данных
        Long nonExistentId = 999L;
        String firstName = "Имя";
        String lastName = "Фамилия";

        // Настройка моков
        when(contactRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Проверка исключения
        ContactNotFoundException exception = assertThrows(
                ContactNotFoundException.class,
                () -> contactService.update(nonExistentId, firstName, lastName)
        );

        // Проверки
        assertTrue(exception.getMessage().contains("Не найден контакт с id: " + nonExistentId));
        verify(contactRepository, times(1)).findById(nonExistentId);
        verify(contactRepository, never()).save(any(Contact.class));
    }

    // Тесты для метода update() с шестью параметрами

    @Test
    void update_WithSixParameters_WhenValidData_ShouldReturnUpdatedContact() {
        // Подготовка тестовых данных
        Long contactId = 1L;
        String newFirstName = "НовоеИмя";
        String newLastName = "НоваяФамилия";
        String displayName = "Новое отображаемое имя";
        String avatarUrl = "https://new-avatar.jpg";
        Boolean isFavorite = false;
        Contact existingContact = createTestContact();
        Contact savedContact = Contact.builder()
                .id(contactId)
                .firstName(newFirstName)
                .lastName(newLastName)
                .displayName(displayName)
                .avatarUrl(avatarUrl)
                .isFavorite(isFavorite)
                .build();

        // Настройка моков
        when(contactRepository.findById(contactId)).thenReturn(Optional.of(existingContact));
        when(contactRepository.save(existingContact)).thenReturn(savedContact);

        // Выполнение тестируемого метода
        Contact result = contactService.update(contactId, newFirstName, newLastName, displayName, avatarUrl, isFavorite);

        // Проверки
        assertNotNull(result);
        assertEquals(newFirstName, result.getFirstName());
        assertEquals(newLastName, result.getLastName());
        assertEquals(displayName, result.getDisplayName());
        assertEquals(avatarUrl, result.getAvatarUrl());
        assertEquals(isFavorite, result.getIsFavorite());
        verify(contactRepository, times(1)).findById(contactId);
        verify(contactRepository, times(1)).save(existingContact);
    }

    // Тесты для метода findAll()

    @Test
    void findAll_WhenContactsExist_ShouldReturnAllContacts() {
        // Подготовка тестовых данных
        List<Contact> expectedContacts = Arrays.asList(
                createTestContact(),
                createAnotherTestContact()
        );

        // Настройка моков
        when(contactRepository.findAll()).thenReturn(expectedContacts);

        // Выполнение тестируемого метода
        List<Contact> result = contactService.findAll();

        // Проверки
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(contactRepository, times(1)).findAll();
    }

    @Test
    void findAll_WhenNoContacts_ShouldReturnEmptyList() {
        // Настройка моков
        when(contactRepository.findAll()).thenReturn(List.of());

        // Выполнение тестируемого метода
        List<Contact> result = contactService.findAll();

        // Проверки
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(contactRepository, times(1)).findAll();
    }

    // Тесты для метода findByName()

    @Test
    void findByName_WhenContactsExist_ShouldReturnMatchingContacts() {
        // Подготовка тестовых данных
        String searchName = "Иван";
        List<Contact> expectedContacts = Collections.singletonList(createTestContact());

        // Настройка моков
        when(contactRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(searchName, searchName))
                .thenReturn(expectedContacts);

        // Выполнение тестируемого метода
        List<Contact> result = contactService.findByName(searchName);

        // Проверки
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(contactRepository, times(1))
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(searchName, searchName);
    }

    // Тесты для метода findAllByFullName()

    @Test
    void findAllByFullName_WhenContactsExist_ShouldReturnMatchingContacts() {
        // Подготовка тестовых данных
        String firstName = "Иван";
        String lastName = "Иванов";
        List<Contact> expectedContacts = Collections.singletonList(createTestContact());

        // Настройка моков
        when(contactRepository.findByFirstNameAndLastName(firstName, lastName))
                .thenReturn(expectedContacts);

        // Выполнение тестируемого метода
        List<Contact> result = contactService.findAllByFullName(firstName, lastName);

        // Проверки
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(contactRepository, times(1)).findByFirstNameAndLastName(firstName, lastName);
    }

    // Тесты для метода findByTag()

    @Test
    void findByTag_WhenContactsExist_ShouldReturnMatchingContacts() {
        // Подготовка тестовых данных
        String tagName = "друзья";
        List<Contact> expectedContacts = Collections.singletonList(createTestContact());

        // Настройка моков
        when(contactRepository.findContactsByTagName(tagName)).thenReturn(expectedContacts);

        // Выполнение тестируемого метода
        List<Contact> result = contactService.findByTag(tagName);

        // Проверки
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(contactRepository, times(1)).findContactsByTagName(tagName);
    }

    // Тесты для метода updateAvatar()

    @Test
    void updateAvatar_WhenValidData_ShouldReturnUpdatedContact() {
        // Подготовка тестовых данных
        Long contactId = 1L;
        String newAvatarUrl = "https://new-avatar.jpg";
        Contact existingContact = createTestContact();
        Contact savedContact = createTestContact();
        savedContact.setAvatarUrl(newAvatarUrl);

        // Настройка моков
        when(contactRepository.findById(contactId)).thenReturn(Optional.of(existingContact));
        when(contactRepository.save(existingContact)).thenReturn(savedContact);

        // Выполнение тестируемого метода
        Contact result = contactService.updateAvatar(contactId, newAvatarUrl);

        // Проверки
        assertNotNull(result);
        assertEquals(newAvatarUrl, result.getAvatarUrl());
        verify(contactRepository, times(1)).findById(contactId);
        verify(contactRepository, times(1)).save(existingContact);
    }

    @Test
    void updateAvatar_WhenContactNotFound_ShouldThrowContactNotFoundException() {
        // Подготовка тестовых данных
        Long nonExistentId = 999L;
        String avatarUrl = "https://avatar.jpg";

        // Настройка моков
        when(contactRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Проверка исключения
        ContactNotFoundException exception = assertThrows(
                ContactNotFoundException.class,
                () -> contactService.updateAvatar(nonExistentId, avatarUrl)
        );

        // Проверки
        assertTrue(exception.getMessage().contains("Не найден контакт с id: " + nonExistentId));
        verify(contactRepository, times(1)).findById(nonExistentId);
        verify(contactRepository, never()).save(any(Contact.class));
    }

    // Тесты для метода search()

    @Test
    void search_WithSearchTerm_ShouldReturnMatchingContacts() {
        // Подготовка тестовых данных
        String searchTerm = "Иван";
        List<Contact> expectedContacts = Collections.singletonList(createTestContact());

        // Настройка моков
        when(contactRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(searchTerm, searchTerm))
                .thenReturn(expectedContacts);

        // Выполнение тестируемого метода
        List<Contact> result = contactService.search(searchTerm);

        // Проверки
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(contactRepository, times(1))
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(searchTerm, searchTerm);
    }

    @Test
    void search_WithEmptySearchTerm_ShouldReturnAllContacts() {
        // Подготовка тестовых данных
        List<Contact> expectedContacts = Arrays.asList(
                createTestContact(),
                createAnotherTestContact()
        );

        // Настройка моков
        when(contactRepository.findAll()).thenReturn(expectedContacts);

        // Выполнение тестируемого метода
        List<Contact> result = contactService.search("");

        // Проверки
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(contactRepository, times(1)).findAll();
    }

    @Test
    void search_WithNullSearchTerm_ShouldReturnAllContacts() {
        // Подготовка тестовых данных
        List<Contact> expectedContacts = Arrays.asList(
                createTestContact(),
                createAnotherTestContact()
        );

        // Настройка моков
        when(contactRepository.findAll()).thenReturn(expectedContacts);

        // Выполнение тестируемого метода
        List<Contact> result = contactService.search(null);

        // Проверки
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(contactRepository, times(1)).findAll();
    }

    // Тесты для метода save()

    @Test
    void save_WhenValidContact_ShouldReturnSavedContact() {
        // Подготовка тестовых данных
        Contact testContact = createTestContact();
        Contact savedContact = createTestContact();

        // Настройка моков
        when(contactRepository.save(testContact)).thenReturn(savedContact);

        // Выполнение тестируемого метода
        Contact result = contactService.save(testContact);

        // Проверки
        assertNotNull(result);
        assertEquals(savedContact.getId(), result.getId());
        verify(contactRepository, times(1)).save(testContact);
    }

    // Тесты для метода countTotal()

    @Test
    void countTotal_WhenContactsExist_ShouldReturnCount() {
        // Подготовка тестовых данных
        long expectedCount = 5L;

        // Настройка моков
        when(contactRepository.count()).thenReturn(expectedCount);

        // Выполнение тестируемого метода
        Long result = contactService.countTotal();

        // Проверки
        assertEquals(expectedCount, result);
        verify(contactRepository, times(1)).count();
    }

    // Тесты для метода countFavorites()

    @Test
    void countFavorites_WhenFavoriteContactsExist_ShouldReturnCount() {
        // Подготовка тестовых данных
        long expectedCount = 2L;

        // Настройка моков
        when(contactRepository.countByIsFavoriteTrue()).thenReturn(expectedCount);

        // Выполнение тестируемого метода
        Long result = contactService.countFavorites();

        // Проверки
        assertEquals(expectedCount, result);
        verify(contactRepository, times(1)).countByIsFavoriteTrue();
    }

    // Тесты для метода addToFavorites()

    @Test
    void addToFavorites_WhenContactExists_ShouldSetFavoriteTrue() {
        // Подготовка тестовых данных
        Long contactId = 1L;
        Contact contact = createTestContact();
        contact.setIsFavorite(false);

        // Настройка моков
        when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));
        when(contactRepository.save(contact)).thenReturn(contact);

        // Выполнение тестируемого метода
        contactService.addToFavorites(contactId);

        // Проверки
        assertTrue(contact.getIsFavorite());
        verify(contactRepository, times(1)).findById(contactId);
        verify(contactRepository, times(1)).save(contact);
    }

    @Test
    void addToFavorites_WhenContactNotExists_ShouldDoNothing() {
        // Подготовка тестовых данных
        Long nonExistentId = 999L;

        // Настройка моков
        when(contactRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Выполнение тестируемого метода
        contactService.addToFavorites(nonExistentId);

        // Проверки
        verify(contactRepository, times(1)).findById(nonExistentId);
        verify(contactRepository, never()).save(any(Contact.class));
    }

    // Тесты для метода removeFromFavorites()

    @Test
    void removeFromFavorites_WhenContactExists_ShouldSetFavoriteFalse() {
        // Подготовка тестовых данных
        Long contactId = 1L;
        Contact contact = createTestContact();
        contact.setIsFavorite(true);

        // Настройка моков
        when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));
        when(contactRepository.save(contact)).thenReturn(contact);

        // Выполнение тестируемого метода
        contactService.removeFromFavorites(contactId);

        // Проверки
        assertFalse(contact.getIsFavorite());
        verify(contactRepository, times(1)).findById(contactId);
        verify(contactRepository, times(1)).save(contact);
    }

    // Тесты для метода findFavorites()

    @Test
    void findFavorites_WhenFavoriteContactsExist_ShouldReturnFavoriteContacts() {
        // Подготовка тестовых данных
        List<Contact> expectedContacts = Collections.singletonList(createTestContact());

        // Настройка моков
        when(contactRepository.findByIsFavoriteTrue()).thenReturn(expectedContacts);

        // Выполнение тестируемого метода
        List<Contact> result = contactService.findFavorites();

        // Проверки
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.getFirst().getIsFavorite());
        verify(contactRepository, times(1)).findByIsFavoriteTrue();
    }

    // Тесты для метода findBirthdaysThisMonth()

    @Test
    void findBirthdaysThisMonth_WhenBirthdayEventsExist_ShouldReturnContacts() {
        // Подготовка тестовых данных
        Contact contact1 = createTestContact();
        Contact contact2 = createAnotherTestContact();
        List<Event> birthdayEvents = Arrays.asList(
                createBirthdayEvent(contact1),
                createBirthdayEvent(contact2)
        );

        // Настройка моков
        when(eventService.findByEventTypeAndEventDateBetween(any(), any(), any()))
                .thenReturn(birthdayEvents);

        // Выполнение тестируемого метода
        List<Contact> result = contactService.findBirthdaysThisMonth();

        // Проверки
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(eventService, times(1)).findByEventTypeAndEventDateBetween(any(), any(), any());
    }

    @Test
    void findBirthdaysThisMonth_WhenNoBirthdayEvents_ShouldReturnEmptyList() {
        // Настройка моков
        when(eventService.findByEventTypeAndEventDateBetween(any(), any(), any()))
                .thenReturn(List.of());

        // Выполнение тестируемого метода
        List<Contact> result = contactService.findBirthdaysThisMonth();

        // Проверки
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(eventService, times(1)).findByEventTypeAndEventDateBetween(any(), any(), any());
    }
}