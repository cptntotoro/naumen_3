package ru.anastasia.NauJava.service.facade;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.entity.tag.ContactTag;
import ru.anastasia.NauJava.entity.tag.Tag;
import ru.anastasia.NauJava.exception.contact.ContactNotFoundException;
import ru.anastasia.NauJava.service.contact.ContactService;
import ru.anastasia.NauJava.service.facade.impl.ContactTagFacadeServiceImpl;
import ru.anastasia.NauJava.service.tag.TagService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ContactTagFacadeServiceTest {

    @Mock
    private ContactService contactService;

    @Mock
    private TagService tagService;

    @InjectMocks
    private ContactTagFacadeServiceImpl contactTagFacadeService;

    private Contact createTestContact() {
        return Contact.builder()
                .id(1L)
                .firstName("Иван")
                .lastName("Иванов")
                .build();
    }

    private Tag createTestTag() {
        return Tag.builder()
                .id(1L)
                .name("друзья")
                .build();
    }

    private ContactTag createTestContactTag() {
        return ContactTag.builder()
                .id(1L)
                .contact(createTestContact())
                .tag(createTestTag())
                .build();
    }

    @Test
    void addTagsToContact_WhenValidData_ShouldReturnCreatedContactTags() {
        Long contactId = 1L;
        Contact contact = createTestContact();
        List<String> tagNames = Arrays.asList("друзья", "работа");
        ContactTag contactTag1 = createTestContactTag();
        ContactTag contactTag2 = createTestContactTag();
        contactTag2.setId(2L);
        contactTag2.getTag().setName("работа");

        when(contactService.findById(contactId)).thenReturn(contact);
        when(tagService.addToContact(contactId, "друзья")).thenReturn(contactTag1);
        when(tagService.addToContact(contactId, "работа")).thenReturn(contactTag2);

        List<ContactTag> result = contactTagFacadeService.addTagsToContact(contactId, tagNames);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(contactService, times(1)).findById(contactId);
        verify(tagService, times(1)).addToContact(contactId, "друзья");
        verify(tagService, times(1)).addToContact(contactId, "работа");
    }

    @Test
    void addTagsToContact_WhenContactNotFound_ShouldThrowContactNotFoundException() {
        Long contactId = 1L;
        List<String> tagNames = List.of("друзья");

        when(contactService.findById(contactId))
                .thenThrow(new ContactNotFoundException("Контакт не найден"));

        ContactNotFoundException exception = assertThrows(
                ContactNotFoundException.class,
                () -> contactTagFacadeService.addTagsToContact(contactId, tagNames)
        );

        assertTrue(exception.getMessage().contains("Контакт не найден"));
        verify(contactService, times(1)).findById(contactId);
        verify(tagService, never()).addToContact(anyLong(), anyString());
    }

    @Test
    void addTagsToContact_WhenEmptyTagNames_ShouldReturnEmptyList() {
        Long contactId = 1L;
        Contact contact = createTestContact();
        List<String> emptyTagNames = List.of();

        when(contactService.findById(contactId)).thenReturn(contact);

        List<ContactTag> result = contactTagFacadeService.addTagsToContact(contactId, emptyTagNames);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(contactService, times(1)).findById(contactId);
        verify(tagService, never()).addToContact(anyLong(), anyString());
    }

    @Test
    void getContactTags_WhenContactExists_ShouldReturnContactTags() {
        Long contactId = 1L;
        Contact contact = createTestContact();
        List<ContactTag> contactTags = Arrays.asList(
                createTestContactTag(),
                createTestContactTag()
        );

        when(contactService.findById(contactId)).thenReturn(contact);
        when(tagService.findContactTagsByContactId(contactId)).thenReturn(contactTags);

        List<ContactTag> result = contactTagFacadeService.getContactTags(contactId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(contactService, times(1)).findById(contactId);
        verify(tagService, times(1)).findContactTagsByContactId(contactId);
    }

    @Test
    void getContactTags_WhenContactNotFound_ShouldThrowContactNotFoundException() {
        Long contactId = 1L;

        when(contactService.findById(contactId))
                .thenThrow(new ContactNotFoundException("Контакт не найден"));

        ContactNotFoundException exception = assertThrows(
                ContactNotFoundException.class,
                () -> contactTagFacadeService.getContactTags(contactId)
        );

        assertTrue(exception.getMessage().contains("Контакт не найден"));
        verify(contactService, times(1)).findById(contactId);
        verify(tagService, never()).findContactTagsByContactId(anyLong());
    }

    @Test
    void getContactTags_WhenNoTags_ShouldReturnEmptyList() {
        Long contactId = 1L;
        Contact contact = createTestContact();

        when(contactService.findById(contactId)).thenReturn(contact);
        when(tagService.findContactTagsByContactId(contactId)).thenReturn(List.of());

        List<ContactTag> result = contactTagFacadeService.getContactTags(contactId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(contactService, times(1)).findById(contactId);
        verify(tagService, times(1)).findContactTagsByContactId(contactId);
    }
}
