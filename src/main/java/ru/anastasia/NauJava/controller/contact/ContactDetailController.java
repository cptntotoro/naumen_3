package ru.anastasia.NauJava.controller.contact;

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
import ru.anastasia.NauJava.dto.contact.ContactDetailCreateDto;
import ru.anastasia.NauJava.dto.contact.ContactDetailUpdateDto;
import ru.anastasia.NauJava.entity.contact.ContactDetail;
import ru.anastasia.NauJava.mapper.contact.ContactDetailMapper;
import ru.anastasia.NauJava.service.contact.ContactDetailService;

import java.util.List;

@Controller
@RequestMapping("/contact-details")
@RequiredArgsConstructor
public class ContactDetailController {

    /**
     * Сервис способов связи
     */
    private final ContactDetailService contactDetailService;

    /**
     * Маппер способов связи
     */
    private final ContactDetailMapper contactDetailMapper;

    @GetMapping
    public String listContactDetails(Model model) {
        List<ContactDetail> details = contactDetailService.findAll();
        model.addAttribute("details", details);
        return "contactdetail/list";
    }

    @GetMapping("/{id}/edit")
    public String editContactDetailForm(@PathVariable Long id, Model model) {
        ContactDetail detail = contactDetailService.findById(id);
        if (detail == null) {
            return "redirect:/contact-details";
        }
        ContactDetailCreateDto dto = ContactDetailCreateDto.builder()
                .detailType(detail.getDetailType())
                .label(detail.getLabel())
                .value(detail.getValue())
                .isPrimary(detail.getIsPrimary())
                .build();
        model.addAttribute("detailDto", dto);
        model.addAttribute("detailId", id);
        return "contactdetail/edit";
    }

    @PostMapping("/{id}")
    public String updateContactDetail(@PathVariable Long id,
                                      @Valid @ModelAttribute("detailDto") ContactDetailUpdateDto contactDetailUpdateDto,
                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "contactdetail/edit";
        }
        ContactDetail detail = contactDetailMapper.contactDetailUpdateDtoToContactDetail(contactDetailUpdateDto);
        contactDetailService.update(id, detail);
        return "redirect:/contact-details";
    }

    @PostMapping("/{id}/delete")
    public String deleteContactDetail(@PathVariable Long id) {
        contactDetailService.delete(id);
        return "redirect:/contact-details";
    }
}