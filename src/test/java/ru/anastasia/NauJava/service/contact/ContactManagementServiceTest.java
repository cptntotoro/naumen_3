package ru.anastasia.NauJava.service.contact;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.entity.contact.ContactDetail;
import ru.anastasia.NauJava.entity.contact.Event;
import ru.anastasia.NauJava.entity.contact.SocialProfile;
import ru.anastasia.NauJava.entity.enums.DetailLabel;
import ru.anastasia.NauJava.entity.enums.DetailType;
import ru.anastasia.NauJava.entity.enums.EventType;
import ru.anastasia.NauJava.entity.enums.SocialPlatform;
import ru.anastasia.NauJava.repository.contact.ContactRepository;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class ContactManagementServiceTest {

    @Autowired
    private ContactManagementService contactManagementService;

    @Autowired
    private ContactRepository contactRepository;

    @Test
    void createWithDetails_SuccessfullyCreatesContactWithAllDetails() {
        String firstName = "Иван";
        String lastName = "Иванов";
        String company = "ООО Ромашка";
        String jobTitle = "Разработчик";

        ContactDetail detail = ContactDetail.builder()
                .detailType(DetailType.EMAIL)
                .label(DetailLabel.WORK)
                .value("ivan@example.com")
                .build();

        SocialProfile profile = SocialProfile.builder()
                .platform(SocialPlatform.TELEGRAM)
                .username("@ivanov")
                .build();

        Event event = Event.builder()
                .eventType(EventType.BIRTHDAY)
                .eventDate(LocalDate.now())
                .build();

        List<String> tagNames = List.of("друг", "коллега");
        List<String> notes = List.of("Заметка 1");

        Contact savedContact = contactManagementService.createWithDetails(
                firstName, lastName, company, jobTitle,
                List.of(detail), List.of(profile), List.of(event), tagNames, notes);

        assertNotNull(savedContact.getId());
        assertEquals(firstName, savedContact.getFirstName());
        assertEquals(lastName, savedContact.getLastName());

        Contact dbContact = contactRepository.findById(savedContact.getId()).orElseThrow();
        assertEquals(1, dbContact.getContactDetails().size());
        assertEquals(1, dbContact.getSocialProfiles().size());
        assertEquals(1, dbContact.getEvents().size());
        assertEquals(2, dbContact.getContactTags().size());
        assertEquals(1, dbContact.getNotes().size());
        assertEquals("ivan@example.com", dbContact.getContactDetails().stream().findFirst().orElseThrow().getValue());
        assertEquals("@ivanov", dbContact.getSocialProfiles().stream().findFirst().orElseThrow().getUsername());
        assertEquals(EventType.BIRTHDAY, dbContact.getEvents().stream().findFirst().orElseThrow().getEventType());
        assertTrue(dbContact.getContactTags().stream().anyMatch(ct -> ct.getTag().getName().equals("друг")));
        assertTrue(dbContact.getNotes().stream().anyMatch(n -> n.getContent().equals("Заметка 1")));
    }

    @Test
    void delete_SuccessfullyRemovesContact() {
        Contact contact = Contact.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .build();

        Contact savedContact = contactRepository.save(contact);

        contactManagementService.delete(savedContact.getId());

        assertFalse(contactRepository.findById(savedContact.getId()).isPresent());
    }

    @Test
    void delete_ThrowsExceptionForNonExistentContact() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> contactManagementService.delete(999L));
        assertEquals("Не найден контакт с id: 999", exception.getMessage());
    }

    @Test
    void duplicate_SuccessfullyCreatesCopyWithDetailsAndTags() {
        Contact original = Contact.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .build();

        String contactDetailValue = "+1234567890";

        ContactDetail detail = ContactDetail.builder()
                .detailType(DetailType.PHONE)
                .label(DetailLabel.MOBILE)
                .value(contactDetailValue)
                .build();

        original = contactManagementService.createWithDetails(
                original.getFirstName(), original.getLastName(), null, null,
                List.of(detail), null, null, List.of("друг"), null);

        String newFirstName = "Петр";
        String newLastName = "Петров";

        Contact duplicate = contactManagementService.duplicate(original.getId(), newFirstName, newLastName);

        assertNotNull(duplicate.getId());
        assertNotEquals(original.getId(), duplicate.getId());
        assertEquals(newFirstName, duplicate.getFirstName());
        assertEquals(newLastName, duplicate.getLastName());

        Contact dbDuplicate = contactRepository.findById(duplicate.getId()).orElseThrow();
        assertEquals(1, dbDuplicate.getContactDetails().size());
        assertEquals(1, dbDuplicate.getContactTags().size());
        assertEquals(contactDetailValue, dbDuplicate.getContactDetails().stream().findFirst().orElseThrow().getValue());
        assertTrue(dbDuplicate.getContactTags().stream().anyMatch(ct -> ct.getTag().getName().equals("друг")));
    }

    @Test
    void duplicate_ThrowsExceptionForNonExistentContact() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> contactManagementService.duplicate(999L, "Петр", "Петров"));
        assertEquals("Не найден контакт с id: 999", exception.getMessage());
    }

    @Test
    void searchComplex_ReturnsMatchingContacts() {
        Contact contact = Contact.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .build();

        contactRepository.save(contact);

        List<Contact> result = contactManagementService.searchComplex("Иван", "Иванов", null, null);

        assertEquals(1, result.size());
        assertEquals("Иван", result.getFirst().getFirstName());
        assertEquals("Иванов", result.getFirst().getLastName());
    }

    @Test
    void searchComplex_ReturnsEmptyListForNonMatchingCriteria() {
        Contact contact = Contact.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .build();

        contactRepository.save(contact);

        List<Contact> result = contactManagementService.searchComplex("Петр", "Петров", null, null);

        assertTrue(result.isEmpty());
    }

    @Test
    void findWithUpcomingBirthdays_ReturnsContactsWithBirthdays() {
        Contact contact = Contact.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .build();

        Event birthday = Event.builder()
                .eventType(EventType.BIRTHDAY)
                .eventDate(LocalDate.now())
                .build();

        contact = contactManagementService.createWithDetails(
                contact.getFirstName(), contact.getLastName(), null, null,
                null, null, List.of(birthday), null, null);

        List<Contact> result = contactManagementService.findWithUpcomingBirthdays(7);

        assertEquals(1, result.size());
        assertEquals(contact.getId(), result.getFirst().getId());
    }

    @Test
    void findWithUpcomingBirthdays_ReturnsEmptyListForNoBirthdays() {
        Contact contact = Contact.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .build();

        contactRepository.save(contact);

        List<Contact> result = contactManagementService.findWithUpcomingBirthdays(7);

        assertTrue(result.isEmpty());
    }

    @Test
    void updateWithDetails_SuccessfullyUpdatesContactAndDetails() {
        Contact contact = Contact.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .build();

        Contact savedContact = contactRepository.save(contact);

        ContactDetail originalDetail = ContactDetail.builder()
                .detailType(DetailType.EMAIL)
                .label(DetailLabel.WORK)
                .value("old@example.com")
                .build();

        contactManagementService.createWithDetails(
                savedContact.getFirstName(), savedContact.getLastName(), null, null,
                List.of(originalDetail), null, null, null, null);

        Contact updatedContactData = Contact.builder()
                .firstName("Петр")
                .lastName("Петров")
                .isFavorite(true)
                .build();

        ContactDetail newDetail = ContactDetail.builder()
                .detailType(DetailType.PHONE)
                .label(DetailLabel.MOBILE)
                .value("+1234567890")
                .build();

        contactManagementService.updateWithDetails(savedContact.getId(), updatedContactData, List.of(newDetail));

        Contact dbContact = contactRepository.findById(savedContact.getId()).orElseThrow();
        assertEquals("Петр", dbContact.getFirstName());
        assertEquals("Петров", dbContact.getLastName());
        assertTrue(dbContact.getIsFavorite());
        assertEquals(1, dbContact.getContactDetails().size());
        assertEquals("+1234567890", dbContact.getContactDetails().stream().findFirst().orElseThrow().getValue());
    }

    @Test
    void updateWithDetails_ThrowsExceptionForNonExistentContact() {
        Contact contactData = Contact.builder()
                .firstName("Петр")
                .lastName("Петров")
                .build();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> contactManagementService.updateWithDetails(999L, contactData, null));
        assertEquals("Не найден контакт с id: 999", exception.getMessage());
    }
}