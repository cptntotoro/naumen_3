package ru.anastasia.NauJava.controller.note;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.anastasia.NauJava.dto.note.NoteCreateDto;
import ru.anastasia.NauJava.dto.note.NoteUpdateDto;
import ru.anastasia.NauJava.entity.note.Note;
import ru.anastasia.NauJava.mapper.note.NoteMapper;
import ru.anastasia.NauJava.service.note.NoteService;

@Slf4j
@Controller
@RequestMapping("/notes")
@RequiredArgsConstructor
public class NoteController {

    /**
     * Сервис управления заметками
     */
    private final NoteService noteService;

    /**
     * Маппер заметок
     */
    private final NoteMapper noteMapper;

    @PostMapping
    public String createNote(@Valid @ModelAttribute("noteDto") NoteCreateDto noteCreateDto,
                             BindingResult bindingResult) {
        log.info("POST /notes - создание заметки [контакт: {}]", noteCreateDto.getContactId());

        if (bindingResult.hasErrors()) {
            log.warn("Ошибки валидации при создании заметки: {}", bindingResult.getAllErrors());
            return "redirect:/contacts/" + noteCreateDto.getContactId() + "?error=note_validation";
        }

        try {
            Note note = noteMapper.noteCreateDtoToNote(noteCreateDto);
            noteService.create(noteCreateDto.getContactId(), note);
            log.info("Заметка успешно создана [ID: {}, контакт: {}]", note.getId(), noteCreateDto.getContactId());
            return "redirect:/contacts/" + noteCreateDto.getContactId() + "?success=note_created";
        } catch (RuntimeException e) {
            log.error("Ошибка при создании заметки [контакт: {}]", noteCreateDto.getContactId(), e);
            return "redirect:/contacts/" + noteCreateDto.getContactId() + "?error=note_creation";
        }
    }

    @PostMapping("/{id}/update")
    public String updateNote(@PathVariable Long id,
                             @Valid @ModelAttribute("noteDto") NoteUpdateDto noteUpdateDto,
                             BindingResult bindingResult) {
        log.info("POST /notes/{}/update - обновление заметки", id);

        noteUpdateDto.setId(id);

        if (bindingResult.hasErrors()) {
            log.warn("Ошибки валидации при обновлении заметки: {}", bindingResult.getAllErrors());
            return "redirect:/contacts/" + noteUpdateDto.getContactId() +
                    "?error=note_validation";
        }

        try {
            Note note = noteMapper.noteUpdateDtoToNote(noteUpdateDto);
            noteService.update(note, noteUpdateDto.getContactId());

            log.info("Заметка успешно обновлена [ID: {}, контакт: {}]", id, noteUpdateDto.getContactId());
            return "redirect:/contacts/" + noteUpdateDto.getContactId() + "?success=note_updated";
        } catch (RuntimeException e) {
            log.error("Ошибка при обновлении заметки [ID: {}]", id, e);
            return "redirect:/contacts/" + noteUpdateDto.getContactId() + "?error=note_update";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteNote(@PathVariable Long id) {
        log.info("POST /notes/{}/delete - удаление заметки", id);

        try {
            Note note = noteService.findById(id);
            if (note != null) {
                Long contactId = note.getContact().getId();
                noteService.delete(id);
                log.info("Заметка успешно удалена [ID: {}, контакт: {}]", id, contactId);
                return "redirect:/contacts/" + contactId + "?success=note_deleted";
            } else {
                log.warn("Заметка не найдена [ID: {}]", id);
                return "redirect:/contacts?error=note_not_found";
            }
        } catch (Exception e) {
            log.error("Ошибка при удалении заметки [ID: {}]", id, e);
            return "redirect:/contacts?error=note_delete_error";
        }
    }
}
