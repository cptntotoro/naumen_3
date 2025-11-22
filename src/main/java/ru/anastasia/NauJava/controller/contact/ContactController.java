package ru.anastasia.NauJava.controller.contact;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.anastasia.NauJava.dto.contact.ContactCreateDto;
import ru.anastasia.NauJava.dto.contact.ContactUpdateDto;
import ru.anastasia.NauJava.entity.company.Company;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.entity.enums.DetailLabel;
import ru.anastasia.NauJava.entity.enums.DetailType;
import ru.anastasia.NauJava.entity.enums.EventType;
import ru.anastasia.NauJava.entity.enums.SocialPlatform;
import ru.anastasia.NauJava.entity.tag.Tag;
import ru.anastasia.NauJava.mapper.contact.ContactManagementMapper;
import ru.anastasia.NauJava.service.company.CompanyService;
import ru.anastasia.NauJava.service.company.JobTitleService;
import ru.anastasia.NauJava.service.contact.ContactManagementService;
import ru.anastasia.NauJava.service.contact.ContactService;
import ru.anastasia.NauJava.service.facade.dto.ContactFullDetails;
import ru.anastasia.NauJava.service.tag.TagService;

import java.util.List;

@Controller
@RequestMapping("/contacts")
@RequiredArgsConstructor
public class ContactController {

    /**
     * Сервис контактов
     */
    private final ContactService contactService;

    /**
     * Сервис управления контактами
     */
    private final ContactManagementService contactManagementService;

    /**
     * Сервис тегов
     */
    private final TagService tagService;

    /**
     * Маппер для управления контактами со сложными преобразованиями
     */
    private final ContactManagementMapper contactManagementMapper;

    /**
     * Сервис компаний
     */
    private final CompanyService companyService;

    /**
     * Сервис должностей
     */
    private final JobTitleService jobTitleService;

    @GetMapping
    public String listContacts(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "company", required = false) String companyName,
            @RequestParam(value = "tag", required = false) String tagName,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "12") int size,
            Model model) {

        List<Company> companies = companyService.findAll();
        List<Tag> tags = tagService.findAll();

        Pageable pageable = PageRequest.of(page, size);
        Page<Contact> contactPage;

        contactPage = contactService.searchContacts(search, companyName, tagName, pageable);

        model.addAttribute("contacts", contactPage.getContent());
        model.addAttribute("companies", companies);
        model.addAttribute("tags", tags);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", contactPage.getTotalPages());
        model.addAttribute("totalCount", contactPage.getTotalElements());

        model.addAttribute("searchParam", search);
        model.addAttribute("companyParam", companyName);
        model.addAttribute("tagParam", tagName);

        return "contact/list";
    }

    @GetMapping("/new")
    public String newContactForm(Model model) {
        model.addAttribute("contactCreateDto", new ContactCreateDto());
        model.addAttribute("allTags", tagService.findAll());
        model.addAttribute("allCompanies", companyService.findAll());
        model.addAttribute("allJobTitles", jobTitleService.findAll());
        model.addAttribute("platforms", SocialPlatform.values());
        model.addAttribute("detailTypes", DetailType.values());
        model.addAttribute("detailLabels", DetailLabel.values());
        return "contact/form";
    }

    @PostMapping
    public String createContact(@Valid @ModelAttribute("contactCreateDto") ContactCreateDto contactCreateDto,
                                BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "contact/form";
        }

        try {
            contactManagementService.create(contactCreateDto);
            return "redirect:/contacts";
        } catch (RuntimeException e) {
            newContactForm(model);
            model.addAttribute("contactCreateDto", contactCreateDto);
            model.addAttribute("error", e.getMessage());
            return "contact/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editContactForm(@PathVariable Long id, Model model) {
        ContactFullDetails contactDetails = contactManagementService.getWithAllDetails(id);

        if (contactDetails == null) {
            return "redirect:/contacts";
        }

        model.addAttribute("allTags", tagService.findAll());
        model.addAttribute("allCompanies", companyService.findAll());
        model.addAttribute("allJobTitles", jobTitleService.findAll());
        model.addAttribute("platforms", SocialPlatform.values());
        model.addAttribute("detailTypes", DetailType.values());
        model.addAttribute("detailLabels", DetailLabel.values());
        model.addAttribute("eventTypes", EventType.values());

        ContactUpdateDto contactUpdateDto = contactManagementMapper.contactFullDetailsToContactUpdateDto(contactDetails);
        model.addAttribute("contactUpdateDto", contactUpdateDto);
        return "contact/editform";
    }

    @PostMapping("/{id}/edit")
    public String updateContact(@Valid @ModelAttribute("contactUpdateDto") ContactUpdateDto contactUpdateDto,
                                BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "contact/editform";
        }

        try {
            contactManagementService.update(contactUpdateDto);
            return "redirect:/contacts";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("allTags", tagService.findAll());
            model.addAttribute("allCompanies", companyService.findAll());
            model.addAttribute("allJobTitles", jobTitleService.findAll());
            model.addAttribute("platforms", SocialPlatform.values());
            model.addAttribute("detailTypes", DetailType.values());
            model.addAttribute("detailLabels", DetailLabel.values());
            model.addAttribute("eventTypes", EventType.values());
            return "contact/editform";
        }
    }

    @GetMapping("/{id}")
    public String contactDetails(@PathVariable Long id, Model model) {
        ContactFullDetails contactDetails = contactManagementService.getWithAllDetails(id);
        model.addAttribute("contactDetails", contactDetails);
        return "contact/details";
    }

    @PostMapping("/{id}/duplicate")
    public String duplicateContact(@PathVariable Long id) {
        contactManagementService.duplicate(id, null, null);
        return "redirect:/contacts";
    }

    @GetMapping("/favorites")
    public String favoriteContacts(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "12") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Contact> favoriteContactsPage = contactService.findFavorites(pageable);

        model.addAttribute("contacts", favoriteContactsPage.getContent());

        model.addAttribute("contacts", favoriteContactsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", favoriteContactsPage.getTotalPages());
        model.addAttribute("totalCount", favoriteContactsPage.getTotalElements());

        Long favoritesCount = contactService.countFavorites();
        model.addAttribute("favoritesCount", favoritesCount);

        return "contact/favorites";
    }

    @GetMapping("/birthdays")
    public String upcomingBirthdays(Model model) {
        List<ContactFullDetails> birthdayContacts = contactManagementService.getListWithUpcomingBirthdays(30);
        model.addAttribute("contacts", birthdayContacts);
        return "contact/birthdays";
    }

    @PostMapping("/{id}/delete")
    public String deleteContact(@PathVariable Long id) {
        contactService.deleteById(id);
        return "redirect:/contacts";
    }
}