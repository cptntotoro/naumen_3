package ru.anastasia.NauJava.controller.tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.anastasia.NauJava.dto.tag.TagFormDto;
import ru.anastasia.NauJava.entity.tag.Tag;
import ru.anastasia.NauJava.mapper.tag.TagMapper;
import ru.anastasia.NauJava.service.tag.TagService;

import java.util.List;

@Controller
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagController {

    /**
     * Сервис управления тегами
     */
    private final TagService tagService;

    /**
     * Маппер тегов
     */
    private final TagMapper tagMapper;

    @GetMapping
    public String listTags(Model model) {
        List<Tag> tags = tagService.findAll();
        model.addAttribute("tags", tags);
        return "tag/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("tagDto", new TagFormDto());
        model.addAttribute("isEdit", false);
        return "tag/form";
    }

    @PostMapping
    public String createTag(@Valid @ModelAttribute("tagDto") TagFormDto tagFormDto, BindingResult bindingResult,
                            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "tag/form";
        }
        try {
            Tag tag = tagMapper.tagFormDtoToTag(tagFormDto);
            tagService.create(tag);
            return "redirect:/tags";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("isEdit", false);
            return "tag/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Tag tag = tagService.findById(id);
        if (tag == null) {
            return "redirect:/tags";
        }

        TagFormDto dto = tagMapper.tagToTagFormDto(tag);
        model.addAttribute("tagDto", dto);
        model.addAttribute("isEdit", true);
        return "tag/form";
    }

    @PostMapping("/edit")
    public String updateTag(@Valid @ModelAttribute("tagDto") TagFormDto tagFormDto,
                            BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", true);
            return "tag/form";
        }
        try {
            Tag tag = tagMapper.tagFormDtoToTag(tagFormDto);
            tagService.update(tag);
            return "redirect:/tags";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("isEdit", true);
            return "tag/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteTagPost(@PathVariable Long id) {
        tagService.delete(id);
        return "redirect:/tags";
    }
}