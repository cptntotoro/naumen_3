package ru.anastasia.NauJava.service.note;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.entity.note.Note;
import ru.anastasia.NauJava.exception.note.NoteNotFoundException;
import ru.anastasia.NauJava.repository.note.NoteRepository;
import ru.anastasia.NauJava.service.contact.ContactService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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

    private Contact createTestContact() {
        return Contact.builder()
                .id(1L)
                .firstName("Иван")
                .lastName("Иванов")
                .build();
    }

    private Note createTestNote() {
        return Note.builder()
                .id(1L)
                .content("Тестовая заметка о контакте")
                .contact(createTestContact())
                .build();
    }

    @Test
    void create_WhenValidData_ShouldReturnSavedNote() {
        Long contactId = 1L;
        Contact contact = createTestContact();
        Note note = Note.builder()
                .content("Новая заметка")
                .build();
        Note savedNote = createTestNote();

        when(contactService.findById(contactId)).thenReturn(contact);
        when(noteRepository.save(note)).thenReturn(savedNote);

        Note result = noteService.create(contactId, note);

        assertNotNull(result);
        assertEquals(savedNote.getId(), result.getId());
        assertEquals(savedNote.getContent(), result.getContent());
        assertEquals(contact, note.getContact());
        verify(contactService, times(1)).findById(contactId);
        verify(noteRepository, times(1)).save(note);
    }

    @Test
    void create_WhenContactNotFound_ShouldThrowException() {
        Long contactId = 1L;
        Note note = createTestNote();

        when(contactService.findById(contactId))
                .thenThrow(new ru.anastasia.NauJava.exception.contact.ContactNotFoundException("Контакт не найден"));

        assertThrows(
                ru.anastasia.NauJava.exception.contact.ContactNotFoundException.class,
                () -> noteService.create(contactId, note)
        );

        verify(contactService, times(1)).findById(contactId);
        verify(noteRepository, never()).save(any(Note.class));
    }

    @Test
    void findByContactId_WhenNotesExist_ShouldReturnNotesList() {
        Long contactId = 1L;
        List<Note> expectedNotes = Arrays.asList(createTestNote(), createTestNote());

        when(noteRepository.findByContactId(contactId)).thenReturn(expectedNotes);

        List<Note> result = noteService.findByContactId(contactId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(noteRepository, times(1)).findByContactId(contactId);
    }

    @Test
    void findByContactId_WhenNoNotes_ShouldReturnEmptyList() {
        Long contactId = 1L;

        when(noteRepository.findByContactId(contactId)).thenReturn(List.of());

        List<Note> result = noteService.findByContactId(contactId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(noteRepository, times(1)).findByContactId(contactId);
    }

    @Test
    void findById_WhenNoteExists_ShouldReturnNote() {
        Long noteId = 1L;
        Note testNote = createTestNote();

        when(noteRepository.findById(noteId)).thenReturn(Optional.of(testNote));

        Note result = noteService.findById(noteId);

        assertNotNull(result);
        assertEquals(testNote.getId(), result.getId());
        assertEquals(testNote.getContent(), result.getContent());
        verify(noteRepository, times(1)).findById(noteId);
    }

    @Test
    void findById_WhenNoteNotExists_ShouldThrowNoteNotFoundException() {
        Long nonExistentId = 999L;

        when(noteRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        NoteNotFoundException exception = assertThrows(
                NoteNotFoundException.class,
                () -> noteService.findById(nonExistentId)
        );

        assertTrue(exception.getMessage().contains("Не найдена заметка с id: " + nonExistentId));
        verify(noteRepository, times(1)).findById(nonExistentId);
    }

    @Test
    void update_WhenValidNote_ShouldReturnUpdatedNote() {
        Note updatedNote = createTestNote();
        updatedNote.setContent("Обновленное содержимое заметки");
        Note existingNote = createTestNote();
        Note savedNote = createTestNote();
        savedNote.setContent("Обновленное содержимое заметки");

        when(noteRepository.findById(updatedNote.getId())).thenReturn(Optional.of(existingNote));
        when(noteRepository.save(existingNote)).thenReturn(savedNote);

        Note result = noteService.update(updatedNote);

        assertNotNull(result);
        assertEquals(updatedNote.getContent(), result.getContent());
        verify(noteRepository, times(1)).findById(updatedNote.getId());
        verify(noteRepository, times(1)).save(existingNote);
    }

    @Test
    void update_WhenNoteNotFound_ShouldThrowNoteNotFoundException() {
        Note note = createTestNote();

        when(noteRepository.findById(note.getId())).thenReturn(Optional.empty());

        NoteNotFoundException exception = assertThrows(
                NoteNotFoundException.class,
                () -> noteService.update(note)
        );

        assertTrue(exception.getMessage().contains("Не найдена заметка с id: " + note.getId()));
        verify(noteRepository, times(1)).findById(note.getId());
        verify(noteRepository, never()).save(any(Note.class));
    }

    @Test
    void delete_WhenValidId_ShouldCallRepositoryDelete() {
        Long noteId = 1L;

        when(noteRepository.findById(noteId)).thenReturn(Optional.of(createTestNote()));
        doNothing().when(noteRepository).deleteById(noteId);

        noteService.delete(noteId);

        verify(noteRepository, times(1)).deleteById(noteId);
    }

    @Test
    void delete_WhenNoteNotExists_ShouldThrowNoteNotFoundException() {
        Long nonExistentId = 999L;

        when(noteRepository.findById(nonExistentId)).thenThrow(NoteNotFoundException.class);

        assertThrows(NoteNotFoundException.class, () -> noteService.delete(nonExistentId));
    }
}