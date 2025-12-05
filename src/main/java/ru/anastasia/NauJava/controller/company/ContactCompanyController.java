package ru.anastasia.NauJava.controller.company;

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
import ru.anastasia.NauJava.dto.company.ContactCompanyCreateDto;
import ru.anastasia.NauJava.dto.company.ContactCompanyUpdateDto;
import ru.anastasia.NauJava.service.company.ContactCompanyService;

@Slf4j
@Controller
@RequestMapping("/contact-companies")
@RequiredArgsConstructor
public class ContactCompanyController {

    /**
     * Сервис компаний контактов
     */
    private final ContactCompanyService contactCompanyService;

    @PostMapping
    public String createContactCompany(
            @RequestParam Long contactId,
            @Valid @ModelAttribute("contactCompanyDto") ContactCompanyCreateDto contactCompanyDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        log.info("POST /contact-companies - создание компании контакта [контакт: {}, компания: {}]",
                contactId, contactCompanyDto.getCompanyId());

        if (bindingResult.hasErrors()) {
            log.warn("Ошибки валидации при создании компании контакта: {}", bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.contactCompanyDto", bindingResult);
            redirectAttributes.addFlashAttribute("contactCompanyDto", contactCompanyDto);
            redirectAttributes.addAttribute("error", "contact_company_validation");
            return "redirect:/contacts/" + contactId;
        }

        try {
            contactCompanyService.create(contactCompanyDto, contactId);
            log.info("Компания успешно добавлена к контакту [контакт: {}, компания: {}]",
                    contactId, contactCompanyDto.getCompanyId());
            redirectAttributes.addFlashAttribute("successMessage", "Компания успешно добавлена к контакту");
            redirectAttributes.addAttribute("success", "contact_company_created");
            return "redirect:/contacts/" + contactId;
        } catch (RuntimeException e) {
            log.error("Ошибка при создании компании контакта [контакт: {}, компания: {}]",
                    contactId, contactCompanyDto.getCompanyId(), e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addAttribute("error", "contact_company_creation");
            return "redirect:/contacts/" + contactId;
        }
    }

    @PostMapping("/{id}")
    public String updateContactCompany(
            @PathVariable Long id,
            @RequestParam Long contactId,
            @Valid @ModelAttribute("contactCompanyDto") ContactCompanyUpdateDto dto,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        log.info("POST /contact-companies/{} - обновление связи контакт-компания", id);

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.contactCompanyDto", result);
            redirectAttributes.addFlashAttribute("contactCompanyDto", dto);
            redirectAttributes.addAttribute("error", "contact_company_validation");
            return "redirect:/contacts/" + contactId;
        }

        try {
            contactCompanyService.update(id, dto);
            redirectAttributes.addAttribute("success", "contact_company_updated");
        } catch (RuntimeException e) {
            log.error("Ошибка при обновлении связи контакт-компания [ID: {}]", id, e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addAttribute("error", "contact_company_update");
        }

        return "redirect:/contacts/" + contactId;
    }

    @PostMapping("/{id}/delete")
    public String deleteContactCompany(
            @PathVariable Long id,
            @RequestParam Long contactId,
            RedirectAttributes redirectAttributes) {

        log.info("POST /contact-companies/{}/delete - удаление компании контакта", id);

        try {
            contactCompanyService.delete(id);
            log.info("Компания контакта успешно удалена [ID: {}] для контакта [ID: {}]", id, contactId);
            redirectAttributes.addFlashAttribute("successMessage", "Компания удалена из контакта");
            redirectAttributes.addAttribute("success", "contact_company_deleted");
        } catch (RuntimeException e) {
            log.error("Ошибка при удалении компании контакта [ID: {}] для контакта [ID: {}]", id, contactId, e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addAttribute("error", "contact_company_delete");
        }

        return "redirect:/contacts/" + contactId;
    }
}