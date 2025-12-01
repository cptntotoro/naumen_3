package ru.anastasia.NauJava.service.note;

import ru.anastasia.NauJava.entity.note.Note;

import java.util.List;

/**
 * Сервис заметок
 */
public interface NoteService {

    /**
     * Создать заметку
     *
     * @param contactId Идентификатор контакта
     * @param note      Заметка
     * @return Заметка
     */
    Note create(Long contactId, Note note);

    /**
     * Получить заметки по идентификатору контакта
     *
     * @param contactId Идентификатор контакта
     * @return Список заметок
     */
    List<Note> findByContactId(Long contactId);

    /**
     * Получить заметку по идентификатору
     *
     * @param id Идентификатор
     * @return Заметка
     */
    Note findById(Long id);

    /**
     * Обновить заметку
     *
     * @param note      Заметка
     * @param contactId Идентификатотр контакта
     * @return Заметка
     */
    Note update(Note note, Long contactId);

    /**
     * Удалить заметку
     *
     * @param id Идентификатор
     */
    void delete(Long id);
}
