package ru.anastasia.NauJava.repository.tag;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.anastasia.NauJava.entity.tag.Tag;
import ru.anastasia.NauJava.repository.contact.ContactRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class TagRepositoryTest {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ContactRepository contactRepository;

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
}
