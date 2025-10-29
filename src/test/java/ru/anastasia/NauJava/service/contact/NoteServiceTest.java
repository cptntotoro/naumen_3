package ru.anastasia.NauJava.service.contact;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.entity.note.Note;
import ru.anastasia.NauJava.exception.note.NoteNotFoundException;
import ru.anastasia.NauJava.repository.note.NoteRepository;
import ru.anastasia.NauJava.service.note.NoteServiceImpl;

import java.util.List;
import java.util.Optional;

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
class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;

    @Mock
    private ContactService contactService;

    @InjectMocks
    private NoteServiceImpl noteService;

    @Test
    void create_ShouldReturnSavedNote() {
        Long contactId = 1L;
        Contact contact = Contact.builder().id(contactId).build();
        Note note = Note.builder().content("Test note").build();
        Note savedNote = Note.builder().id(1L).content("Test note").contact(contact).build();

        when(contactService.findById(contactId)).thenReturn(contact);
        when(noteRepository.save(note)).thenReturn(savedNote);

        Note result = noteService.create(contactId, note);

        assertNotNull(result.getId());
        assertEquals(contact, result.getContact());
        assertEquals("Test note", result.getContent());
        verify(contactService).findById(contactId);
        verify(noteRepository).save(note);
    }

    @Test
    void findByContactId_ShouldReturnNotes() {
        Long contactId = 1L;
        Note note1 = Note.builder().id(1L).content("Note 1").build();
        Note note2 = Note.builder().id(2L).content("Note 2").build();
        List<Note> expectedNotes = List.of(note1, note2);

        when(noteRepository.findByContactId(contactId)).thenReturn(expectedNotes);

        List<Note> result = noteService.findByContactId(contactId);

        assertEquals(expectedNotes, result);
        verify(noteRepository).findByContactId(contactId);
    }

    @Test
    void findById_ShouldReturnNote_WhenExists() {
        Long id = 1L;
        Note note = Note.builder().id(id).content("Test note").build();

        when(noteRepository.findById(id)).thenReturn(Optional.of(note));

        Note result = noteService.findById(id);

        assertEquals(note, result);
        verify(noteRepository).findById(id);
    }

    @Test
    void findById_ShouldThrowNoteNotFoundException_WhenNotExists() {
        Long id = 999L;

        when(noteRepository.findById(id)).thenReturn(Optional.empty());

        NoteNotFoundException exception = assertThrows(
                NoteNotFoundException.class,
                () -> noteService.findById(id)
        );

        assertTrue(exception.getMessage().contains("Не найдена заметка с id: " + id));
        verify(noteRepository).findById(id);
    }

    @Test
    void update_ShouldReturnUpdatedNote_WhenExists() {
        Long id = 1L;
        Note existingNote = Note.builder().id(id).content("Old content").build();
        Note updateNote = Note.builder().id(id).content("New content").build();
        Note updatedNote = Note.builder().id(id).content("New content").build();

        when(noteRepository.findById(id)).thenReturn(Optional.of(existingNote));
        when(noteRepository.save(existingNote)).thenReturn(updatedNote);

        Note result = noteService.update(updateNote);

        assertEquals("New content", result.getContent());
        verify(noteRepository).findById(id);
        verify(noteRepository).save(existingNote);
    }

    @Test
    void update_ShouldThrowNoteNotFoundException_WhenNotExists() {
        Note note = Note.builder().id(999L).build();

        when(noteRepository.findById(999L)).thenReturn(Optional.empty());

        NoteNotFoundException exception = assertThrows(
                NoteNotFoundException.class,
                () -> noteService.update(note)
        );

        assertTrue(exception.getMessage().contains("Не найдена заметка с id: " + note.getId()));
        verify(noteRepository).findById(999L);
        verify(noteRepository, never()).save(any(Note.class));
    }

    @Test
    void delete_ShouldCallRepositoryDelete() {
        Long id = 1L;
        doNothing().when(noteRepository).deleteById(id);

        noteService.delete(id);

        verify(noteRepository).deleteById(id);
    }
}