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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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

    private Contact createTestContact() {
        Contact contact = new Contact();
        contact.setId(1L);
        contact.setFirstName("Иван");
        contact.setLastName("Петров");
        return contact;
    }

    @Test
    void createSuccess() {
        Tag tag = Tag.builder()
                .name("важный")
                .color("#FF0000")
                .description("важные контакты")
                .build();

        Tag savedTag = Tag.builder()
                .id(1L)
                .name("важный")
                .color("#FF0000")
                .description("важные контакты")
                .build();

        when(tagRepository.save(any(Tag.class))).thenReturn(savedTag);

        Tag result = tagService.create(tag);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("важный", result.getName());
        assertEquals("#FF0000", result.getColor());
        verify(tagRepository, times(1)).save(tag);
    }

    @Test
    void createWithDuplicateNameThrowsException() {
        Tag tag = Tag.builder()
                .name("существующий")
                .color("#0000FF")
                .build();

        when(tagRepository.save(any(Tag.class))).thenThrow(new DataIntegrityViolationException("Duplicate"));

        assertThrows(IllegalTagStateException.class, () -> tagService.create(tag));
        verify(tagRepository, times(1)).save(tag);
    }

    @Test
    void findByNameSuccess() {
        String tagName = "работа";
        Tag tag = Tag.builder()
                .id(1L)
                .name(tagName)
                .build();

        when(tagRepository.findByName(tagName)).thenReturn(Optional.of(tag));

        Tag result = tagService.findByName(tagName);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("работа", result.getName());
        verify(tagRepository, times(1)).findByName(tagName);
    }

    @Test
    void findByNameNotFoundThrowsException() {
        String tagName = "несуществующий";

        when(tagRepository.findByName(tagName)).thenReturn(Optional.empty());

        assertThrows(TagNotFoundException.class, () -> tagService.findByName(tagName));
        verify(tagRepository, times(1)).findByName(tagName);
    }

    @Test
    void addToContactWithExistingTagSuccess() {
        Long contactId = 1L;
        String tagName = "друзья";
        Contact contact = createTestContact();

        Tag existingTag = Tag.builder()
                .id(2L)
                .name(tagName)
                .build();

        ContactTag contactTag = ContactTag.builder()
                .id(3L)
                .contact(contact)
                .tag(existingTag)
                .build();

        when(contactService.findById(contactId)).thenReturn(contact);
        when(tagRepository.findByName(tagName)).thenReturn(Optional.of(existingTag));
        when(contactTagRepository.existsByContactIdAndTagId(contactId, existingTag.getId())).thenReturn(false);
        when(contactTagRepository.save(any(ContactTag.class))).thenReturn(contactTag);

        ContactTag result = tagService.addToContact(contactId, tagName);

        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals(contact, result.getContact());
        assertEquals(existingTag, result.getTag());
        verify(contactService, times(1)).findById(contactId);
        verify(tagRepository, times(1)).findByName(tagName);
        verify(contactTagRepository, times(1)).existsByContactIdAndTagId(contactId, existingTag.getId());
        verify(contactTagRepository, times(1)).save(any(ContactTag.class));
    }

    @Test
    void addToContactWhenTagNotFoundThrowsException() {
        Long contactId = 1L;
        String tagName = "новая метка";
        Contact contact = createTestContact();

        when(contactService.findById(contactId)).thenReturn(contact);
        when(tagRepository.findByName(tagName)).thenReturn(Optional.empty());

        assertThrows(TagNotFoundException.class, () -> tagService.addToContact(contactId, tagName));
        verify(contactTagRepository, never()).save(any(ContactTag.class));
    }

    @Test
    void addToContactWhenAlreadyExistsThrowsException() {
        Long contactId = 1L;
        String tagName = "семья";
        Contact contact = createTestContact();

        Tag tag = Tag.builder()
                .id(2L)
                .name(tagName)
                .build();

        when(contactService.findById(contactId)).thenReturn(contact);
        when(tagRepository.findByName(tagName)).thenReturn(Optional.of(tag));
        when(contactTagRepository.existsByContactIdAndTagId(contactId, tag.getId())).thenReturn(true);

        assertThrows(IllegalTagStateException.class, () -> tagService.addToContact(contactId, tagName));
        verify(contactTagRepository, never()).save(any(ContactTag.class));
    }

    @Test
    void findByContactIdSuccess() {
        Long contactId = 1L;
        Tag tag1 = Tag.builder().id(1L).name("работа").build();
        Tag tag2 = Tag.builder().id(2L).name("друзья").build();

        ContactTag contactTag1 = ContactTag.builder().id(10L).tag(tag1).build();
        ContactTag contactTag2 = ContactTag.builder().id(11L).tag(tag2).build();

        when(contactTagRepository.findByContactId(contactId)).thenReturn(List.of(contactTag1, contactTag2));

        List<Tag> result = tagService.findByContactId(contactId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("работа", result.get(0).getName());
        assertEquals("друзья", result.get(1).getName());
        verify(contactTagRepository, times(1)).findByContactId(contactId);
    }

    @Test
    void findAllSuccess() {
        Tag tag1 = Tag.builder().id(1L).name("метка1").build();
        Tag tag2 = Tag.builder().id(2L).name("метка2").build();

        when(tagRepository.findAll()).thenReturn(List.of(tag1, tag2));

        List<Tag> result = tagService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(tagRepository, times(1)).findAll();
    }

    @Test
    void findByIdSuccess() {
        Long tagId = 1L;
        Tag tag = Tag.builder()
                .id(tagId)
                .name("найденный")
                .build();

        when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));

        Tag result = tagService.findById(tagId);

        assertNotNull(result);
        assertEquals(tagId, result.getId());
        assertEquals("найденный", result.getName());
        verify(tagRepository, times(1)).findById(tagId);
    }

    @Test
    void findByIdNotFoundThrowsException() {
        Long tagId = 999L;

        when(tagRepository.findById(tagId)).thenReturn(Optional.empty());

        assertThrows(TagNotFoundException.class, () -> tagService.findById(tagId));
        verify(tagRepository, times(1)).findById(tagId);
    }

    @Test
    void findAllByIdSuccess() {
        List<Long> tagIds = Arrays.asList(1L, 2L, 3L);
        Tag tag1 = Tag.builder().id(1L).name("тег1").build();
        Tag tag2 = Tag.builder().id(2L).name("тег2").build();
        Tag tag3 = Tag.builder().id(3L).name("тег3").build();

        when(tagRepository.getTagsByIdIsIn(tagIds)).thenReturn(List.of(tag1, tag2, tag3));

        List<Tag> result = tagService.findAllById(tagIds);

        assertNotNull(result);
        assertEquals(3, result.size());
        verify(tagRepository, times(1)).getTagsByIdIsIn(tagIds);
    }

    @Test
    void updateSuccess() {
        Long tagId = 1L;
        Tag existingTag = Tag.builder()
                .id(tagId)
                .name("старое имя")
                .color("#000000")
                .description("старое описание")
                .build();

        Tag updatedTag = Tag.builder()
                .id(tagId)
                .name("новое имя")
                .color("#FFFFFF")
                .description("новое описание")
                .build();

        when(tagRepository.findById(tagId)).thenReturn(Optional.of(existingTag));
        when(tagRepository.save(any(Tag.class))).thenReturn(updatedTag);

        Tag result = tagService.update(updatedTag);

        assertNotNull(result);
        assertEquals("новое имя", result.getName());
        assertEquals("#FFFFFF", result.getColor());
        assertEquals("новое описание", result.getDescription());
        verify(tagRepository, times(1)).findById(tagId);
        verify(tagRepository, times(1)).save(existingTag);
    }

    @Test
    void updateWithDuplicateNameThrowsException() {
        Long tagId = 1L;
        Tag existingTag = Tag.builder()
                .id(tagId)
                .name("старое")
                .build();

        Tag updatedTag = Tag.builder()
                .id(tagId)
                .name("дубликат")
                .build();

        when(tagRepository.findById(tagId)).thenReturn(Optional.of(existingTag));
        when(tagRepository.save(any(Tag.class))).thenThrow(new DataIntegrityViolationException("Duplicate"));

        assertThrows(IllegalTagStateException.class, () -> tagService.update(updatedTag));
        verify(tagRepository, times(1)).findById(tagId);
        verify(tagRepository, times(1)).save(existingTag);
    }

    @Test
    void deleteSuccess() {
        Long tagId = 1L;
        Tag tag = Tag.builder()
                .id(tagId)
                .name("тег для удаления")
                .build();

        when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));

        tagService.delete(tagId);

        verify(tagRepository, times(1)).findById(tagId);
        verify(tagRepository, times(1)).deleteById(tagId);
    }

    @Test
    void deleteNotFoundThrowsException() {
        Long tagId = 999L;

        when(tagRepository.findById(tagId)).thenReturn(Optional.empty());

        assertThrows(TagNotFoundException.class, () -> tagService.delete(tagId));
        verify(tagRepository, times(1)).findById(tagId);
        verify(tagRepository, never()).deleteById(tagId);
    }

    @Test
    void findContactTagsByContactIdReturnsEmptyList() {
        Long contactId = 1L;

        when(contactTagRepository.findByContactId(contactId)).thenReturn(List.of());

        List<ContactTag> result = tagService.findContactTagsByContactId(contactId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(contactTagRepository, times(1)).findByContactId(contactId);
    }

    @Test
    void findContactTagsByContactIdSuccess() {
        Long contactId = 1L;
        ContactTag contactTag = ContactTag.builder()
                .id(1L)
                .contact(createTestContact())
                .tag(Tag.builder().id(1L).name("тест").build())
                .build();

        when(contactTagRepository.findByContactId(contactId)).thenReturn(List.of(contactTag));

        List<ContactTag> result = tagService.findContactTagsByContactId(contactId);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(contactTagRepository, times(1)).findByContactId(contactId);
    }

    @Test
    void deleteContactTagSuccess() {
        Long contactTagId = 1L;
        ContactTag contactTag = ContactTag.builder()
                .id(contactTagId)
                .contact(createTestContact())
                .tag(Tag.builder().id(1L).name("тест").build())
                .build();

        when(contactTagRepository.findById(contactTagId)).thenReturn(Optional.of(contactTag));

        tagService.deleteContactTag(contactTagId);

        verify(contactTagRepository, times(1)).findById(contactTagId);
        verify(contactTagRepository, times(1)).deleteById(contactTagId);
    }

    @Test
    void deleteContactTagNotFoundThrowsException() {
        Long contactTagId = 999L;

        when(contactTagRepository.findById(contactTagId)).thenReturn(Optional.empty());

        assertThrows(TagNotFoundException.class, () -> tagService.deleteContactTag(contactTagId));
        verify(contactTagRepository, times(1)).findById(contactTagId);
        verify(contactTagRepository, never()).deleteById(contactTagId);
    }
}