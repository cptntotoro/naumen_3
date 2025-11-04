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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
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

    @Test
    void add_ShouldReturnSavedContact() {
        String firstName = "John";
        String lastName = "Doe";
        Contact savedContact = Contact.builder().id(1L).firstName(firstName).lastName(lastName).build();

        when(contactRepository.save(any(Contact.class))).thenReturn(savedContact);

        Contact result = contactService.add(firstName, lastName);

        assertNotNull(result.getId());
        assertEquals(firstName, result.getFirstName());
        assertEquals(lastName, result.getLastName());
        verify(contactRepository).save(any(Contact.class));
    }

    @Test
    void findById_ShouldReturnContact_WhenExists() {
        Long id = 1L;
        Contact contact = Contact.builder().id(id).firstName("John").lastName("Doe").build();

        when(contactRepository.findById(id)).thenReturn(Optional.of(contact));

        Contact result = contactService.findById(id);

        assertEquals(contact, result);
        verify(contactRepository).findById(id);
    }

    @Test
    void findById_ShouldThrowContactNotFoundException_WhenNotExists() {
        Long id = 999L;

        when(contactRepository.findById(id)).thenReturn(Optional.empty());

        ContactNotFoundException exception = assertThrows(
                ContactNotFoundException.class,
                () -> contactService.findById(id)
        );

        assertTrue(exception.getMessage().contains("Не найден контакт с id: " + id));
        verify(contactRepository).findById(id);
    }

    @Test
    void deleteById_ShouldCallRepositoryDelete() {
        Long id = 1L;
        doNothing().when(contactRepository).deleteById(id);

        contactService.deleteById(id);

        verify(contactRepository).deleteById(id);
    }

    @Test
    void update_WithName_ShouldReturnUpdatedContact_WhenExists() {
        Long id = 1L;
        String newFirstName = "Jane";
        String newLastName = "Smith";
        Contact existingContact = Contact.builder().id(id).firstName("John").lastName("Doe").build();
        Contact updatedContact = Contact.builder().id(id).firstName(newFirstName).lastName(newLastName).build();

        when(contactRepository.findById(id)).thenReturn(Optional.of(existingContact));
        when(contactRepository.save(existingContact)).thenReturn(updatedContact);

        Contact result = contactService.update(id, newFirstName, newLastName);

        assertEquals(newFirstName, result.getFirstName());
        assertEquals(newLastName, result.getLastName());
        verify(contactRepository).findById(id);
        verify(contactRepository).save(existingContact);
    }

    @Test
    void update_WithName_ShouldThrowContactNotFoundException_WhenNotExists() {
        Long id = 999L;

        when(contactRepository.findById(id)).thenReturn(Optional.empty());

        ContactNotFoundException exception = assertThrows(
                ContactNotFoundException.class,
                () -> contactService.update(id, "Jane", "Smith")
        );

        assertTrue(exception.getMessage().contains("Не найден контакт с id: " + id));
        verify(contactRepository).findById(id);
        verify(contactRepository, never()).save(any(Contact.class));
    }

    @Test
    void update_WithAllFields_ShouldReturnUpdatedContact_WhenExists() {
        Long id = 1L;
        Contact existingContact = Contact.builder()
                .id(id)
                .firstName("John")
                .lastName("Doe")
                .displayName("John Doe")
                .avatarUrl("old.jpg")
                .isFavorite(false)
                .build();
        Contact updatedContact = Contact.builder()
                .id(id)
                .firstName("Jane")
                .lastName("Smith")
                .displayName("Jane Smith")
                .avatarUrl("new.jpg")
                .isFavorite(true)
                .build();

        when(contactRepository.findById(id)).thenReturn(Optional.of(existingContact));
        when(contactRepository.save(existingContact)).thenReturn(updatedContact);

        Contact result = contactService.update(id, "Jane", "Smith", "Jane Smith", "new.jpg", true);

        assertEquals("Jane", result.getFirstName());
        assertEquals("Smith", result.getLastName());
        assertEquals("Jane Smith", result.getDisplayName());
        assertEquals("new.jpg", result.getAvatarUrl());
        assertTrue(result.getIsFavorite());
        verify(contactRepository).findById(id);
        verify(contactRepository).save(existingContact);
    }

    @Test
    void findAll_ShouldReturnAllContacts() {
        Contact contact1 = Contact.builder().id(1L).build();
        Contact contact2 = Contact.builder().id(2L).build();
        List<Contact> contacts = List.of(contact1, contact2);

        when(contactRepository.findAll()).thenReturn(contacts);

        List<Contact> result = contactService.findAll();

        assertEquals(2, result.size());
        verify(contactRepository).findAll();
    }

    @Test
    void findByName_ShouldReturnMatchingContacts() {
        String name = "John";
        Contact contact1 = Contact.builder().id(1L).firstName("John").lastName("Doe").build();
        Contact contact2 = Contact.builder().id(2L).firstName("Johnny").lastName("Smith").build();
        List<Contact> expectedContacts = List.of(contact1, contact2);

        when(contactRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name, name))
                .thenReturn(expectedContacts);

        List<Contact> result = contactService.findByName(name);

        assertEquals(expectedContacts, result);
        verify(contactRepository).findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name, name);
    }

    @Test
    void findAllByFullName_ShouldReturnContacts() {
        String firstName = "John";
        String lastName = "Doe";
        Contact contact = Contact.builder().id(1L).firstName(firstName).lastName(lastName).build();
        List<Contact> expectedContacts = List.of(contact);

        when(contactRepository.findByFirstNameAndLastName(firstName, lastName)).thenReturn(expectedContacts);

        List<Contact> result = contactService.findAllByFullName(firstName, lastName);

        assertEquals(expectedContacts, result);
        verify(contactRepository).findByFirstNameAndLastName(firstName, lastName);
    }

    @Test
    void findByTag_ShouldReturnContacts() {
        String tagName = "friend";
        Contact contact = Contact.builder().id(1L).build();
        List<Contact> expectedContacts = List.of(contact);

        when(contactRepository.findContactsByTagName(tagName)).thenReturn(expectedContacts);

        List<Contact> result = contactService.findByTag(tagName);

        assertEquals(expectedContacts, result);
        verify(contactRepository).findContactsByTagName(tagName);
    }

    @Test
    void updateAvatar_ShouldReturnUpdatedContact_WhenExists() {
        Long contactId = 1L;
        String avatarUrl = "new-avatar.jpg";
        Contact existingContact = Contact.builder().id(contactId).avatarUrl("old-avatar.jpg").build();
        Contact updatedContact = Contact.builder().id(contactId).avatarUrl(avatarUrl).build();

        when(contactRepository.findById(contactId)).thenReturn(Optional.of(existingContact));
        when(contactRepository.save(existingContact)).thenReturn(updatedContact);

        Contact result = contactService.updateAvatar(contactId, avatarUrl);

        assertEquals(avatarUrl, result.getAvatarUrl());
        verify(contactRepository).findById(contactId);
        verify(contactRepository).save(existingContact);
    }

    @Test
    void updateAvatar_ShouldThrowContactNotFoundException_WhenNotExists() {
        Long contactId = 999L;

        when(contactRepository.findById(contactId)).thenReturn(Optional.empty());

        ContactNotFoundException exception = assertThrows(
                ContactNotFoundException.class,
                () -> contactService.updateAvatar(contactId, "avatar.jpg")
        );

        assertTrue(exception.getMessage().contains("Не найден контакт с id: " + contactId));
        verify(contactRepository).findById(contactId);
        verify(contactRepository, never()).save(any(Contact.class));
    }

    @Test
    void search_ShouldReturnAllContacts_WhenSearchTermIsEmpty() {
        Contact contact1 = Contact.builder().id(1L).build();
        Contact contact2 = Contact.builder().id(2L).build();
        List<Contact> contacts = List.of(contact1, contact2);

        when(contactRepository.findAll()).thenReturn(contacts);

        List<Contact> result = contactService.search("");

        assertEquals(2, result.size());
        verify(contactRepository).findAll();
        verify(contactRepository, never()).findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(anyString(), anyString());
    }

    @Test
    void search_ShouldReturnMatchingContacts_WhenSearchTermProvided() {
        String searchTerm = "John";
        Contact contact = Contact.builder().id(1L).firstName("John").build();
        List<Contact> expectedContacts = List.of(contact);

        when(contactRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(searchTerm, searchTerm))
                .thenReturn(expectedContacts);

        List<Contact> result = contactService.search(searchTerm);

        assertEquals(expectedContacts, result);
        verify(contactRepository).findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(searchTerm, searchTerm);
    }

    @Test
    void save_ShouldReturnSavedContact() {
        Contact contact = Contact.builder().firstName("John").lastName("Doe").build();
        Contact savedContact = Contact.builder().id(1L).firstName("John").lastName("Doe").build();

        when(contactRepository.save(contact)).thenReturn(savedContact);

        Contact result = contactService.save(contact);

        assertNotNull(result.getId());
        assertEquals(savedContact, result);
        verify(contactRepository).save(contact);
    }

    @Test
    void addToFavorites_ShouldSetFavoriteTrue_WhenContactExists() {
        Long contactId = 1L;
        Contact contact = Contact.builder().id(contactId).isFavorite(false).build();

        when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));

        contactService.addToFavorites(contactId);

        assertTrue(contact.getIsFavorite());
        verify(contactRepository).findById(contactId);
        verify(contactRepository).save(contact);
    }

    @Test
    void addToFavorites_ShouldDoNothing_WhenContactNotExists() {
        Long contactId = 999L;

        when(contactRepository.findById(contactId)).thenReturn(Optional.empty());

        contactService.addToFavorites(contactId);

        verify(contactRepository).findById(contactId);
        verify(contactRepository, never()).save(any(Contact.class));
    }

    @Test
    void removeFromFavorites_ShouldSetFavoriteFalse_WhenContactExists() {
        Long contactId = 1L;
        Contact contact = Contact.builder().id(contactId).isFavorite(true).build();

        when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));

        contactService.removeFromFavorites(contactId);

        assertFalse(contact.getIsFavorite());
        verify(contactRepository).findById(contactId);
        verify(contactRepository).save(contact);
    }

    @Test
    void removeFromFavorites_ShouldDoNothing_WhenContactNotExists() {
        Long contactId = 999L;

        when(contactRepository.findById(contactId)).thenReturn(Optional.empty());

        contactService.removeFromFavorites(contactId);

        verify(contactRepository).findById(contactId);
        verify(contactRepository, never()).save(any(Contact.class));
    }

    @Test
    void findFavorites_ShouldReturnFavoriteContacts() {
        Contact favorite1 = Contact.builder().id(1L).isFavorite(true).build();
        Contact favorite2 = Contact.builder().id(2L).isFavorite(true).build();
        List<Contact> expectedFavorites = List.of(favorite1, favorite2);

        when(contactRepository.findByIsFavoriteTrue()).thenReturn(expectedFavorites);

        List<Contact> result = contactService.findFavorites();

        assertEquals(expectedFavorites, result);
        verify(contactRepository).findByIsFavoriteTrue();
    }

    @Test
    void findBirthdaysThisMonth_ShouldReturnContactsWithBirthdaysThisMonth() {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        Contact contact1 = Contact.builder().id(1L).build();
        Contact contact2 = Contact.builder().id(2L).build();
        Event birthday1 = Event.builder().id(1L).contact(contact1).eventType(EventType.BIRTHDAY).build();
        Event birthday2 = Event.builder().id(2L).contact(contact2).eventType(EventType.BIRTHDAY).build();
        List<Event> birthdayEvents = List.of(birthday1, birthday2);

        when(eventService.findByEventTypeAndEventDateBetween(EventType.BIRTHDAY, startOfMonth, endOfMonth))
                .thenReturn(birthdayEvents);

        List<Contact> result = contactService.findBirthdaysThisMonth();

        assertEquals(2, result.size());
        assertTrue(result.contains(contact1));
        assertTrue(result.contains(contact2));
        verify(eventService).findByEventTypeAndEventDateBetween(EventType.BIRTHDAY, startOfMonth, endOfMonth);
    }
}