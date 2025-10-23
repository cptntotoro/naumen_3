package ru.anastasia.NauJava.service.contact.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.entity.contact.Note;
import ru.anastasia.NauJava.repository.contact.NoteRepository;
import ru.anastasia.NauJava.service.contact.ContactService;
import ru.anastasia.NauJava.service.contact.NoteService;

import java.util.List;

@Service
@Transactional
public class NoteServiceImpl implements NoteService {
    /**
     * Репозиторий заметок
     */
    private final NoteRepository noteRepository;

    /**
     * Сервис контактов
     */
    private final ContactService contactService;

    @Autowired
    public NoteServiceImpl(NoteRepository noteRepository, ContactService contactService) {
        this.noteRepository = noteRepository;
        this.contactService = contactService;
    }

    @Override
    public Note create(Long contactId, String content) {
        Contact contact = contactService.findById(contactId);
        if (contact == null) {
            throw new RuntimeException("Не найден контакт с id: " + contactId);
        }
        Note note = Note.builder()
                .contact(contact)
                .content(content)
                .build();
        return noteRepository.save(note);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Note> findByContactId(Long contactId) {
        return noteRepository.findByContactId(contactId);
    }
}
