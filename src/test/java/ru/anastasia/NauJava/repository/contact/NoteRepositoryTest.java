package ru.anastasia.NauJava.repository.contact;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.entity.note.Note;
import ru.anastasia.NauJava.repository.note.NoteRepository;

import java.util.List;

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
        Contact contact = Contact.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .build();

        contactRepository.save(contact);

        String noteContent = "Заметка";

        Note note = Note.builder()
                .contact(contact)
                .content(noteContent)
                .build();

        noteRepository.save(note);

        List<Note> notes = noteRepository.findByContactId(contact.getId());

        assertFalse(notes.isEmpty());
        assertEquals(noteContent, notes.getFirst().getContent());
        assertEquals(contact.getId(), notes.getFirst().getContact().getId());
    }

    @Test
    void testFindByContactId_NoNotes() {
        Contact contact = Contact.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .build();

        contactRepository.save(contact);

        List<Note> notes = noteRepository.findByContactId(contact.getId());

        assertTrue(notes.isEmpty());
    }
}
