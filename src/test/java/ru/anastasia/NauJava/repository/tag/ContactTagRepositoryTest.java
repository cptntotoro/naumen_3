package ru.anastasia.NauJava.repository.tag;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.entity.tag.ContactTag;
import ru.anastasia.NauJava.entity.tag.Tag;
import ru.anastasia.NauJava.repository.contact.ContactRepository;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class ContactTagRepositoryTest {

    @Autowired
    private ContactTagRepository contactTagRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private TagRepository tagRepository;

    @Test
    void testFindByContactId_Success() {
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

        List<ContactTag> contactTags = contactTagRepository.findByContactId(contact.getId());

        assertFalse(contactTags.isEmpty());
        assertEquals(tagName, contactTags.getFirst().getTag().getName());
        assertEquals(contact.getId(), contactTags.getFirst().getContact().getId());
    }

    @Test
    void testFindByContactId_NoTags() {
        Contact contact = Contact.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .build();
        
        contactRepository.save(contact);

        List<ContactTag> contactTags = contactTagRepository.findByContactId(contact.getId());

        assertTrue(contactTags.isEmpty());
    }
}
