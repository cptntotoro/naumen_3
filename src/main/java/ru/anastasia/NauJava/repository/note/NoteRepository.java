package ru.anastasia.NauJava.repository.note;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.anastasia.NauJava.entity.note.Note;

import java.util.List;

/**
 * Репозиторий заметок
 */
@Repository
public interface NoteRepository extends CrudRepository<Note, Long> {

    /**
     * Получить заметки по идентификатору контакта
     *
     * @param contactId Идентификатор контакта
     * @return Список заметок
     */
    List<Note> findByContactId(Long contactId);
}
