package ru.anastasia.NauJava.controller.tag;

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
import ru.anastasia.NauJava.dto.tag.TagFormDto;
import ru.anastasia.NauJava.entity.tag.Tag;
import ru.anastasia.NauJava.mapper.tag.TagMapper;
import ru.anastasia.NauJava.service.tag.TagService;

import java.util.List;

@Slf4j
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
        log.debug("GET /tags - список тегов");

        try {
            List<Tag> tags = tagService.findAll();
            model.addAttribute("tags", tags);
            log.debug("Загружено тегов: {}", tags.size());
            return "tag/list";
        } catch (Exception e) {
            log.error("Ошибка при загрузке списка тегов", e);
            throw e;
        }
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        log.debug("GET /tags/new - форма создания тега");
        model.addAttribute("tagDto", new TagFormDto());
        model.addAttribute("isEdit", false);
        return "tag/form";
    }

    @PostMapping
    public String createTag(@Valid @ModelAttribute("tagDto") TagFormDto tagFormDto,
                            BindingResult bindingResult, Model model) {
        log.info("POST /tags - создание тега [название: {}, цвет: {}]",
                tagFormDto.getName(), tagFormDto.getColor());

        if (bindingResult.hasErrors()) {
            log.warn("Ошибки валидации при создании тега: {}", bindingResult.getAllErrors());
            model.addAttribute("isEdit", false);
            return "tag/form";
        }

        try {
            Tag tag = tagMapper.tagFormDtoToTag(tagFormDto);
            Tag savedTag = tagService.create(tag);
            log.info("Тег успешно создан [ID: {}, название: {}]", savedTag.getId(), savedTag.getName());
            return "redirect:/tags";
        } catch (RuntimeException e) {
            log.error("Ошибка при создании тега [название: {}]", tagFormDto.getName(), e);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("isEdit", false);
            return "tag/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        log.debug("GET /tags/{}/edit - форма редактирования тега", id);

        try {
            Tag tag = tagService.findById(id);
            if (tag == null) {
                log.warn("Тег не найден при редактировании [ID: {}]", id);
                return "redirect:/tags";
            }

            TagFormDto dto = tagMapper.tagToTagFormDto(tag);
            model.addAttribute("tagDto", dto);
            model.addAttribute("isEdit", true);

            log.debug("Данные для редактирования тега подготовлены [ID: {}, название: {}]", id, tag.getName());
            return "tag/form";
        } catch (Exception e) {
            log.error("Ошибка при загрузке формы редактирования тега [ID: {}]", id, e);
            throw e;
        }
    }

    @PostMapping("/edit")
    public String updateTag(@Valid @ModelAttribute("tagDto") TagFormDto tagFormDto,
                            BindingResult bindingResult, Model model) {
        log.info("POST /tags/edit - обновление тега [ID: {}, название: {}]",
                tagFormDto.getId(), tagFormDto.getName());

        if (bindingResult.hasErrors()) {
            log.warn("Ошибки валидации при обновлении тега [ID: {}]: {}",
                    tagFormDto.getId(), bindingResult.getAllErrors());
            model.addAttribute("isEdit", true);
            return "tag/form";
        }

        try {
            Tag tag = tagMapper.tagFormDtoToTag(tagFormDto);
            Tag updatedTag = tagService.update(tag);
            log.info("Тег успешно обновлен [ID: {}, название: {}]", updatedTag.getId(), updatedTag.getName());
            return "redirect:/tags";
        } catch (RuntimeException e) {
            log.error("Ошибка при обновлении тега [ID: {}, название: {}]",
                    tagFormDto.getId(), tagFormDto.getName(), e);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("isEdit", true);
            return "tag/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteTagPost(@PathVariable Long id) {
        log.info("POST /tags/{}/delete - удаление тега", id);

        try {
            tagService.delete(id);
            log.info("Тег успешно удален [ID: {}]", id);
            return "redirect:/tags";
        } catch (Exception e) {
            log.error("Ошибка при удалении тега [ID: {}]", id, e);
            throw e;
        }
    }
}