package ru.anastasia.NauJava.service.tag;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.entity.tag.ContactTag;
import ru.anastasia.NauJava.entity.tag.Tag;
import ru.anastasia.NauJava.exception.tag.IllegalTagStateException;
import ru.anastasia.NauJava.exception.tag.TagNotFoundException;
import ru.anastasia.NauJava.repository.tag.ContactTagRepository;
import ru.anastasia.NauJava.repository.tag.TagRepository;
import ru.anastasia.NauJava.service.contact.ContactService;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @Mock
    private ContactTagRepository contactTagRepository;

    @Mock
    private ContactService contactService;

    @InjectMocks
    private TagServiceImpl tagService;

    @Test
    void create_ShouldReturnSavedTag() {
        Tag tag = Tag.builder().name("friend").color("#FF0000").build();
        Tag savedTag = Tag.builder().id(1L).name("friend").color("#FF0000").build();

        when(tagRepository.save(tag)).thenReturn(savedTag);

        Tag result = tagService.create(tag);

        assertNotNull(result.getId());
        assertEquals(savedTag, result);
        verify(tagRepository).save(tag);
    }

    @Test
    void create_ShouldThrowIllegalTagStateException_WhenTagNameAlreadyExists() {
        Tag tag = Tag.builder().name("existing").build();

        when(tagRepository.save(tag)).thenThrow(DataIntegrityViolationException.class);

        IllegalTagStateException exception = assertThrows(
                IllegalTagStateException.class,
                () -> tagService.create(tag)
        );

        assertTrue(exception.getMessage().contains("Тег с таким именем уже существует"));
        verify(tagRepository).save(tag);
    }

    @Test
    void findByName_ShouldReturnTag_WhenExists() {
        String tagName = "friend";
        Tag tag = Tag.builder().id(1L).name(tagName).build();

        when(tagRepository.findByName(tagName)).thenReturn(Optional.of(tag));

        Tag result = tagService.findByName(tagName);

        assertEquals(tag, result);
        verify(tagRepository).findByName(tagName);
    }

    @Test
    void findByName_ShouldThrowTagNotFoundException_WhenNotExists() {
        String tagName = "nonexistent";

        when(tagRepository.findByName(tagName)).thenReturn(Optional.empty());

        TagNotFoundException exception = assertThrows(
                TagNotFoundException.class,
                () -> tagService.findByName(tagName)
        );

        assertTrue(exception.getMessage().contains("Тег не найден: " + tagName));
        verify(tagRepository).findByName(tagName);
    }

    @Test
    void addToContact_ShouldReturnContactTag_WhenTagExists() {
        Long contactId = 1L;
        String tagName = "friend";
        Contact contact = Contact.builder().id(contactId).build();
        Tag tag = Tag.builder().id(1L).name(tagName).build();
        ContactTag contactTag = ContactTag.builder().id(1L).contact(contact).tag(tag).build();

        when(contactService.findById(contactId)).thenReturn(contact);
        when(tagRepository.findByName(tagName)).thenReturn(Optional.of(tag));
        when(contactTagRepository.existsByContactIdAndTagId(contactId, tag.getId())).thenReturn(false);
        when(contactTagRepository.save(any(ContactTag.class))).thenReturn(contactTag);

        ContactTag result = tagService.addToContact(contactId, tagName);

        assertNotNull(result.getId());
        assertEquals(contact, result.getContact());
        assertEquals(tag, result.getTag());
        verify(contactService).findById(contactId);
        verify(tagRepository).findByName(tagName);
        verify(contactTagRepository).existsByContactIdAndTagId(contactId, tag.getId());
        verify(contactTagRepository).save(any(ContactTag.class));
    }

    @Test
    void addToContact_ShouldCreateNewTag_WhenTagNotExists() {
        Long contactId = 1L;
        String tagName = "newtag";
        Contact contact = Contact.builder().id(contactId).build();
        Tag newTag = Tag.builder().id(1L).name(tagName).color("#808080").build();
        ContactTag contactTag = ContactTag.builder().id(1L).contact(contact).tag(newTag).build();

        when(contactService.findById(contactId)).thenReturn(contact);
        when(tagRepository.findByName(tagName)).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenReturn(newTag);
        when(contactTagRepository.existsByContactIdAndTagId(contactId, newTag.getId())).thenReturn(false);
        when(contactTagRepository.save(any(ContactTag.class))).thenReturn(contactTag);

        ContactTag result = tagService.addToContact(contactId, tagName);

        assertNotNull(result.getId());
        verify(tagRepository).findByName(tagName);
        verify(tagRepository).save(any(Tag.class));
        verify(contactTagRepository).save(any(ContactTag.class));
    }

    @Test
    void addToContact_ShouldThrowIllegalTagStateException_WhenTagAlreadyAttached() {
        Long contactId = 1L;
        String tagName = "friend";
        Contact contact = Contact.builder().id(contactId).build();
        Tag tag = Tag.builder().id(1L).name(tagName).build();

        when(contactService.findById(contactId)).thenReturn(contact);
        when(tagRepository.findByName(tagName)).thenReturn(Optional.of(tag));
        when(contactTagRepository.existsByContactIdAndTagId(contactId, tag.getId())).thenReturn(true);

        IllegalTagStateException exception = assertThrows(
                IllegalTagStateException.class,
                () -> tagService.addToContact(contactId, tagName)
        );

        assertTrue(exception.getMessage().contains("Тег уже привязан к контакту"));
        verify(contactTagRepository, never()).save(any(ContactTag.class));
    }

    @Test
    void findByContactId_ShouldReturnTags() {
        Long contactId = 1L;
        Tag tag1 = Tag.builder().id(1L).name("friend").build();
        Tag tag2 = Tag.builder().id(2L).name("work").build();
        ContactTag contactTag1 = ContactTag.builder().id(1L).tag(tag1).build();
        ContactTag contactTag2 = ContactTag.builder().id(2L).tag(tag2).build();
        List<ContactTag> contactTags = List.of(contactTag1, contactTag2);

        when(contactTagRepository.findByContactId(contactId)).thenReturn(contactTags);

        List<Tag> result = tagService.findByContactId(contactId);

        assertEquals(2, result.size());
        assertTrue(result.contains(tag1));
        assertTrue(result.contains(tag2));
        verify(contactTagRepository).findByContactId(contactId);
    }

    @Test
    void findAll_ShouldReturnAllTags() {
        Tag tag1 = Tag.builder().id(1L).build();
        Tag tag2 = Tag.builder().id(2L).build();
        List<Tag> tags = List.of(tag1, tag2);

        when(tagRepository.findAll()).thenReturn(tags);

        List<Tag> result = tagService.findAll();

        assertEquals(2, result.size());
        verify(tagRepository).findAll();
    }

    @Test
    void findById_ShouldReturnTag_WhenExists() {
        Long id = 1L;
        Tag tag = Tag.builder().id(id).name("friend").build();

        when(tagRepository.findById(id)).thenReturn(Optional.of(tag));

        Tag result = tagService.findById(id);

        assertEquals(tag, result);
        verify(tagRepository).findById(id);
    }

    @Test
    void findById_ShouldThrowTagNotFoundException_WhenNotExists() {
        Long id = 999L;

        when(tagRepository.findById(id)).thenReturn(Optional.empty());

        TagNotFoundException exception = assertThrows(
                TagNotFoundException.class,
                () -> tagService.findById(id)
        );

        assertTrue(exception.getMessage().contains("Тег не найден с id: " + id));
        verify(tagRepository).findById(id);
    }

    @Test
    void findAllById_ShouldReturnTags() {
        Set<Long> ids = Set.of(1L, 2L);
        Tag tag1 = Tag.builder().id(1L).build();
        Tag tag2 = Tag.builder().id(2L).build();
        List<Tag> expectedTags = List.of(tag1, tag2);

        when(tagRepository.getTagsByIdIsIn(ids)).thenReturn(expectedTags);

        List<Tag> result = tagService.findAllById(ids);

        assertEquals(expectedTags, result);
        verify(tagRepository).getTagsByIdIsIn(ids);
    }

    @Test
    void update_ShouldReturnUpdatedTag_WhenExists() {
        Long id = 1L;
        Tag existingTag = Tag.builder()
                .id(id)
                .name("old")
                .color("#000000")
                .description("old desc")
                .build();
        Tag updateTag = Tag.builder()
                .id(id)
                .name("new")
                .color("#FFFFFF")
                .description("new desc")
                .build();
        Tag updatedTag = Tag.builder()
                .id(id)
                .name("new")
                .color("#FFFFFF")
                .description("new desc")
                .build();

        when(tagRepository.findById(id)).thenReturn(Optional.of(existingTag));
        when(tagRepository.save(existingTag)).thenReturn(updatedTag);

        Tag result = tagService.update(updateTag);

        assertEquals("new", result.getName());
        assertEquals("#FFFFFF", result.getColor());
        assertEquals("new desc", result.getDescription());
        verify(tagRepository).findById(id);
        verify(tagRepository).save(existingTag);
    }

    @Test
    void update_ShouldThrowTagNotFoundException_WhenNotExists() {
        Tag tag = Tag.builder().id(999L).build();

        when(tagRepository.findById(999L)).thenReturn(Optional.empty());

        TagNotFoundException exception = assertThrows(
                TagNotFoundException.class,
                () -> tagService.update(tag)
        );

        assertTrue(exception.getMessage().contains("Тег не найден с id: " + tag.getId()));
        verify(tagRepository).findById(999L);
        verify(tagRepository, never()).save(any(Tag.class));
    }

    @Test
    void update_ShouldThrowIllegalTagStateException_WhenTagNameAlreadyExists() {
        Long id = 1L;
        Tag existingTag = Tag.builder().id(id).name("old").build();
        Tag updateTag = Tag.builder().id(id).name("existing").build();

        when(tagRepository.findById(id)).thenReturn(Optional.of(existingTag));
        when(tagRepository.save(existingTag)).thenThrow(DataIntegrityViolationException.class);

        IllegalTagStateException exception = assertThrows(
                IllegalTagStateException.class,
                () -> tagService.update(updateTag)
        );

        assertTrue(exception.getMessage().contains("Тег с таким именем уже существует"));
        verify(tagRepository).findById(id);
        verify(tagRepository).save(existingTag);
    }

    @Test
    void delete_ShouldCallRepositoryDelete() {
        Long id = 1L;
        doNothing().when(tagRepository).deleteById(id);

        tagService.delete(id);

        verify(tagRepository).deleteById(id);
    }

    @Test
    void findContactTagsByContactId_ShouldReturnEmptyList() {
        Long contactId = 1L;

        List<ContactTag> result = tagService.findContactTagsByContactId(contactId);

        assertTrue(result.isEmpty());
    }

    @Test
    void deleteContactTag_ShouldCallRepositoryDelete() {
        Long contactTagId = 1L;
        doNothing().when(contactTagRepository).deleteById(contactTagId);

        tagService.deleteContactTag(contactTagId);

        verify(contactTagRepository).deleteById(contactTagId);
    }
}