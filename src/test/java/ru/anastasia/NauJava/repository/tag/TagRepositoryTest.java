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
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class TagRepositoryTest {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Test
    void testFindByNameContainingIgnoreCaseOrColorContainingIgnoreCase() {
        String tagName = "Important" + UUID.randomUUID();
        String color = "#FF0000";

        Tag tag = new Tag();
        tag.setName(tagName);
        tag.setColor(color);
        tagRepository.save(tag);

        List<Tag> foundByName = tagRepository.findByNameContainingIgnoreCaseOrColorContainingIgnoreCase(
                "important", "");
        List<Tag> foundByColor = tagRepository.findByNameContainingIgnoreCaseOrColorContainingIgnoreCase(
                "", "FF0000");

        assertFalse(foundByName.isEmpty());
        assertFalse(foundByColor.isEmpty());
        assertEquals(tagName, foundByName.getFirst().getName());
        assertEquals(color, foundByColor.getFirst().getColor());
    }

    @Test
    void testFindByName() {
        String tagName = "TestTag" + UUID.randomUUID();

        Tag tag = new Tag();
        tag.setName(tagName);
        tagRepository.save(tag);

        Optional<Tag> foundTag = tagRepository.findByName(tagName);

        assertTrue(foundTag.isPresent());
        assertEquals(tagName, foundTag.get().getName());
    }

    @Test
    void testFindByNameContainingIgnoreCase() {
        String tagName = "Работа" + UUID.randomUUID();
        Tag tag = new Tag();
        tag.setName(tagName);
        tagRepository.save(tag);

        List<Tag> foundTags = tagRepository.findByNameContainingIgnoreCase("работа");

        assertFalse(foundTags.isEmpty());
        assertEquals(tagName, foundTags.getFirst().getName());
    }

    @Test
    void testFindByContactId() {
        Contact contact = new Contact("Иван", "Иванов");
        contactRepository.save(contact);

        String tagName = "Коллега" + UUID.randomUUID();
        Tag tag = new Tag();
        tag.setName(tagName);
        tagRepository.save(tag);

        ContactTag contactTag = new ContactTag();
        contactTag.setContact(contact);
        contactTag.setTag(tag);
        contact.getContactTags().add(contactTag);
        tag.getContactTags().add(contactTag);
        contactRepository.save(contact);

        List<Tag> foundTags = tagRepository.findByContactId(contact.getId());

        assertFalse(foundTags.isEmpty());
        assertEquals(tagName, foundTags.getFirst().getName());
    }
}
