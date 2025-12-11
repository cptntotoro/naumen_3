package ru.anastasia.NauJava.service.contact;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
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

    @Test
    void add_WhenValidNames_ShouldReturnSavedContact() {
        String firstName = "Иван";
        String lastName = "Иванов";
        Contact savedContact = createTestContact();

        when(contactRepository.save(any(Contact.class))).thenReturn(savedContact);

        Contact result = contactService.add(firstName, lastName);

        assertNotNull(result);
        assertEquals(savedContact.getId(), result.getId());
        assertEquals(savedContact.getFirstName(), result.getFirstName());
        assertEquals(savedContact.getLastName(), result.getLastName());
        verify(contactRepository, times(1)).save(any(Contact.class));
    }

    @Test
    void findById_WhenContactExists_ShouldReturnContact() {
        Long contactId = 1L;
        Contact testContact = createTestContact();

        when(contactRepository.findById(contactId)).thenReturn(Optional.of(testContact));

        Contact result = contactService.findById(contactId);

        assertNotNull(result);
        assertEquals(testContact.getId(), result.getId());
        assertEquals(testContact.getFirstName(), result.getFirstName());
        verify(contactRepository, times(1)).findById(contactId);
    }

    @Test
    void findById_WhenContactNotExists_ShouldThrowContactNotFoundException() {
        Long nonExistentId = 999L;

        when(contactRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        ContactNotFoundException exception = assertThrows(
                ContactNotFoundException.class,
                () -> contactService.findById(nonExistentId)
        );

        assertTrue(exception.getMessage().contains("Не найден контакт с id: " + nonExistentId));
        verify(contactRepository, times(1)).findById(nonExistentId);
    }

    @Test
    void deleteById_WhenValidId_ShouldCallRepositoryDelete() {
        Long contactId = 1L;

        when(contactRepository.existsById(contactId)).thenReturn(true);
        doNothing().when(contactRepository).deleteById(contactId);

        contactService.deleteById(contactId);

        verify(contactRepository, times(1)).deleteById(contactId);
    }

    @Test
    void update_WithTwoParameters_WhenValidData_ShouldReturnUpdatedContact() {
        Long contactId = 1L;
        String newFirstName = "НовоеИмя";
        String newLastName = "НоваяФамилия";
        Contact existingContact = createTestContact();
        Contact savedContact = Contact.builder()
                .id(contactId)
                .firstName(newFirstName)
                .lastName(newLastName)
                .build();

        when(contactRepository.findById(contactId)).thenReturn(Optional.of(existingContact));
        when(contactRepository.save(existingContact)).thenReturn(savedContact);

        Contact result = contactService.update(contactId, newFirstName, newLastName);

        assertNotNull(result);
        assertEquals(newFirstName, result.getFirstName());
        assertEquals(newLastName, result.getLastName());
        verify(contactRepository, times(1)).findById(contactId);
        verify(contactRepository, times(1)).save(existingContact);
    }

    @Test
    void update_WithTwoParameters_WhenContactNotFound_ShouldThrowContactNotFoundException() {
        Long nonExistentId = 999L;
        String firstName = "Имя";
        String lastName = "Фамилия";

        when(contactRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        ContactNotFoundException exception = assertThrows(
                ContactNotFoundException.class,
                () -> contactService.update(nonExistentId, firstName, lastName)
        );

        assertTrue(exception.getMessage().contains("Не найден контакт с id: " + nonExistentId));
        verify(contactRepository, times(1)).findById(nonExistentId);
        verify(contactRepository, never()).save(any(Contact.class));
    }

    @Test
    void update_WithSixParameters_WhenValidData_ShouldReturnUpdatedContact() {
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

        when(contactRepository.findById(contactId)).thenReturn(Optional.of(existingContact));
        when(contactRepository.save(existingContact)).thenReturn(savedContact);

        Contact result = contactService.update(contactId, newFirstName, newLastName, displayName, avatarUrl, isFavorite);

        assertNotNull(result);
        assertEquals(newFirstName, result.getFirstName());
        assertEquals(newLastName, result.getLastName());
        assertEquals(displayName, result.getDisplayName());
        assertEquals(avatarUrl, result.getAvatarUrl());
        assertEquals(isFavorite, result.getIsFavorite());
        verify(contactRepository, times(1)).findById(contactId);
        verify(contactRepository, times(1)).save(existingContact);
    }

    @Test
    void findAll_WhenContactsExist_ShouldReturnAllContacts() {
        List<Contact> expectedContacts = Arrays.asList(
                createTestContact(),
                createAnotherTestContact()
        );

        when(contactRepository.findAll()).thenReturn(expectedContacts);

        List<Contact> result = contactService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(contactRepository, times(1)).findAll();
    }

    @Test
    void findAll_WhenNoContacts_ShouldReturnEmptyList() {
        when(contactRepository.findAll()).thenReturn(List.of());

        List<Contact> result = contactService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(contactRepository, times(1)).findAll();
    }

    @Test
    void findByName_WhenContactsExist_ShouldReturnMatchingContacts() {
        String searchName = "Иван";
        List<Contact> expectedContacts = Collections.singletonList(createTestContact());

        when(contactRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(searchName, searchName))
                .thenReturn(expectedContacts);

        List<Contact> result = contactService.findByName(searchName);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(contactRepository, times(1))
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(searchName, searchName);
    }

    @Test
    void findAllByFullName_WhenContactsExist_ShouldReturnMatchingContacts() {
        String firstName = "Иван";
        String lastName = "Иванов";
        List<Contact> expectedContacts = Collections.singletonList(createTestContact());

        when(contactRepository.findByFirstNameAndLastName(firstName, lastName))
                .thenReturn(expectedContacts);

        List<Contact> result = contactService.findAllByFullName(firstName, lastName);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(contactRepository, times(1)).findByFirstNameAndLastName(firstName, lastName);
    }

    @Test
    void findByTag_WhenContactsExist_ShouldReturnMatchingContacts() {
        String tagName = "друзья";
        List<Contact> expectedContacts = Collections.singletonList(createTestContact());

        when(contactRepository.findContactsByTagName(tagName)).thenReturn(expectedContacts);

        List<Contact> result = contactService.findByTag(tagName);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(contactRepository, times(1)).findContactsByTagName(tagName);
    }

    @Test
    void updateAvatar_WhenValidData_ShouldReturnUpdatedContact() {
        Long contactId = 1L;
        String newAvatarUrl = "https://new-avatar.jpg";
        Contact existingContact = createTestContact();
        Contact savedContact = createTestContact();
        savedContact.setAvatarUrl(newAvatarUrl);

        when(contactRepository.findById(contactId)).thenReturn(Optional.of(existingContact));
        when(contactRepository.save(existingContact)).thenReturn(savedContact);

        Contact result = contactService.updateAvatar(contactId, newAvatarUrl);

        assertNotNull(result);
        assertEquals(newAvatarUrl, result.getAvatarUrl());
        verify(contactRepository, times(1)).findById(contactId);
        verify(contactRepository, times(1)).save(existingContact);
    }

    @Test
    void updateAvatar_WhenContactNotFound_ShouldThrowContactNotFoundException() {
        Long nonExistentId = 999L;
        String avatarUrl = "https://avatar.jpg";

        when(contactRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        ContactNotFoundException exception = assertThrows(
                ContactNotFoundException.class,
                () -> contactService.updateAvatar(nonExistentId, avatarUrl)
        );

        assertTrue(exception.getMessage().contains("Не найден контакт с id: " + nonExistentId));
        verify(contactRepository, times(1)).findById(nonExistentId);
        verify(contactRepository, never()).save(any(Contact.class));
    }

    @Test
    void search_WithSearchTerm_ShouldReturnMatchingContacts() {
        String searchTerm = "Иван";
        List<Contact> expectedContacts = Collections.singletonList(createTestContact());

        when(contactRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(searchTerm, searchTerm))
                .thenReturn(expectedContacts);

        List<Contact> result = contactService.search(searchTerm);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(contactRepository, times(1))
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(searchTerm, searchTerm);
    }

    @Test
    void search_WithEmptySearchTerm_ShouldReturnAllContacts() {
        List<Contact> expectedContacts = Arrays.asList(
                createTestContact(),
                createAnotherTestContact()
        );

        when(contactRepository.findAll()).thenReturn(expectedContacts);

        List<Contact> result = contactService.search("");

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(contactRepository, times(1)).findAll();
    }

    @Test
    void search_WithNullSearchTerm_ShouldReturnAllContacts() {
        List<Contact> expectedContacts = Arrays.asList(
                createTestContact(),
                createAnotherTestContact()
        );

        when(contactRepository.findAll()).thenReturn(expectedContacts);

        List<Contact> result = contactService.search(null);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(contactRepository, times(1)).findAll();
    }

    @Test
    void save_WhenValidContact_ShouldReturnSavedContact() {
        Contact testContact = createTestContact();
        Contact savedContact = createTestContact();

        when(contactRepository.save(testContact)).thenReturn(savedContact);

        Contact result = contactService.save(testContact);

        assertNotNull(result);
        assertEquals(savedContact.getId(), result.getId());
        verify(contactRepository, times(1)).save(testContact);
    }

    @Test
    void countTotal_WhenContactsExist_ShouldReturnCount() {
        long expectedCount = 5L;

        when(contactRepository.count()).thenReturn(expectedCount);

        Long result = contactService.countTotal();

        assertEquals(expectedCount, result);
        verify(contactRepository, times(1)).count();
    }

    @Test
    void countFavorites_WhenFavoriteContactsExist_ShouldReturnCount() {
        long expectedCount = 2L;

        when(contactRepository.countByIsFavoriteTrue()).thenReturn(expectedCount);

        Long result = contactService.countFavorites();

        assertEquals(expectedCount, result);
        verify(contactRepository, times(1)).countByIsFavoriteTrue();
    }

    @Test
    void addToFavorites_WhenContactExists_ShouldSetFavoriteTrue() {
        Long contactId = 1L;
        Contact contact = createTestContact();
        contact.setIsFavorite(false);

        when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));
        when(contactRepository.save(contact)).thenReturn(contact);

        contactService.addToFavorites(contactId);

        assertTrue(contact.getIsFavorite());
        verify(contactRepository, times(1)).findById(contactId);
        verify(contactRepository, times(1)).save(contact);
    }

    @Test
    void addToFavorites_WhenContactNotExists_ShouldDoNothing() {
        Long nonExistentId = 999L;

        when(contactRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        contactService.addToFavorites(nonExistentId);

        verify(contactRepository, times(1)).findById(nonExistentId);
        verify(contactRepository, never()).save(any(Contact.class));
    }

    @Test
    void removeFromFavorites_WhenContactExists_ShouldSetFavoriteFalse() {
        Long contactId = 1L;
        Contact contact = createTestContact();
        contact.setIsFavorite(true);

        when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));
        when(contactRepository.save(contact)).thenReturn(contact);

        contactService.removeFromFavorites(contactId);

        assertFalse(contact.getIsFavorite());
        verify(contactRepository, times(1)).findById(contactId);
        verify(contactRepository, times(1)).save(contact);
    }

    @Test
    void findBirthdaysThisMonth_WhenBirthdayEventsExist_ShouldReturnContacts() {
        Contact contact1 = createTestContact();
        Contact contact2 = createAnotherTestContact();
        List<Event> birthdayEvents = Arrays.asList(
                createBirthdayEvent(contact1),
                createBirthdayEvent(contact2)
        );

        when(eventService.findByEventTypeAndEventDateBetween(any(), any(), any()))
                .thenReturn(birthdayEvents);

        List<Contact> result = contactService.findBirthdaysThisMonth();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(eventService, times(1)).findByEventTypeAndEventDateBetween(any(), any(), any());
    }

    @Test
    void findBirthdaysThisMonth_WhenNoBirthdayEvents_ShouldReturnEmptyList() {
        when(eventService.findByEventTypeAndEventDateBetween(any(), any(), any()))
                .thenReturn(List.of());

        List<Contact> result = contactService.findBirthdaysThisMonth();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(eventService, times(1)).findByEventTypeAndEventDateBetween(any(), any(), any());
    }

    @Test
    void searchContacts_WhenNoFilters_ShouldReturnAllContactsPage() {
        String searchTerm = "";
        String companyName = "";
        String tagName = "";
        Pageable pageable = PageRequest.of(0, 10);
        List<Contact> contacts = Arrays.asList(createTestContact(), createAnotherTestContact());
        Page<Contact> expectedPage = new PageImpl<>(contacts, pageable, contacts.size());

        when(contactRepository.findAll(pageable)).thenReturn(expectedPage);

        Page<Contact> result = contactService.searchContacts(searchTerm, companyName, tagName, pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(contacts, result.getContent());
        verify(contactRepository, times(1)).findAll(pageable);
        verify(contactRepository, never()).searchWithFilters(any(), any(), any(), any());
    }

    @Test
    void searchContacts_WithSearchTermOnly_ShouldUseSearchWithFilters() {
        String searchTerm = "Иван";
        String companyName = "";
        String tagName = "";
        Pageable pageable = PageRequest.of(0, 10);
        List<Contact> contacts = Collections.singletonList(createTestContact());
        Page<Contact> expectedPage = new PageImpl<>(contacts, pageable, contacts.size());

        when(contactRepository.searchWithFilters(eq(searchTerm.trim()), isNull(), isNull(), eq(pageable)))
                .thenReturn(expectedPage);

        Page<Contact> result = contactService.searchContacts(searchTerm, companyName, tagName, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(contacts, result.getContent());
        verify(contactRepository, times(1)).searchWithFilters(eq(searchTerm.trim()), isNull(), isNull(), eq(pageable));
        verify(contactRepository, never()).findAll(pageable);
    }

    @Test
    void searchContacts_WithCompanyNameOnly_ShouldUseSearchWithFilters() {
        String searchTerm = "";
        String companyName = "ООО Рога и Копыта";
        String tagName = "";
        Pageable pageable = PageRequest.of(0, 10);
        List<Contact> contacts = Collections.singletonList(createTestContact());
        Page<Contact> expectedPage = new PageImpl<>(contacts, pageable, contacts.size());

        when(contactRepository.searchWithFilters(isNull(), eq(companyName.trim()), isNull(), eq(pageable)))
                .thenReturn(expectedPage);

        Page<Contact> result = contactService.searchContacts(searchTerm, companyName, tagName, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(contactRepository, times(1)).searchWithFilters(isNull(), eq(companyName.trim()), isNull(), eq(pageable));
    }

    @Test
    void searchContacts_WithTagNameOnly_ShouldUseSearchWithFilters() {
        String searchTerm = "";
        String companyName = "";
        String tagName = "друзья";
        Pageable pageable = PageRequest.of(0, 10);
        List<Contact> contacts = Collections.singletonList(createTestContact());
        Page<Contact> expectedPage = new PageImpl<>(contacts, pageable, contacts.size());

        when(contactRepository.searchWithFilters(isNull(), isNull(), eq(tagName.trim()), eq(pageable)))
                .thenReturn(expectedPage);

        Page<Contact> result = contactService.searchContacts(searchTerm, companyName, tagName, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(contactRepository, times(1)).searchWithFilters(isNull(), isNull(), eq(tagName.trim()), eq(pageable));
    }

    @Test
    void searchContacts_WithAllFilters_ShouldUseSearchWithFilters() {
        String searchTerm = "Иван";
        String companyName = "Компания";
        String tagName = "друзья";
        Pageable pageable = PageRequest.of(0, 10);
        List<Contact> contacts = Collections.singletonList(createTestContact());
        Page<Contact> expectedPage = new PageImpl<>(contacts, pageable, contacts.size());

        when(contactRepository.searchWithFilters(
                eq(searchTerm.trim()),
                eq(companyName.trim()),
                eq(tagName.trim()),
                eq(pageable)))
                .thenReturn(expectedPage);

        Page<Contact> result = contactService.searchContacts(searchTerm, companyName, tagName, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(contactRepository, times(1)).searchWithFilters(
                eq(searchTerm.trim()),
                eq(companyName.trim()),
                eq(tagName.trim()),
                eq(pageable));
    }

    @Test
    void findAll_WithPageable_ShouldReturnPageOfContacts() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Contact> contacts = Arrays.asList(createTestContact(), createAnotherTestContact());
        Page<Contact> expectedPage = new PageImpl<>(contacts, pageable, contacts.size());

        when(contactRepository.findAll(pageable)).thenReturn(expectedPage);

        Page<Contact> result = contactService.findAll(pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(contacts, result.getContent());
        verify(contactRepository, times(1)).findAll(pageable);
    }

    @Test
    void findAll_WithEmptyPage_ShouldReturnEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Contact> expectedPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(contactRepository.findAll(pageable)).thenReturn(expectedPage);

        Page<Contact> result = contactService.findAll(pageable);

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
        verify(contactRepository, times(1)).findAll(pageable);
    }

    @Test
    void findFavorites_WithPageable_ShouldReturnPageOfFavoriteContacts() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Contact> favoriteContacts = Collections.singletonList(createTestContact());
        Page<Contact> expectedPage = new PageImpl<>(favoriteContacts, pageable, favoriteContacts.size());

        when(contactRepository.findByIsFavoriteTrue(pageable)).thenReturn(expectedPage);

        Page<Contact> result = contactService.findFavorites(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertTrue(result.getContent().get(0).getIsFavorite());
        verify(contactRepository, times(1)).findByIsFavoriteTrue(pageable);
    }

    @Test
    void findFavorites_WhenNoFavorites_ShouldReturnEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Contact> expectedPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(contactRepository.findByIsFavoriteTrue(pageable)).thenReturn(expectedPage);

        Page<Contact> result = contactService.findFavorites(pageable);

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
        verify(contactRepository, times(1)).findByIsFavoriteTrue(pageable);
    }

    @Test
    void findWithUpcomingBirthdays_WithinSameYear_ShouldCallFindBirthdaysInRange() {
        int daysAhead = 7;
        List<Contact> expectedContacts = Collections.singletonList(createTestContact());

        when(contactRepository.findBirthdaysInRange(anyString(), anyString()))
                .thenReturn(expectedContacts);

        List<Contact> result = contactService.findWithUpcomingBirthdays(daysAhead);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(contactRepository, times(1)).findBirthdaysInRange(anyString(), anyString());
        verify(contactRepository, never()).findBirthdaysCrossingYear(anyString(), anyString());
    }

    @Test
    void findWithUpcomingBirthdays_CrossingYearBoundary_ShouldCallFindBirthdaysCrossingYear() {
        int daysAhead = 30;
        List<Contact> expectedContacts = Collections.singletonList(createTestContact());

        // Предположим, что сегодня 15 декабря, а daysAhead = 30, значит диапазон пересекает новый год
        when(contactRepository.findBirthdaysCrossingYear(anyString(), anyString()))
                .thenReturn(expectedContacts);

        List<Contact> result = contactService.findWithUpcomingBirthdays(daysAhead);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(contactRepository, times(1)).findBirthdaysCrossingYear(anyString(), anyString());
    }

    @Test
    void findWithUpcomingBirthdays_WhenNoUpcomingBirthdays_ShouldReturnEmptyList() {
        int daysAhead = 7;

        when(contactRepository.findBirthdaysInRange(anyString(), anyString()))
                .thenReturn(Collections.emptyList());

        List<Contact> result = contactService.findWithUpcomingBirthdays(daysAhead);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(contactRepository, times(1)).findBirthdaysInRange(anyString(), anyString());
    }

    @Test
    void existsById_WhenContactExists_ShouldReturnTrue() {
        Long contactId = 1L;

        when(contactRepository.existsById(contactId)).thenReturn(true);

        boolean result = contactService.existsById(contactId);

        assertTrue(result);
        verify(contactRepository, times(1)).existsById(contactId);
    }

    @Test
    void existsById_WhenContactDoesNotExist_ShouldReturnFalse() {
        Long nonExistentId = 999L;

        when(contactRepository.existsById(nonExistentId)).thenReturn(false);

        boolean result = contactService.existsById(nonExistentId);

        assertFalse(result);
        verify(contactRepository, times(1)).existsById(nonExistentId);
    }

    @Test
    void existsById_WhenIdIsNull_ShouldReturnFalse() {
        when(contactRepository.existsById(null)).thenReturn(false);

        boolean result = contactService.existsById(null);

        assertFalse(result);
        verify(contactRepository, times(1)).existsById(null);
    }
}