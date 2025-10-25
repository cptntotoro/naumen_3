package ru.anastasia.NauJava.mapper.note;

import org.mapstruct.Mapper;
import ru.anastasia.NauJava.dto.note.NoteCreateDto;
import ru.anastasia.NauJava.dto.note.NoteUpdateDto;
import ru.anastasia.NauJava.entity.note.Note;

/**
 * Маппер заметок
 */
@Mapper(componentModel = "spring")
public interface NoteMapper {

    /**
     * Смаппить DTO создания заметки в заметку
     *
     * @param noteCreateDto DTO создания заметки
     * @return Заметка
     */
    Note noteCreateDtoToNote(NoteCreateDto noteCreateDto);

    /**
     * Смаппить DTO обновления заметки в заметку
     *
     * @param noteUpdateDto DTO обновления заметки
     * @return Заметка
     */
    Note noteUpdateDtoToNote(NoteUpdateDto noteUpdateDto);
}
