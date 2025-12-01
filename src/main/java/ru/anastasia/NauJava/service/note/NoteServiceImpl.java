package ru.anastasia.NauJava.service.note;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.entity.note.Note;
import ru.anastasia.NauJava.exception.note.NoteNotFoundException;
import ru.anastasia.NauJava.repository.note.NoteRepository;
import ru.anastasia.NauJava.service.contact.ContactService;

import java.util.List;

@Slf4j
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
        log.info("Создание заметки для контакта ID: {}", contactId);

        Contact contact = contactService.findById(contactId);
        log.debug("Контакт найден: ID: {}, имя: {}", contactId, contact.getFullName());

        note.setContact(contact);
        Note savedNote = noteRepository.save(note);

        log.info("Заметка успешно создана. ID: {}, контакт: {}, длина контента: {} символов",
                savedNote.getId(), contact.getFullName(),
                savedNote.getContent() != null ? savedNote.getContent().length() : 0);

        return savedNote;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Note> findByContactId(Long contactId) {
        log.debug("Поиск заметок для контакта ID: {}", contactId);

        List<Note> notes = noteRepository.findByContactId(contactId);

        log.debug("Найдено {} заметок для контакта ID: {}", notes.size(), contactId);

        return notes;
    }

    @Override
    public Note findById(Long id) {
        log.debug("Поиск заметки по ID: {}", id);

        Note note = noteRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Заметка не найдена с ID: {}", id);
                    return new NoteNotFoundException("Не найдена заметка с id: " + id);
                });

        log.debug("Заметка найдена: ID: {}, контакт ID: {}, длина контента: {} символов",
                note.getId(), note.getContact().getId(),
                note.getContent() != null ? note.getContent().length() : 0);

        return note;
    }

    @Override
    public Note update(Note note, Long contactId) {
        log.info("Обновление заметки ID: {}", note.getId());

        if (note.getId() == null) {
            log.error("Попытка обновить заметку с null ID");
            throw new IllegalArgumentException("ID заметки не может быть null");
        }

        Note existingNote = findById(note.getId());
        log.debug("Текущий контент заметки ID: {}, длина: {} символов",
                existingNote.getId(), existingNote.getContent() != null ? existingNote.getContent().length() : 0);

        if (!existingNote.getContact().getId().equals(contactId)) {
            log.warn("Попытка обновить заметку не принадлежащую контакту [noteId: {}, contactId: {}, actualContactId: {}]",
                    existingNote.getId(), contactId, existingNote.getContact().getId());
            throw new IllegalArgumentException("Заметка не принадлежит указанному контакту");
        }

        existingNote.setContent(note.getContent());
        Note updatedNote = noteRepository.save(existingNote);

        log.info("Заметка успешно обновлена. ID: {}, новая длина контента: {} символов",
                updatedNote.getId(),
                updatedNote.getContent() != null ? updatedNote.getContent().length() : 0);

        return updatedNote;
    }

    @Override
    public void delete(Long id) {
        log.info("Удаление заметки ID: {}", id);

        Note note = noteRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Попытка удаления несуществующей заметки ID: {}", id);
                    return new NoteNotFoundException("Не найдена заметка с id: " + id);
                });

        log.debug("Заметка для удаления: ID: {}, контакт ID: {}",
                note.getId(), note.getContact().getId());

        noteRepository.deleteById(id);

        log.info("Заметка успешно удалена. ID: {}, контакт ID: {}",
                id, note.getContact().getId());
    }
}