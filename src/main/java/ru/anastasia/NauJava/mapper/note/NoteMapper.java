package ru.anastasia.NauJava.mapper.note;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.anastasia.NauJava.dto.note.NoteCreateDto;
import ru.anastasia.NauJava.dto.note.NoteUpdateDto;
import ru.anastasia.NauJava.entity.note.Note;

/**
 * Маппер заметок
 */
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NoteMapper {

    /**
     * Смаппить DTO создания заметки в заметку
     *
     * @param noteCreateDto DTO создания заметки
     * @return Заметка
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "contact", ignore = true)
    Note noteCreateDtoToNote(NoteCreateDto noteCreateDto);

    /**
     * Смаппить DTO обновления заметки в заметку
     *
     * @param noteUpdateDto DTO обновления заметки
     * @return Заметка
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "contact", ignore = true)
    Note noteUpdateDtoToNote(NoteUpdateDto noteUpdateDto);

    /**
     * Смаппить заметку в DTO обновления заметки
     *
     * @param note Заметка
     * @return DTO обновления заметки
     */
    NoteUpdateDto noteToNoteUpdateDto(Note note);
}
