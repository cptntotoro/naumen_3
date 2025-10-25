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
import ru.anastasia.NauJava.dto.company.ContactCompanyCreateDto;
import ru.anastasia.NauJava.dto.contact.ContactCreateDto;
import ru.anastasia.NauJava.dto.contact.ContactDetailCreateDto;
import ru.anastasia.NauJava.dto.contact.ContactUpdateDto;
import ru.anastasia.NauJava.dto.event.EventCreateDto;
import ru.anastasia.NauJava.dto.note.NoteCreateDto;
import ru.anastasia.NauJava.dto.socialprofile.SocialProfileCreateDto;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.entity.contact.ContactDetail;
import ru.anastasia.NauJava.mapper.contact.ContactDetailMapper;
import ru.anastasia.NauJava.mapper.contact.ContactMapper;
import ru.anastasia.NauJava.service.contact.ContactManagementService;
import ru.anastasia.NauJava.service.contact.ContactService;

import java.util.List;

@Controller
@RequestMapping("/contacts")
@RequiredArgsConstructor
class ContactController {

    /**
     * Сервис контактов
     */
    private final ContactService contactService;

    /**
     * Сервис управления контактами
     */
    private final ContactManagementService contactManagementService;

    /**
     * Маппер способов связи
     */
    private final ContactDetailMapper contactDetailMapper;

    /**
     * Маппер контактов
     */
    private final ContactMapper contactMapper;

    @GetMapping
    public String listContacts(Model model) {
        List<Contact> contacts = contactService.findAll();
        model.addAttribute("contacts", contacts);
        return "contact/list";
    }

    @GetMapping("/new")
    public String newContactForm(Model model) {
        model.addAttribute("contactDto", new ContactCreateDto());
        return "contact/form";
    }

    @PostMapping
    public String createContact(@Valid @ModelAttribute("contactDto") ContactCreateDto contactCreateDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "contact/form";
        }
        String company = contactCreateDto.getCompanies() != null && !contactCreateDto.getCompanies().isEmpty() ? contactCreateDto.getCompanies().get(0).getCompanyName() : null;
        String jobTitle = contactCreateDto.getCompanies() != null && !contactCreateDto.getCompanies().isEmpty() ? contactCreateDto.getCompanies().get(0).getJobTitle() : null;

        List<String> notes = contactCreateDto.getNotes() != null ?
                contactCreateDto.getNotes().stream()
                        .map(NoteCreateDto::getContent)
                        .toList()
                : null;

        // TODO
        contactManagementService.createWithDetails(
                contactCreateDto.getFirstName(),
                contactCreateDto.getLastName(),
                company,
                jobTitle,
                contactCreateDto.getContactDetails(),
                contactCreateDto.getSocialProfiles(),
                contactCreateDto.getEvents(),
                contactCreateDto.getTagNames(),
                notes
        );
        return "redirect:/contacts";
    }

    @GetMapping("/{id}/edit")
    public String editContactForm(@PathVariable Long id, Model model) {
        Contact contact = contactService.findById(id);
        if (contact == null) {
            return "redirect:/contacts";
        }
        ContactUpdateDto dto = ContactUpdateDto.builder()
                .firstName(contact.getFirstName())
                .lastName(contact.getLastName())
                .displayName(contact.getDisplayName())
                .avatarUrl(contact.getAvatarUrl())
                .isFavorite(contact.getIsFavorite())
                .contactDetails(contact.getContactDetails().stream()
                        .map(cd -> ContactDetailCreateDto.builder()
                                .detailType(cd.getDetailType())
                                .label(cd.getLabel())
                                .value(cd.getValue())
                                .isPrimary(cd.getIsPrimary())
                                .build()).toList())
                .socialProfiles(contact.getSocialProfiles().stream().map(sp -> SocialProfileCreateDto.builder()
                        .platform(sp.getPlatform())
                        .customPlatformName(sp.getCustomPlatformName())
                        .username(sp.getUsername())
                        .profileUrl(sp.getProfileUrl())
                        .build()).toList())
                .events(contact.getEvents().stream().map(e -> EventCreateDto.builder()
                        .eventType(e.getEventType())
                        .customEventName(e.getCustomEventName())
                        .eventDate(e.getEventDate())
                        .notes(e.getNotes())
                        .yearlyRecurrence(e.getYearlyRecurrence())
                        .build()).toList())
                .notes(contact.getNotes().stream().map(n -> NoteCreateDto.builder()
                        .content(n.getContent())
                        .build()).toList())
                .tagNames(contact.getContactTags().stream().map(ct -> ct.getTag().getName()).toList())
                .companies(contact.getCompanies().stream().map(cc -> ContactCompanyCreateDto.builder()
                        .companyName(cc.getCompany().getName())
                        .companyWebsite(cc.getCompany().getWebsite())
                        .jobTitle(cc.getJobTitle().getTitle())
                        .isCurrent(cc.getIsCurrent())
                        .build()).toList())
                .build();
        model.addAttribute("contactDto", dto);
        model.addAttribute("contactId", id);
        return "contact/edit";
    }

    @PostMapping("/{id}")
    public String updateContact(@PathVariable Long id,
                                @Valid @ModelAttribute("contactDto") ContactUpdateDto contactUpdateDto,
                                BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "contact/edit";
        }

        Contact contact = contactMapper.contactUpdateDtoToContact(contactUpdateDto);

        List<ContactDetail> contactDetails = contactUpdateDto.getContactDetails().stream()
                .map(contactDetailMapper::contactDetailUpdateDtoToContactDetail)
                .toList();

        contactManagementService.updateWithDetails(id, contact, contactDetails);
        return "redirect:/contacts";
    }

    @PostMapping("/{id}/delete")
    public String deleteContact(@PathVariable Long id) {
        contactService.deleteById(id);
        return "redirect:/contacts";
    }
}