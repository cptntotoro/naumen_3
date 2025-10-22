package ru.anastasia.NauJava.service.tag;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.entity.tag.ContactTag;
import ru.anastasia.NauJava.entity.tag.Tag;
import ru.anastasia.NauJava.repository.contact.ContactRepository;
import ru.anastasia.NauJava.repository.tag.ContactTagRepository;
import ru.anastasia.NauJava.repository.tag.TagRepository;

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
class TagServiceTest {

    @Autowired
    private TagService tagService;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ContactTagRepository contactTagRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Test
    void testCreate_Success() {
        String tagName = "Друг" + UUID.randomUUID();
        String color = "#FF0000";

        Tag tag = tagService.create(tagName, color);

        assertNotNull(tag.getId());
        assertEquals(tagName, tag.getName());
        assertEquals(color, tag.getColor());
        assertTrue(tagRepository.findByName(tagName).isPresent());
    }

    @Test
    void testCreate_ExistingTag() {
        String tagName = "Коллега" + UUID.randomUUID();
        String color = "#00FF00";

        Tag tag1 = tagService.create(tagName, color);
        Tag tag2 = tagService.create(tagName, "#0000FF");

        assertEquals(tag1.getId(), tag2.getId());
        assertEquals(tagName, tag2.getName());
        assertEquals(color, tag2.getColor());
    }

    @Test
    void testFindByName_Success() {
        String tagName = "Работа" + UUID.randomUUID();
        String color = "#0000FF";
        tagService.create(tagName, color);

        Tag foundTag = tagService.findByName(tagName);

        assertNotNull(foundTag);
        assertEquals(tagName, foundTag.getName());
        assertEquals(color, foundTag.getColor());
    }

    @Test
    void testFindByName_NotFound() {
        String tagName = "Несуществующий" + UUID.randomUUID();

        Tag foundTag = tagService.findByName(tagName);

        assertNull(foundTag);
    }

    @Test
    void testAddToContact_Success() {
        Contact contact = new Contact("Иван", "Иванов");
        contactRepository.save(contact);

        String tagName = "Коллега" + UUID.randomUUID();

        ContactTag contactTag = tagService.addToContact(contact.getId(), tagName);

        assertNotNull(contactTag.getId());
        assertEquals(contact.getId(), contactTag.getContact().getId());
        assertEquals(tagName, contactTag.getTag().getName());
        assertEquals("#808080", contactTag.getTag().getColor());
        assertTrue(contactTagRepository.findById(contactTag.getId()).isPresent());
    }

    @Test
    void testAddToContact_ContactNotFound() {
        Long nonExistentContactId = 999L;
        String tagName = "Друг" + UUID.randomUUID();

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                tagService.addToContact(nonExistentContactId, tagName));

        assertEquals("Не найден контакт с id: " + nonExistentContactId, exception.getMessage());
    }

    @Test
    void testFindByContactId_Success() {
        Contact contact = new Contact("Иван", "Иванов");
        contactRepository.save(contact);

        String tagName = "Работа" + UUID.randomUUID();
        tagService.addToContact(contact.getId(), tagName);

        List<Tag> tags = tagService.findByContactId(contact.getId());

        assertFalse(tags.isEmpty());
        assertEquals(tagName, tags.getFirst().getName());
    }

    @Test
    void testFindByContactId_NoTags() {
        Contact contact = new Contact("Иван", "Иванов");
        contactRepository.save(contact);

        List<Tag> tags = tagService.findByContactId(contact.getId());

        assertTrue(tags.isEmpty());
    }
}
