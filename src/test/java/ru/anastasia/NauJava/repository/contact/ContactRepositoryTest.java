package ru.anastasia.NauJava.repository.contact;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.anastasia.NauJava.entity.contact.Contact;

import java.util.List;
import java.util.UUID;

@SpringBootTest
@Transactional
class ContactRepositoryTest {

    private final ContactRepository contactRepository;

    @Autowired
    ContactRepositoryTest(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @Test
    void testFindByFirstNameAndLastNameOrDisplayName() {
        String firstName = "TestFirstName" + UUID.randomUUID();
        String lastName = "TestLastName" + UUID.randomUUID();
        String displayName = "CustomDisplay" + UUID.randomUUID();

        Contact contact = new Contact();
        contact.setFirstName(firstName);
        contact.setLastName(lastName);
        contact.setDisplayName(displayName);
        contactRepository.save(contact);

        List<Contact> foundContacts = contactRepository
                .findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCaseOrDisplayNameContainingIgnoreCase(
                        firstName, lastName, "NonExistentDisplay");

        Assertions.assertNotNull(foundContacts);
        Assertions.assertFalse(foundContacts.isEmpty());
        Assertions.assertEquals(1, foundContacts.size());
        Assertions.assertEquals(firstName, foundContacts.getFirst().getFirstName());
        Assertions.assertEquals(lastName, foundContacts.getFirst().getLastName());
    }

    @Test
    void testFindByCompanyName() {
        String companyName = "TestCompany" + UUID.randomUUID();

        List<Contact> contacts = contactRepository.findByCompanyName(companyName);

        Assertions.assertNotNull(contacts);
    }

    @Test
    void testFindByIsFavoriteTrue() {
        Contact favoriteContact = new Contact();
        favoriteContact.setFirstName("Favorite");
        favoriteContact.setLastName("Contact");
        favoriteContact.setIsFavorite(true);
        contactRepository.save(favoriteContact);

        Contact regularContact = new Contact();
        regularContact.setFirstName("Regular");
        regularContact.setLastName("Contact");
        regularContact.setIsFavorite(false);
        contactRepository.save(regularContact);

        List<Contact> favorites = contactRepository.findByIsFavoriteTrue();

        Assertions.assertNotNull(favorites);
        Assertions.assertFalse(favorites.isEmpty());
        favorites.forEach(contact ->
                Assertions.assertTrue(contact.getIsFavorite())
        );
    }
}