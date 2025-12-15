package ru.anastasia.NauJava.controller.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.anastasia.NauJava.dto.event.EventCreateDto;
import ru.anastasia.NauJava.dto.event.EventUpdateDto;
import ru.anastasia.NauJava.service.facade.ContactEventManagementService;

@Slf4j
@Controller
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    /**
     * Сервис управления событиями контактов
     */
    private final ContactEventManagementService contactEventManagementService;

    @PostMapping
    public String createEvent(@Valid @ModelAttribute("eventDto") EventCreateDto eventCreateDto,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {
        log.info("POST /events - создание события [тип: {}, контакт: {}]",
                eventCreateDto.getEventType(), eventCreateDto.getContactId());

        if (bindingResult.hasErrors()) {
            log.warn("Ошибки валидации при создании события: {}", bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.eventDto", bindingResult);
            redirectAttributes.addFlashAttribute("eventDto", eventCreateDto);
            redirectAttributes.addAttribute("error", "event_validation");
            return "redirect:/contacts/" + eventCreateDto.getContactId();
        }

        Long contactId = eventCreateDto.getContactId();

        try {
            contactEventManagementService.createEventForContact(contactId, eventCreateDto);
            log.info("Событие успешно создано [контакт: {}, тип: {}]", contactId, eventCreateDto.getEventType());
            redirectAttributes.addFlashAttribute("successMessage",
                    "Событие '" + eventCreateDto.getEventType().name() + "' успешно создано");
            redirectAttributes.addAttribute("success", "event_created");
            return "redirect:/contacts/" + contactId;
        } catch (RuntimeException e) {
            log.error("Ошибка при создании события [контакт: {}, тип: {}]",
                    contactId, eventCreateDto.getEventType(), e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addAttribute("error", "event_creation");
            return "redirect:/contacts/" + contactId;
        }
    }

    @PostMapping("/{id}/update")
    public String updateEvent(@PathVariable Long id,
                              @Valid @ModelAttribute("eventDto") EventUpdateDto eventUpdateDto,
                              BindingResult bindingResult,
                              @RequestParam("contactId") Long contactId,
                              RedirectAttributes redirectAttributes) {
        log.info("POST /events/{}/update - обновление события для контакта ID: {}", id, contactId);

        if (bindingResult.hasErrors()) {
            log.warn("Ошибки валидации при обновлении события: {}", bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.eventDto", bindingResult);
            redirectAttributes.addFlashAttribute("eventDto", eventUpdateDto);
            redirectAttributes.addAttribute("error", "event_validation");
            return "redirect:/contacts/" + contactId;
        }

        try {
            contactEventManagementService.updateEventForContact(contactId, id, eventUpdateDto);

            log.info("Событие успешно обновлено [ID: {}] для контакта [ID: {}]", id, contactId);
            redirectAttributes.addAttribute("success", "event_updated");
        } catch (Exception e) {
            log.error("Ошибка при обновлении события [ID: {}] для контакта [ID: {}]", id, contactId, e);
            redirectAttributes.addAttribute("error", "event_update_failed");
            redirectAttributes.addFlashAttribute("errorMessage", "Не удалось обновить событие: " + e.getMessage());
        }

        return "redirect:/contacts/" + contactId;
    }

    @PostMapping("/{id}/delete")
    public String deleteEvent(@PathVariable Long id,
                              @RequestParam("contactId") Long contactId,
                              RedirectAttributes redirectAttributes) {
        log.info("POST /events/{}/delete - удаление события для контакта ID: {}", id, contactId);

        try {
            contactEventManagementService.deleteEventForContact(contactId, id);

            log.info("Событие успешно удалено [ID: {}] для контакта [ID: {}]", id, contactId);
            redirectAttributes.addAttribute("success", "event_deleted");
        } catch (Exception e) {
            log.error("Ошибка при удалении события [ID: {}] для контакта [ID: {}]", id, contactId, e);
            redirectAttributes.addAttribute("error", "event_delete_failed");
            redirectAttributes.addFlashAttribute("errorMessage", "Не удалось удалить событие: " + e.getMessage());
        }

        return "redirect:/contacts/" + contactId;
    }
}
