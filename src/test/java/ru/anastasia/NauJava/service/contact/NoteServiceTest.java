package ru.anastasia.NauJava.service.contact;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.entity.contact.Note;
import ru.anastasia.NauJava.repository.contact.ContactRepository;
import ru.anastasia.NauJava.repository.contact.NoteRepository;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class NoteServiceTest {

    @Autowired
    private NoteService noteService;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Test
    void testCreate_Success() {
        Contact contact = new Contact("Иван", "Иванов");
        contactRepository.save(contact);

        String content = "Заметка о встрече" + UUID.randomUUID();
        Note note = noteService.create(contact.getId(), content);

        assertNotNull(note.getId());
        assertEquals(content, note.getContent());
        assertEquals(contact.getId(), note.getContact().getId());
        assertTrue(noteRepository.findById(note.getId()).isPresent());
    }

    @Test
    void testCreate_ContactNotFound() {
        Long nonExistentContactId = 999L;
        String content = "Заметка о встрече" + UUID.randomUUID();

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                noteService.create(nonExistentContactId, content));

        assertEquals("Не найден контакт с id: " + nonExistentContactId, exception.getMessage());
    }

    @Test
    void testFindByContactId_Success() {
        Contact contact = new Contact("Иван", "Иванов");
        contactRepository.save(contact);

        String content = "Заметка о встрече" + UUID.randomUUID();
        noteService.create(contact.getId(), content);

        List<Note> notes = noteService.findByContactId(contact.getId());

        assertFalse(notes.isEmpty());
        assertEquals(content, notes.getFirst().getContent());
        assertEquals(contact.getId(), notes.getFirst().getContact().getId());
    }

    @Test
    void testFindByContactId_NoNotes() {
        Contact contact = new Contact("Иван", "Иванов");
        contactRepository.save(contact);

        List<Note> notes = noteService.findByContactId(contact.getId());

        assertTrue(notes.isEmpty());
    }
}
