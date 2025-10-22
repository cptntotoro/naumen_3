package ru.anastasia.NauJava.repository.contact.custom;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.repository.contact.ContactRepository;

import java.util.List;
import java.util.UUID;

@SpringBootTest
@Transactional
class ContactRepositoryCustomTest {

    private final ContactRepository contactRepository;

    @Autowired
    ContactRepositoryCustomTest(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @Test
    void testFindByFirstNameAndLastNameOrDisplayNameCriteria() {
        String firstName = "Criteria" + UUID.randomUUID();
        String lastName = "Test" + UUID.randomUUID();
        String displayName = "CustomDisplay" + UUID.randomUUID();

        Contact contact = new Contact();
        contact.setFirstName(firstName);
        contact.setLastName(lastName);
        contact.setDisplayName(displayName);
        contactRepository.save(contact);

        List<Contact> foundContacts = contactRepository
                .findByFirstNameAndLastNameOrDisplayNameCriteria(firstName, lastName, "NonExistent");

        Assertions.assertFalse(foundContacts.isEmpty());
        Assertions.assertEquals(firstName, foundContacts.getFirst().getFirstName());
    }

    @Test
    void testFindContactsByComplexCriteria() {
        String firstName = "Complex" + UUID.randomUUID();
        String lastName = "Search" + UUID.randomUUID();

        Contact contact = new Contact();
        contact.setFirstName(firstName);
        contact.setLastName(lastName);
        contactRepository.save(contact);

        List<Contact> foundContacts = contactRepository
                .findContactsByComplexCriteria(firstName, null, null, null);

        Assertions.assertFalse(foundContacts.isEmpty());
        Assertions.assertEquals(firstName, foundContacts.getFirst().getFirstName());
    }
}