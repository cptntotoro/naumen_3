package ru.anastasia.NauJava.service.contact;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.entity.contact.Event;
import ru.anastasia.NauJava.entity.enums.EventType;
import ru.anastasia.NauJava.entity.tag.ContactTag;
import ru.anastasia.NauJava.entity.tag.Tag;
import ru.anastasia.NauJava.repository.contact.ContactRepository;
import ru.anastasia.NauJava.repository.contact.EventRepository;
import ru.anastasia.NauJava.repository.tag.ContactTagRepository;
import ru.anastasia.NauJava.repository.tag.TagRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class ContactServiceTest {

    @Autowired
    private ContactService contactService;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ContactTagRepository contactTagRepository;

    @Test
    void testAdd_Success() {
        String firstName = "Иван";
        String lastName = "Иванов";
        Contact contact = contactService.add(firstName, lastName);

        assertNotNull(contact.getId());
        assertEquals(firstName, contact.getFirstName());
        assertEquals(lastName, contact.getLastName());
        assertTrue(contactRepository.findById(contact.getId()).isPresent());
    }

    @Test
    void testFindById_Success() {
        Contact contact = Contact.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .build();

        contactRepository.save(contact);

        Contact foundContact = contactService.findById(contact.getId());

        assertNotNull(foundContact);
        assertEquals(contact.getId(), foundContact.getId());
    }

    @Test
    void testFindById_NotFound() {
        Long nonExistentId = 999L;

        Contact foundContact = contactService.findById(nonExistentId);

        assertNull(foundContact);
    }

    @Test
    void testDeleteById_Success() {
        Contact contact = Contact.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .build();

        contactRepository.save(contact);

        contactService.deleteById(contact.getId());

        assertFalse(contactRepository.findById(contact.getId()).isPresent());
    }

    @Test
    void testUpdate_Success() {
        Contact contact = Contact.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .build();

        contactRepository.save(contact);

        String newFirstName = "Петр";
        String newLastName = "Петров";
        Contact updatedContact = contactService.update(contact.getId(), newFirstName, newLastName);

        assertEquals(newFirstName, updatedContact.getFirstName());
        assertEquals(newLastName, updatedContact.getLastName());
        assertTrue(contactRepository.findById(contact.getId()).isPresent());
    }

    @Test
    void testUpdate_NotFound() {
        Long nonExistentId = 999L;
        String newFirstName = "Петр";
        String newLastName = "Петров";

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                contactService.update(nonExistentId, newFirstName, newLastName));

        assertEquals("Не найден контакт с id: " + nonExistentId, exception.getMessage());
    }

    @Test
    void testFindAll_Success() {
        Contact contact1 = Contact.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .build();

        Contact contact2 = Contact.builder()
                .firstName("Анна")
                .lastName("Петрова")
                .build();

        contactRepository.save(contact1);
        contactRepository.save(contact2);

        List<Contact> contacts = contactService.findAll();

        assertTrue(contacts.size() >= 2);
        assertTrue(contacts.stream().anyMatch(c -> c.getFirstName().equals("Иван")));
        assertTrue(contacts.stream().anyMatch(c -> c.getFirstName().equals("Анна")));
    }

    @Test
    void testFindByName_Success() {
        Contact contact = Contact.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .build();

        contactRepository.save(contact);

        List<Contact> contacts = contactService.findByName("Иван");

        assertFalse(contacts.isEmpty());
        assertEquals("Иван", contacts.getFirst().getFirstName());
    }

    @Test
    void testFindAllByFullName_Success() {
        Contact contact = Contact.builder()
                .firstName("Анна")
                .lastName("Петрова")
                .build();

        contactRepository.save(contact);

        List<Contact> contacts = contactService.findAllByFullName("Анна", "Петрова");

        assertFalse(contacts.isEmpty());
        assertEquals("Анна", contacts.getFirst().getFirstName());
        assertEquals("Петрова", contacts.getFirst().getLastName());
    }

    @Test
    void testFindByTag_Success() {
        Contact contact = Contact.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .build();

        contactRepository.save(contact);

        String tagName = "Коллега" + UUID.randomUUID();

        Tag tag = new Tag();
        tag.setName(tagName);
        tagRepository.save(tag);

        ContactTag contactTag = ContactTag.builder()
                .contact(contact)
                .tag(tag)
                .build();

        contactTagRepository.save(contactTag);

        List<Contact> contacts = contactService.findByTag(tagName);

        assertFalse(contacts.isEmpty());
        assertEquals(contact.getId(), contacts.getFirst().getId());
    }

    @Test
    void testUpdateAvatar_Success() {
        Contact contact = Contact.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .build();

        contactRepository.save(contact);

        String avatarUrl = "http://example.com/avatar.jpg";
        Contact updatedContact = contactService.updateAvatar(contact.getId(), avatarUrl);

        assertEquals(avatarUrl, updatedContact.getAvatarUrl());
        assertTrue(contactRepository.findById(contact.getId()).isPresent());
    }

    @Test
    void testUpdateAvatar_NotFound() {
        Long nonExistentId = 999L;
        String avatarUrl = "http://example.com/avatar.jpg";

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                contactService.updateAvatar(nonExistentId, avatarUrl));

        assertEquals("Контакт не найден", exception.getMessage());
    }

    @Test
    void testSearch_Success() {
        Contact contact = Contact.builder()
                .firstName("Анна")
                .lastName("Петрова")
                .build();

        contactRepository.save(contact);

        List<Contact> contacts = contactService.search("Анна");

        assertFalse(contacts.isEmpty());
        assertEquals("Анна", contacts.getFirst().getFirstName());
    }

    @Test
    void testSearch_EmptySearchTerm() {
        Contact contact1 = Contact.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .build();

        Contact contact2 = Contact.builder()
                .firstName("Анна")
                .lastName("Петрова")
                .build();

        contactRepository.save(contact1);
        contactRepository.save(contact2);

        List<Contact> contacts = contactService.search("");

        assertTrue(contacts.size() >= 2);
        assertTrue(contacts.stream().anyMatch(c -> c.getFirstName().equals("Иван")));
        assertTrue(contacts.stream().anyMatch(c -> c.getFirstName().equals("Анна")));
    }

    @Test
    void testAddToFavorites_Success() {
        Contact contact = Contact.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .build();

        contactRepository.save(contact);

        contactService.addToFavorites(contact.getId());

        Contact updatedContact = contactRepository.findById(contact.getId()).orElseThrow();
        assertTrue(updatedContact.getIsFavorite());
    }

    @Test
    void testRemoveFromFavorites_Success() {
        Contact contact = Contact.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .build();

        contact.setIsFavorite(true);
        contactRepository.save(contact);

        contactService.removeFromFavorites(contact.getId());

        Contact updatedContact = contactRepository.findById(contact.getId()).orElseThrow();
        assertFalse(updatedContact.getIsFavorite());
    }

    @Test
    void testFindFavorites_Success() {
        Contact contact1 = Contact.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .isFavorite(true)
                .build();

        Contact contact2 = Contact.builder()
                .firstName("Анна")
                .lastName("Петрова")
                .build();

        contactRepository.save(contact1);
        contactRepository.save(contact2);

        List<Contact> favorites = contactService.findFavorites();

        assertFalse(favorites.isEmpty());
        assertTrue(favorites.stream().allMatch(Contact::getIsFavorite));
        assertTrue(favorites.stream().anyMatch(c -> c.getFirstName().equals("Иван")));
    }

    @Test
    void testFindBirthdaysThisMonth_Success() {
        Contact contact = Contact.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .build();

        contactRepository.save(contact);

        Event event = Event.builder()
                .contact(contact)
                .eventType(EventType.BIRTHDAY)
                .eventDate(LocalDate.now())
                .build();

        eventRepository.save(event);

        List<Contact> birthdayContacts = contactService.findBirthdaysThisMonth();

        assertFalse(birthdayContacts.isEmpty());
        assertEquals(contact.getId(), birthdayContacts.getFirst().getId());
    }

    @Test
    void testFindBirthdaysThisMonth_NoBirthdays() {
        Contact contact = Contact.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .build();

        contactRepository.save(contact);

        List<Contact> birthdayContacts = contactService.findBirthdaysThisMonth();

        assertTrue(birthdayContacts.isEmpty());
    }
}