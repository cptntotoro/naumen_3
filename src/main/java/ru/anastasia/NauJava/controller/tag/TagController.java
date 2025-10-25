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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.anastasia.NauJava.dto.tag.TagCreateDto;
import ru.anastasia.NauJava.dto.tag.TagUpdateDto;
import ru.anastasia.NauJava.entity.tag.Tag;
import ru.anastasia.NauJava.mapper.tag.TagMapper;
import ru.anastasia.NauJava.service.tag.TagService;

import java.util.List;

@Controller
@RequestMapping("/tags")
@RequiredArgsConstructor
class TagController {
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
    public String newTagForm(Model model) {
        model.addAttribute("tagDto", new TagCreateDto());
        return "tag/form";
    }

    @PostMapping
    public String createTag(@Valid @ModelAttribute("tagDto") TagCreateDto tagCreateDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "tag/form";
        }
        Tag tag = tagMapper.tagCreateDtoToTag(tagCreateDto);
        tagService.create(tag);
        return "redirect:/tags";
    }

    @GetMapping("/{id}/edit")
    public String editTagForm(@PathVariable Long id, Model model) {
        Tag tag = tagService.findById(id);
        if (tag == null) {
            return "redirect:/tags";
        }

        TagUpdateDto dto = tagMapper.tagToTagUpdateDto(tag);
        model.addAttribute("tagDto", dto);
        model.addAttribute("tagId", id);
        return "tag/edit";
    }

    @PutMapping()
    public String updateTag(@Valid @ModelAttribute("tagDto") TagUpdateDto tagUpdateDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "tag/edit";
        }
        Tag tag = tagMapper.tagUpdateDtoToTag(tagUpdateDto);
        tagService.update(tag);
        return "redirect:/tags";
    }

    @PostMapping("/{id}/delete")
    public String deleteTag(@PathVariable Long id) {
        tagService.delete(id);
        return "redirect:/tags";
    }
}