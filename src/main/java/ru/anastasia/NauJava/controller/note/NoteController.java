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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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
                             RedirectAttributes redirectAttributes) {
        log.info("POST /notes - создание заметки [контакт: {}]", noteCreateDto.getContactId());

        if (bindingResult.hasErrors()) {
            log.warn("Ошибки валидации при создании заметки: {}", bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.noteDto", bindingResult);
            redirectAttributes.addFlashAttribute("noteDto", noteCreateDto);
            redirectAttributes.addAttribute("error", "note_validation");
            return "redirect:/contacts/" + noteCreateDto.getContactId();
        }

        try {
            Note note = noteMapper.noteCreateDtoToNote(noteCreateDto);
            noteService.create(noteCreateDto.getContactId(), note);
            log.info("Заметка успешно создана [ID: {}, контакт: {}]", note.getId(), noteCreateDto.getContactId());
            redirectAttributes.addAttribute("success", "note_created");
            return "redirect:/contacts/" + noteCreateDto.getContactId();
        } catch (RuntimeException e) {
            log.error("Ошибка при создании заметки [контакт: {}]", noteCreateDto.getContactId(), e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addAttribute("error", "note_creation");
            return "redirect:/contacts/" + noteCreateDto.getContactId();
        }
    }

    @PostMapping("/{id}/update")
    public String updateNote(@PathVariable Long id,
                             @Valid @ModelAttribute("noteDto") NoteUpdateDto noteUpdateDto,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        log.info("POST /notes/{}/update - обновление заметки", id);

        noteUpdateDto.setId(id);

        if (bindingResult.hasErrors()) {
            log.warn("Ошибки валидации при обновлении заметки: {}", bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.noteDto", bindingResult);
            redirectAttributes.addFlashAttribute("noteDto", noteUpdateDto);
            redirectAttributes.addAttribute("error", "note_validation");
            return "redirect:/contacts/" + noteUpdateDto.getContactId();
        }

        try {
            Note note = noteMapper.noteUpdateDtoToNote(noteUpdateDto);
            noteService.update(note, noteUpdateDto.getContactId());

            log.info("Заметка успешно обновлена [ID: {}, контакт: {}]", id, noteUpdateDto.getContactId());
            redirectAttributes.addAttribute("success", "note_updated");
            return "redirect:/contacts/" + noteUpdateDto.getContactId();
        } catch (RuntimeException e) {
            log.error("Ошибка при обновлении заметки [ID: {}]", id, e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addAttribute("error", "note_update");
            return "redirect:/contacts/" + noteUpdateDto.getContactId();
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteNote(@PathVariable Long id,
                             RedirectAttributes redirectAttributes) {
        log.info("POST /notes/{}/delete - удаление заметки", id);

        try {
            Note note = noteService.findById(id);
            if (note != null) {
                Long contactId = note.getContact().getId();
                noteService.delete(id);
                log.info("Заметка успешно удалена [ID: {}, контакт: {}]", id, contactId);
                redirectAttributes.addAttribute("success", "note_deleted");
                return "redirect:/contacts/" + contactId;
            } else {
                log.warn("Заметка не найдена [ID: {}]", id);
                redirectAttributes.addAttribute("error", "note_not_found");
                return "redirect:/contacts";
            }
        } catch (Exception e) {
            log.error("Ошибка при удалении заметки [ID: {}]", id, e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addAttribute("error", "note_delete_error");
            return "redirect:/contacts";
        }
    }
}
