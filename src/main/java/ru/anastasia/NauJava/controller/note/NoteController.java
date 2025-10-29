package ru.anastasia.NauJava.controller.note;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
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

    @GetMapping("/new")
    public String newNoteForm(Model model) {
        model.addAttribute("noteDto", new NoteCreateDto());
        return "note/form";
    }

    @PostMapping
    public String createNote(@Valid @ModelAttribute("noteDto") NoteCreateDto noteCreateDto, BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            return "note/form";
        }

        try {
            Note note = noteMapper.noteCreateDtoToNote(noteCreateDto);
            noteService.create(noteCreateDto.getContactId(), note);
            return "redirect:/contacts/" + noteCreateDto.getContactId();
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "note/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editNoteForm(@PathVariable Long id, Model model) {
        Note note = noteService.findById(id);
        if (note == null) {
            return "redirect:/notes";
        }
        NoteUpdateDto dto = noteMapper.noteToNoteUpdateDto(note);

        model.addAttribute("noteDto", dto);
        model.addAttribute("noteId", id);
        return "note/edit";
    }

    @PostMapping("/edit")
    public String updateNote(@Valid @ModelAttribute("noteDto") NoteUpdateDto noteUpdateDto,
                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "note/edit";
        }
        Note note = noteMapper.noteUpdateDtoToNote(noteUpdateDto);
        noteService.update(note);
        return "redirect:/notes";
    }

    @DeleteMapping("/{id}")
    public String deleteNote(@PathVariable Long id) {
        noteService.delete(id);
        return "redirect:/notes";
    }
}
