package ru.anastasia.NauJava.controller.note;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
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
                             BindingResult bindingResult,
                             Model model) {
        log.info("POST /notes - создание заметки [контакт: {}]", noteCreateDto.getContactId());

        if (bindingResult.hasErrors()) {
            log.warn("Ошибки валидации при создании заметки: {}", bindingResult.getAllErrors());
            // Возвращаем на страницу контакта с ошибками
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

    @GetMapping("/{id}/edit")
    public String editNoteForm(@PathVariable Long id, Model model) {
        Note note = noteService.findById(id);
        if (note == null) {
            return "redirect:/contacts";
        }
        NoteUpdateDto dto = noteMapper.noteToNoteUpdateDto(note);

        model.addAttribute("noteDto", dto);
        model.addAttribute("noteId", id);
        return "note/edit";
    }

    @PostMapping("/{id}/edit")
    public String updateNote(@PathVariable Long id,
                             @Valid @ModelAttribute("noteDto") NoteUpdateDto noteUpdateDto,
                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "note/edit";
        }

        Note existingNote = noteService.findById(id);
        if (existingNote == null) {
            return "redirect:/contacts";
        }

        Note note = noteMapper.noteUpdateDtoToNote(noteUpdateDto);
        note.setId(id);
        note.setContact(existingNote.getContact());
        noteService.update(note);

        return "redirect:/contacts/" + existingNote.getContact().getId();
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
                return "redirect:/contacts/" + contactId;
            } else {
                log.warn("Заметка не найдена [ID: {}]", id);
                return "redirect:/contacts";
            }
        } catch (Exception e) {
            log.error("Ошибка при удалении заметки [ID: {}]", id, e);
            return "redirect:/contacts";
        }
    }
}
