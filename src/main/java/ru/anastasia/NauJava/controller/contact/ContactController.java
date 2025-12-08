package ru.anastasia.NauJava.controller.contact;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.anastasia.NauJava.dto.company.ContactCompanyCreateDto;
import ru.anastasia.NauJava.dto.contact.ContactCreateDto;
import ru.anastasia.NauJava.dto.contact.ContactUpdateDto;
import ru.anastasia.NauJava.dto.event.EventCreateDto;
import ru.anastasia.NauJava.dto.note.NoteCreateDto;
import ru.anastasia.NauJava.dto.socialprofile.SocialProfileCreateDto;
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

@Slf4j
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

    /**
     * Страница по умолчанию
     */
    private static final String DEFAULT_PAGE = "0";

    /**
     * Размер страницы по умолчанию
     */
    private static final String DEFAULT_PAGE_SIZE = "12";

    @GetMapping
    public String listContacts(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "company", required = false) String companyName,
            @RequestParam(value = "tag", required = false) String tagName,
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) int size,
            Model model) {
        log.info("GET /contacts - поиск контактов [search: {}, company: {}, tag: {}, page: {}, size: {}]",
                search, companyName, tagName, page, size);

        List<Company> companies = companyService.findAll();
        List<Tag> tags = tagService.findAll();

        Pageable pageable = PageRequest.of(page, size);
        Page<Contact> contactPage;

        contactPage = contactService.searchContacts(search, companyName, tagName, pageable);
        log.debug("Найдено контактов: {} из {}", contactPage.getNumberOfElements(), contactPage.getTotalElements());

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
        log.debug("GET /contacts/new - форма создания контакта");
        model.addAttribute("contactCreateDto", new ContactCreateDto());
        model.addAttribute("allTags", tagService.findAll());
        model.addAttribute("allCompanies", companyService.findAll());
        model.addAttribute("allJobTitles", jobTitleService.findAll());
        model.addAttribute("platforms", SocialPlatform.values());
        model.addAttribute("detailTypes", DetailType.values());
        model.addAttribute("detailLabels", DetailLabel.values());
        model.addAttribute("eventTypes", EventType.values());
        return "contact/form";
    }

    @PostMapping
    public String createContact(@Valid @ModelAttribute("contactCreateDto") ContactCreateDto contactCreateDto,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        log.info("POST /contacts - создание контакта [имя: {} {}]",
                contactCreateDto.getFirstName(), contactCreateDto.getLastName());

        if (bindingResult.hasErrors()) {
            log.warn("Ошибки валидации при создании контакта: {}", bindingResult.getAllErrors());
            model.addAttribute("org.springframework.validation.BindingResult.contactCreateDto", bindingResult);
            newContactForm(model);
            return "contact/form";
        }

        try {
            Contact contact = contactManagementService.create(contactCreateDto);
            log.info("Контакт успешно создан [ID: {}, имя: {} {}]",
                    contact.getId(), contact.getFirstName(), contact.getLastName());

            redirectAttributes.addFlashAttribute("success", "Контакт успешно создан");
            redirectAttributes.addAttribute("success", "contact_created");
            return "redirect:/contacts";
        } catch (RuntimeException e) {
            log.error("Ошибка при создании контакта [имя: {} {}]",
                    contactCreateDto.getFirstName(), contactCreateDto.getLastName(), e);
            newContactForm(model);
            model.addAttribute("contactCreateDto", contactCreateDto);
            model.addAttribute("error", e.getMessage());
            return "contact/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editContactForm(@PathVariable Long id, Model model) {
        log.debug("GET /contacts/{}/edit - форма редактирования контакта", id);

        ContactFullDetails contactDetails = contactManagementService.getWithAllDetails(id);

        if (contactDetails == null) {
            log.warn("Контакт не найден при редактировании [ID: {}]", id);
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

        log.debug("Данные для редактирования контакта подготовлены [ID: {}, имя: {}]",
                id, contactDetails.getFullName());

        return "contact/editform";
    }

    @PostMapping("/{id}/edit")
    public String updateContact(@Valid @ModelAttribute("contactUpdateDto") ContactUpdateDto contactUpdateDto,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        log.info("POST /contacts/{}/edit - обновление контакта", contactUpdateDto.getId());

        if (bindingResult.hasErrors()) {
            log.warn("Ошибки валидации при обновлении контакта [ID: {}]: {}",
                    contactUpdateDto.getId(), bindingResult.getAllErrors());
            model.addAttribute("org.springframework.validation.BindingResult.contactUpdateDto", bindingResult);
            editContactForm(contactUpdateDto.getId(), model);
            return "contact/editform";
        }

        try {
            Contact contact = contactManagementService.update(contactUpdateDto);
            log.info("Контакт успешно обновлен [ID: {}, имя: {}, фамилия: {}]",
                    contact.getId(), contact.getFirstName(), contact.getLastName());

            redirectAttributes.addFlashAttribute("success", "Контакт успешно обновлен");
            redirectAttributes.addAttribute("success", "contact_updated");
            return "redirect:/contacts";
        } catch (RuntimeException e) {
            log.error("Ошибка при обновлении контакта [ID: {}]", contactUpdateDto.getId(), e);
            model.addAttribute("error", e.getMessage());
            editContactForm(contactUpdateDto.getId(), model);
            return "contact/editform";
        }
    }

    @GetMapping("/{id}")
    public String contactDetails(@PathVariable Long id, Model model) {
        log.info("GET /contacts/{} - получение контакта", id);

        ContactFullDetails contactDetails = contactManagementService.getWithAllDetails(id);
        model.addAttribute("contactDetails", contactDetails);

        model.addAttribute("contactCompanyDto", new ContactCompanyCreateDto());
        model.addAttribute("allCompanies", companyService.findAll());
        model.addAttribute("allJobTitles", jobTitleService.findAll());

        EventCreateDto eventDto = new EventCreateDto();
        eventDto.setContactId(id);
        model.addAttribute("eventDto", eventDto);

        model.addAttribute("eventTypes", EventType.values());

        NoteCreateDto noteDto = new NoteCreateDto();
        noteDto.setContactId(id);
        model.addAttribute("noteDto", noteDto);

        SocialProfileCreateDto socialProfileDto = new SocialProfileCreateDto();
        socialProfileDto.setContactId(id);
        model.addAttribute("socialProfileDto", socialProfileDto);

        model.addAttribute("platforms", SocialPlatform.values());

        return "contact/details";
    }

    @PostMapping("/{id}/duplicate")
    public String duplicateContact(@PathVariable Long id,
                                   RedirectAttributes redirectAttributes) {
        log.info("POST /contacts/{}/duplicate - дублирование контакта", id);
        try {
            contactManagementService.duplicate(id, null, null);
            redirectAttributes.addFlashAttribute("success", "Контакт успешно продублирован");
            redirectAttributes.addAttribute("success", "contact_duplicated");
        } catch (Exception e) {
            log.error("Ошибка при дублировании контакта [ID: {}]", id, e);
            redirectAttributes.addFlashAttribute("error", "Ошибка при дублировании: " + e.getMessage());
            redirectAttributes.addAttribute("error", "contact_duplication_failed");
        }
        return "redirect:/contacts";
    }

    @GetMapping("/favorites")
    public String favoriteContacts(
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) int size,
            Model model) {
        log.info("GET /contacts/favorites - получение избранных контактов");

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
        log.info("GET /contacts/birthdays - получение контактов с предстоящими днями рождения (30 дней)");

        List<ContactFullDetails> birthdayContacts = contactManagementService.getListWithUpcomingBirthdays(30);
        model.addAttribute("contacts", birthdayContacts);
        return "contact/birthdays";
    }

    @PostMapping("/{id}/delete")
    public String deleteContact(@PathVariable Long id,
                                RedirectAttributes redirectAttributes) {
        log.info("POST /contacts/{}/delete - удаление контакта", id);

        try {
            contactService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Контакт успешно удален");
            redirectAttributes.addAttribute("success", "contact_deleted");
        } catch (Exception e) {
            log.error("Ошибка при удалении контакта [ID: {}]", id, e);
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении: " + e.getMessage());
            redirectAttributes.addAttribute("error", "contact_delete_failed");
        }

        return "redirect:/contacts";
    }

    // TODO Метод для добавления/удаления из избранного тоже нужен (если есть в UI)
    @PostMapping("/{id}/favorite")
    public String toggleFavorite(@PathVariable Long id,
                                 @RequestParam(value = "action", defaultValue = "add") String action,
                                 RedirectAttributes redirectAttributes) {
        log.info("POST /contacts/{}/favorite - {} в избранное", id, action);

        try {
            if ("add".equals(action)) {
                contactService.addToFavorites(id);
                redirectAttributes.addFlashAttribute("success", "Контакт добавлен в избранное");
            } else {
                contactService.removeFromFavorites(id);
                redirectAttributes.addFlashAttribute("success", "Контакт удален из избранного");
            }
            redirectAttributes.addAttribute("success", "favorite_updated");
        } catch (Exception e) {
            log.error("Ошибка при обновлении избранного [ID: {}]", id, e);
            redirectAttributes.addFlashAttribute("error", "Ошибка: " + e.getMessage());
            redirectAttributes.addAttribute("error", "favorite_update_failed");
        }

        return "redirect:/contacts/" + id;
    }
}