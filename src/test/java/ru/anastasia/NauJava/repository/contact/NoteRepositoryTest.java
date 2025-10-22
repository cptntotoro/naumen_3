package ru.anastasia.NauJava.repository.contact;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.entity.contact.Note;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class NoteRepositoryTest {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Test
    void testFindByContactId_Success() {
        Contact contact = new Contact("Иван", "Иванов");
        contactRepository.save(contact);

        Note note = new Note();
        note.setContact(contact);
        note.setContent("Встреча в офисе");
        noteRepository.save(note);

        List<Note> notes = noteRepository.findByContactId(contact.getId());

        assertFalse(notes.isEmpty());
        assertEquals("Встреча в офисе", notes.getFirst().getContent());
        assertEquals(contact.getId(), notes.getFirst().getContact().getId());
    }

    @Test
    void testFindByContactId_NoNotes() {
        Contact contact = new Contact("Иван", "Иванов");
        contactRepository.save(contact);

        List<Note> notes = noteRepository.findByContactId(contact.getId());

        assertTrue(notes.isEmpty());
    }

    @Test
    void testFindByContentContainingIgnoreCase_Success() {
        Contact contact = new Contact("Иван", "Иванов");
        contactRepository.save(contact);

        Note note = new Note();
        note.setContact(contact);
        note.setContent("Заметка о встрече" + UUID.randomUUID());
        noteRepository.save(note);

        List<Note> notes = noteRepository.findByContentContainingIgnoreCase("встрече");

        assertFalse(notes.isEmpty());
        assertTrue(notes.getFirst().getContent().contains("Заметка о встрече"));
    }

    @Test
    void testFindByContentContainingIgnoreCase_NoMatches() {
        Contact contact = new Contact("Иван", "Иванов");
        contactRepository.save(contact);

        Note note = new Note();
        note.setContact(contact);
        note.setContent("Заметка о встрече" + UUID.randomUUID());
        noteRepository.save(note);

        List<Note> notes = noteRepository.findByContentContainingIgnoreCase("несуществующий текст");

        assertTrue(notes.isEmpty());
    }
}
