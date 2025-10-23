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

        Contact contact = Contact.builder()
                .firstName(firstName)
                .lastName(lastName)
                .displayName(displayName)
                .build();

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
        Contact favoriteContact = Contact.builder()
                .firstName("Favorite")
                .lastName("Contact")
                .isFavorite(true)
                .build();

        contactRepository.save(favoriteContact);

        Contact regularContact = Contact.builder()
                .firstName("Regular")
                .lastName("Contact")
                .isFavorite(false)
                .build();

        contactRepository.save(regularContact);

        List<Contact> favorites = contactRepository.findByIsFavoriteTrue();

        Assertions.assertNotNull(favorites);
        Assertions.assertFalse(favorites.isEmpty());
        favorites.forEach(contact ->
                Assertions.assertTrue(contact.getIsFavorite())
        );
    }
}