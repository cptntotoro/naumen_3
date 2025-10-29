package ru.anastasia.NauJava.controller.event;

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
import ru.anastasia.NauJava.dto.event.EventCreateDto;
import ru.anastasia.NauJava.dto.event.EventUpdateDto;
import ru.anastasia.NauJava.entity.event.Event;
import ru.anastasia.NauJava.mapper.event.EventMapper;
import ru.anastasia.NauJava.service.event.EventService;

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
        if (bindingResult.hasErrors()) {
            return "event/form";
        }

        Long contactId = eventCreateDto.getContactId();

        try {
            eventService.create(contactId, eventCreateDto);
            return "redirect:/contacts/" + contactId;
        } catch (RuntimeException e) {
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
        eventService.delete(id);
        return "redirect:/events";
    }
}
