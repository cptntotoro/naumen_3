package ru.anastasia.NauJava.service.contact;

import ru.anastasia.NauJava.entity.contact.Note;

import java.util.List;

/**
 * Сервис управления заметками
 */
public interface NoteService {

    /**
     * Создать заметку
     *
     * @param contactId Идентификатор контакта
     * @param content   Содержимое заметки
     * @return Заметка
     */
    Note create(Long contactId, String content);

    /**
     * Получить заметки по идентификатору контакта
     *
     * @param contactId Идентификатор контакта
     * @return Список заметок
     */
    List<Note> findByContactId(Long contactId);
}
