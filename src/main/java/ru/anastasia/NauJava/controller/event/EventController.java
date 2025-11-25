package ru.anastasia.NauJava.controller.event;

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
import ru.anastasia.NauJava.dto.event.EventCreateDto;
import ru.anastasia.NauJava.dto.event.EventUpdateDto;
import ru.anastasia.NauJava.entity.event.Event;
import ru.anastasia.NauJava.mapper.event.EventMapper;
import ru.anastasia.NauJava.service.event.EventService;

@Slf4j
@Controller
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    /**
     * Сервис управления событиями контактов
     */
    private final EventService eventService;

    /**
     * Маппер событий
     */
    private final EventMapper eventMapper;

    @GetMapping("/new")
    public String newEventForm(Model model) {
        model.addAttribute("eventDto", new EventCreateDto());
        return "event/form";
    }

    @PostMapping
    public String createEvent(@Valid @ModelAttribute("eventDto") EventCreateDto eventCreateDto,
                              BindingResult bindingResult, Model model) {
        log.info("POST /events - создание события [тип: {}, контакт: {}]",
                eventCreateDto.getEventType(), eventCreateDto.getContactId());

        if (bindingResult.hasErrors()) {
            log.warn("Ошибки валидации при создании события: {}", bindingResult.getAllErrors());
            return "event/form";
        }

        Long contactId = eventCreateDto.getContactId();

        try {
            eventService.create(contactId, eventCreateDto);
            log.info("Событие успешно создано [контакт: {}, тип: {}]", contactId, eventCreateDto.getEventType());
            return "redirect:/contacts/" + contactId;
        } catch (RuntimeException e) {
            log.error("Ошибка при создании события [контакт: {}, тип: {}]",
                    contactId, eventCreateDto.getEventType(), e);
            model.addAttribute("error", e.getMessage());
            return "event/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editEventForm(@PathVariable Long id, Model model) {
        Event event = eventService.findById(id);
        if (event == null) {
            return "redirect:/events";
        }

        EventUpdateDto dto = eventMapper.eventToEventUpdateDto(event);

        model.addAttribute("eventDto", dto);
        model.addAttribute("eventId", id);
        return "event/edit";
    }

    @PostMapping("/edit")
    public String updateEvent(@Valid @ModelAttribute("eventDto") EventUpdateDto eventUpdateDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "event/edit";
        }
        Event event = eventMapper.eventUpdateDtoToEvent(eventUpdateDto);
        eventService.update(event);
        return "redirect:/events";
    }

    @PostMapping("/{id}/delete")
    public String deleteEvent(@PathVariable Long id) {
        log.info("POST /events/{}/delete - удаление события", id);

        try {
            eventService.delete(id);
            log.info("Событие успешно удалено [ID: {}]", id);
        } catch (Exception e) {
            log.error("Ошибка при удалении события [ID: {}]", id, e);
            throw e;
        }

        return "redirect:/events";
    }
}
