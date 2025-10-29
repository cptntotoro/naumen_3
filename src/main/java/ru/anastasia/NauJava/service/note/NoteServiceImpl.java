package ru.anastasia.NauJava.service.note;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.entity.note.Note;
import ru.anastasia.NauJava.exception.note.NoteNotFoundException;
import ru.anastasia.NauJava.repository.note.NoteRepository;
import ru.anastasia.NauJava.service.contact.ContactService;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {
    /**
     * Репозиторий заметок
     */
    private final NoteRepository noteRepository;

    /**
     * Сервис контактов
     */
    private final ContactService contactService;

    @Override
    public Note create(Long contactId, Note note) {
        Contact contact = contactService.findById(contactId);
        note.setContact(contact);
        return noteRepository.save(note);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Note> findByContactId(Long contactId) {
        return noteRepository.findByContactId(contactId);
    }

    @Override
    public Note findById(Long id) {
        return noteRepository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException("Не найдена заметка с id: " + id));
    }

    @Override
    public Note update(Note note) {
        Note existing = findById(note.getId());
        existing.setContent(note.getContent());
        return noteRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        noteRepository.deleteById(id);
    }
}
