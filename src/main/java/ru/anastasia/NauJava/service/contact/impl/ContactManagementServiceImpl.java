package ru.anastasia.NauJava.service.contact.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.anastasia.NauJava.dto.company.ContactCompanyCreateDto;
import ru.anastasia.NauJava.dto.company.ContactCompanyUpdateDto;
import ru.anastasia.NauJava.dto.contact.ContactCreateDto;
import ru.anastasia.NauJava.dto.contact.ContactDetailCreateDto;
import ru.anastasia.NauJava.dto.contact.ContactUpdateDto;
import ru.anastasia.NauJava.dto.event.EventCreateDto;
import ru.anastasia.NauJava.dto.event.EventUpdateDto;
import ru.anastasia.NauJava.dto.note.NoteCreateDto;
import ru.anastasia.NauJava.dto.note.NoteUpdateDto;
import ru.anastasia.NauJava.dto.socialprofile.SocialProfileCreateDto;
import ru.anastasia.NauJava.dto.socialprofile.SocialProfileUpdateDto;
import ru.anastasia.NauJava.entity.company.Company;
import ru.anastasia.NauJava.entity.company.ContactCompany;
import ru.anastasia.NauJava.entity.company.JobTitle;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.entity.contact.ContactDetail;
import ru.anastasia.NauJava.entity.event.Event;
import ru.anastasia.NauJava.entity.note.Note;
import ru.anastasia.NauJava.entity.socialprofile.SocialProfile;
import ru.anastasia.NauJava.entity.tag.ContactTag;
import ru.anastasia.NauJava.entity.tag.Tag;
import ru.anastasia.NauJava.exception.tag.TagNotFoundException;
import ru.anastasia.NauJava.service.company.CompanyService;
import ru.anastasia.NauJava.service.company.JobTitleService;
import ru.anastasia.NauJava.service.contact.ContactDetailService;
import ru.anastasia.NauJava.service.contact.ContactManagementService;
import ru.anastasia.NauJava.service.contact.ContactService;
import ru.anastasia.NauJava.service.event.EventService;
import ru.anastasia.NauJava.service.facade.ContactDetailFacadeService;
import ru.anastasia.NauJava.service.facade.ContactEventManagementService;
import ru.anastasia.NauJava.service.facade.ContactTagFacadeService;
import ru.anastasia.NauJava.service.facade.dto.ContactFullDetails;
import ru.anastasia.NauJava.service.note.NoteService;
import ru.anastasia.NauJava.service.socialprofile.SocialProfileService;
import ru.anastasia.NauJava.service.tag.TagService;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContactManagementServiceImpl implements ContactManagementService {

    /**
     * Сервис способов связи
     */
    private final ContactDetailService contactDetailService;

    /**
     * Сервис тегов
     */
    private final TagService tagService;

    /**
     * Сервис событий контактов
     */
    private final EventService eventService;

    /**
     * Сервис профилей в соцсетях
     */
    private final SocialProfileService socialProfileService;

    /**
     * Сервис заметок
     */
    private final NoteService noteService;

    /**
     * Сервис контактов
     */
    private final ContactService contactService;

    /**
     * Сервис управления событиями контактов
     */
    private final ContactEventManagementService contactEventManagementService;

    /**
     * Фасад для операций с контактами и тегами
     */
    private final ContactTagFacadeService tagFacade;

    /**
     * Фасад для операций с контактами и способами связи
     */
    private final ContactDetailFacadeService detailFacade;

    /**
     * Сервис компаний
     */
    private final CompanyService companyService;

    /**
     * Сервис должностей
     */
    private final JobTitleService jobTitleService;

    @Transactional
    @Override
    public Contact create(ContactCreateDto contactCreateDto) {
        log.info("Создание нового контакта: {} {}", contactCreateDto.getFirstName(), contactCreateDto.getLastName());

        Contact contact = buildContactFromCreateDto(contactCreateDto);
        contact = contactService.save(contact);
        log.debug("Базовый контакт создан с ID: {}", contact.getId());

        addCompaniesToContact(contact, contactCreateDto.getCompanies());
        addContactDetailsToContact(contact, contactCreateDto.getContactDetails());
        addSocialProfilesToContact(contact, contactCreateDto.getSocialProfiles());
        addEventsToContact(contact, contactCreateDto.getEvents());
        addNotesToContact(contact, contactCreateDto.getNotes());
        addTagsToContact(contact, contactCreateDto.getTagIds());

        Contact savedContact = contactService.save(contact);
        log.info("Контакт {} {} успешно создан с ID: {}", savedContact.getFirstName(), savedContact.getLastName(), savedContact.getId());
        return savedContact;
    }

    @Transactional
    @Override
    public Contact update(ContactUpdateDto contactUpdateDto) {
        log.info("Обновление контакта с ID: {}", contactUpdateDto.getId());

        Contact contact = contactService.findById(contactUpdateDto.getId());
        log.debug("Контакт с ID: {} найден для обновления", contactUpdateDto.getId());

        updateContactBasicInfo(contact, contactUpdateDto);
        updateContactCompanies(contact, contactUpdateDto.getCompanies());
        updateContactDetails(contact, contactUpdateDto.getContactDetails());
        updateSocialProfiles(contact, contactUpdateDto.getSocialProfiles());
        updateContactTags(contact, contactUpdateDto.getTagIds());
        updateContactNotes(contact, contactUpdateDto.getNotes());
        updateContactEvents(contact, contactUpdateDto.getEvents());

        Contact updatedContact = contactService.save(contact);
        log.info("Контакт с ID: {} успешно обновлен", contactUpdateDto.getId());
        return updatedContact;
    }

    @Override
    @Transactional
    public void delete(Long contactId) {
        log.info("Удаление контакта с ID: {}", contactId);
        contactService.deleteById(contactId);
        log.info("Контакт с ID: {} успешно удален", contactId);
    }

    @Transactional(readOnly = true)
    @Override
    public ContactFullDetails getWithAllDetails(Long contactId) {
        log.debug("Запрос полной информации о контакте с ID: {}", contactId);

        Contact contact = contactService.findById(contactId);
        List<ContactDetail> contactDetails = contactDetailService.findByContactId(contactId);
        List<SocialProfile> socialProfiles = socialProfileService.findByContactId(contactId);
        List<Event> events = eventService.findByContactId(contactId);
        List<Note> notes = noteService.findByContactId(contactId);
        List<Tag> tags = tagService.findByContactId(contactId);
        List<ContactTag> contactTags = tagService.findContactTagsByContactId(contactId);
        Event birthday = eventService.findBirthdayByContactId(contactId);
        List<ContactDetail> primaryDetails = contactDetailService.findPrimaryByContactId(contactId);

        log.debug("Полная информация о контакте с ID: {} собрана ({} событий, {} заметок, {} тегов)",
                contactId, events.size(), notes.size(), tags.size());

        return ContactFullDetails.builder()
                .contact(contact)
                .contactDetails(contactDetails)
                .primaryContactDetails(primaryDetails)
                .socialProfiles(socialProfiles)
                .events(events)
                .birthday(birthday)
                .notes(notes)
                .tags(tags)
                .contactTags(contactTags)
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public ContactFullDetails getSummary(Long contactId) {
        log.debug("Запрос краткой информации о контакте с ID: {}", contactId);

        Contact contact = contactService.findById(contactId);
        List<ContactDetail> primaryDetails = contactDetailService.findPrimaryByContactId(contactId);
        List<SocialProfile> mainSocialProfiles = socialProfileService.findByContactId(contactId);
        Event birthday = eventService.findBirthdayByContactId(contactId);
        List<Tag> tags = tagService.findByContactId(contactId);

        return ContactFullDetails.builder()
                .contact(contact)
                .primaryContactDetails(primaryDetails)
                .socialProfiles(mainSocialProfiles)
                .birthday(birthday)
                .tags(tags)
                .build();
    }

    @Transactional
    @Override
    public Contact duplicate(Long contactId, String newFirstName, String newLastName) {
        log.info("Дублирование контакта с ID: {}", contactId);

        ContactFullDetails originalDetails = getWithAllDetails(contactId);
        Contact original = originalDetails.getContact();

        String duplicateFirstName = getDuplicateFirstName(newFirstName, original);
        String duplicateLastName = getDuplicateLastName(newLastName, original);

        Contact savedDuplicate = contactService.add(duplicateFirstName, duplicateLastName);
        log.debug("Базовый дубликат создан с ID: {}", savedDuplicate.getId());

        savedDuplicate = updateDuplicateBasicInfo(savedDuplicate, original);

        duplicateContactDetails(originalDetails, savedDuplicate);
        duplicateSocialProfiles(originalDetails, savedDuplicate);
        duplicateEvents(originalDetails, savedDuplicate);
        duplicateTags(originalDetails, savedDuplicate);
        duplicateNotes(originalDetails, savedDuplicate);

        Contact result = contactService.findById(savedDuplicate.getId());
        log.info("Контакт с ID: {} успешно продублирован в контакт с ID: {}", contactId, result.getId());
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ContactFullDetails> getListWithUpcomingBirthdays(int daysAhead) {
        log.debug("Поиск контактов с предстоящими днями рождения в течение {} дней", daysAhead);

        List<Contact> contactsWithBirthdays = contactService.findWithUpcomingBirthdays(daysAhead);
        List<ContactFullDetails> result = contactsWithBirthdays.stream()
                .map(contact -> {
                    ContactFullDetails details = getSummary(contact.getId());
                    Event birthday = eventService.findBirthdayByContactId(contact.getId());
                    if (birthday != null) {
                        details.setBirthday(birthday);
                        details.setDaysUntil(calculateDaysUntil(birthday.getEventDate()));
                    }
                    return details;
                })
                .filter(details -> details.getBirthday() != null)
                .sorted(Comparator.comparing(details -> details.getBirthday().getEventDate()))
                .toList();

        log.debug("Найдено {} контактов с предстоящими днями рождения", result.size());
        return result;
    }

    private void addEventsToContact(Contact contact, List<EventCreateDto> eventDtos) {
        if (eventDtos.isEmpty()) {
            log.trace("Нет событий для добавления к контакту");
            return;
        }

        log.debug("Добавление {} событий к контакту", eventDtos.size());
        eventDtos.forEach(dto -> contact.addEvent(Event.builder()
                .contact(contact)
                .eventType(dto.getEventType())
                .customEventName(dto.getCustomEventName())
                .eventDate(dto.getEventDate())
                .notes(dto.getNotes())
                .yearlyRecurrence(dto.getYearlyRecurrence())
                .build()));
    }

    private void addNotesToContact(Contact contact, List<NoteCreateDto> noteDtos) {
        if (noteDtos.isEmpty()) {
            log.trace("Нет заметок для добавления к контакту");
            return;
        }

        log.debug("Добавление {} заметок к контакту", noteDtos.size());
        noteDtos.forEach(dto -> contact.addNote(Note.builder()
                .contact(contact)
                .content(dto.getContent())
                .build()));
    }

    private Contact buildContactFromCreateDto(ContactCreateDto dto) {
        return Contact.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .displayName(dto.getDisplayName())
                .avatarUrl(dto.getAvatarUrl())
                .isFavorite(dto.getIsFavorite())
                .build();
    }

    private int calculateDaysUntil(LocalDate birthday) {
        LocalDate today = LocalDate.now();
        LocalDate nextBirthday = birthday.withYear(today.getYear());

        if (nextBirthday.isBefore(today)) {
            nextBirthday = nextBirthday.plusYears(1);
        }

        return (int) ChronoUnit.DAYS.between(today, nextBirthday);
    }

    private void addCompaniesToContact(Contact contact, List<ContactCompanyCreateDto> companyDtos) {
        if (companyDtos.isEmpty()) {
            log.trace("Нет компаний для добавления к контакту");
            return;
        }

        log.debug("Добавление {} компаний к контакту", companyDtos.size());
        companyDtos.forEach(dto -> {
            Company company = companyService.findById(dto.getCompanyId());
            JobTitle jobTitle = jobTitleService.findById(dto.getJobTitleId());
            contact.addCompany(ContactCompany.builder()
                    .contact(contact)
                    .company(company)
                    .jobTitle(jobTitle)
                    .isCurrent(dto.getIsCurrent())
                    .build());
        });
    }

    private void addContactDetailsToContact(Contact contact, List<ContactDetailCreateDto> detailDtos) {
        if (detailDtos.isEmpty()) return;

        detailDtos.forEach(dto -> contact.addContactDetail(ContactDetail.builder()
                .contact(contact)
                .detailType(dto.getDetailType())
                .label(dto.getLabel())
                .value(dto.getValue())
                .isPrimary(dto.getIsPrimary())
                .build()));
    }

    private void addSocialProfilesToContact(Contact contact, List<SocialProfileCreateDto> profileDtos) {
        if (profileDtos.isEmpty()) {
            log.trace("Нет социальных профилей для добавления к контакту");
            return;
        }

        log.debug("Добавление {} социальных профилей к контакту", profileDtos.size());
        profileDtos.forEach(dto -> contact.addSocialProfile(SocialProfile.builder()
                .contact(contact)
                .platform(dto.getPlatform())
                .customPlatformName(dto.getCustomPlatformName())
                .username(dto.getUsername())
                .profileUrl(dto.getProfileUrl())
                .build()));
    }

    private void addTagsToContact(Contact contact, Set<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            log.trace("Нет тегов для добавления к контакту");
            return;
        }

        log.debug("Добавление {} тегов к контакту", tagIds.size());
        List<Tag> tags = tagService.findAllById(tagIds);

        if (tags.size() != tagIds.size()) {
            Set<Long> foundTagIds = tags.stream()
                    .map(Tag::getId)
                    .collect(Collectors.toSet());
            Set<Long> missingTagIds = tagIds.stream()
                    .filter(id -> !foundTagIds.contains(id))
                    .collect(Collectors.toSet());

            log.error("Не найдены теги с ID: {}", missingTagIds);
            throw new TagNotFoundException("Не найдены теги с ID: " + missingTagIds);
        }

        tags.forEach(tag -> contact.addContactTag(ContactTag.builder()
                .tag(tag)
                .contact(contact)
                .build()));
    }

    private void updateContactBasicInfo(Contact contact, ContactUpdateDto dto) {
        contact.setFirstName(dto.getFirstName());
        contact.setLastName(dto.getLastName());
        contact.setDisplayName(dto.getDisplayName());
        contact.setAvatarUrl(dto.getAvatarUrl());
        contact.setIsFavorite(dto.getIsFavorite());
    }

    private void updateContactCompanies(Contact contact, List<ContactCompanyUpdateDto> companyDtos) {
        log.debug("Обновление компаний контакта с ID: {}. Старое количество: {}", contact.getId(), contact.getCompanies().size());
        validateContactCompanies(companyDtos);

        List<ContactCompany> oldCompanies = List.copyOf(contact.getCompanies());
        contact.getCompanies().clear();

        companyDtos.forEach(dto -> {
            Company company = companyService.findById(dto.getCompanyId());
            JobTitle jobTitle = jobTitleService.findById(dto.getJobTitleId());

            ContactCompany contactCompany = findOrCreateContactCompany(oldCompanies, dto.getId(), contact);
            contactCompany.setCompany(company);
            contactCompany.setJobTitle(jobTitle);
            contactCompany.setIsCurrent(dto.getIsCurrent());
            contact.addCompany(contactCompany);
        });
        log.debug("Обновлено компаний контакта с ID: {}. Новое количество: {}", contact.getId(), companyDtos.size());
    }

    private void validateContactCompanies(List<ContactCompanyUpdateDto> companyDtos) {
        if (companyDtos == null) {
            return;
        }

        long currentCount = companyDtos.stream()
                .filter(dto -> dto.getIsCurrent() != null && dto.getIsCurrent())
                .count();

        if (currentCount > 1) {
            throw new IllegalArgumentException("Только одно место работы может быть текущим");
        }
    }

    private void updateContactDetails(Contact contact, List<ru.anastasia.NauJava.dto.contact.ContactDetailUpdateDto> detailDtos) {
        List<ContactDetail> oldDetails = List.copyOf(contact.getContactDetails());
        contact.getContactDetails().clear();

        detailDtos.forEach(dto -> {
            ContactDetail contactDetail = findOrCreateContactDetail(oldDetails, dto.getId(), contact);
            contactDetail.setDetailType(dto.getDetailType());
            contactDetail.setLabel(dto.getLabel());
            contactDetail.setValue(dto.getValue());
            contactDetail.setIsPrimary(dto.getIsPrimary());
            contact.addContactDetail(contactDetail);
        });
    }

    private void updateSocialProfiles(Contact contact, List<SocialProfileUpdateDto> profileDtos) {
        List<SocialProfile> oldProfiles = List.copyOf(contact.getSocialProfiles());
        contact.getSocialProfiles().clear();

        profileDtos.forEach(dto -> {
            SocialProfile socialProfile = findOrCreateSocialProfile(oldProfiles, dto.getId(), contact);
            socialProfile.setPlatform(dto.getPlatform());
            socialProfile.setCustomPlatformName(dto.getCustomPlatformName());
            socialProfile.setUsername(dto.getUsername());
            socialProfile.setProfileUrl(dto.getProfileUrl());
            contact.addSocialProfile(socialProfile);
        });
    }

    private void updateContactTags(Contact contact, Set<Long> tagIds) {
        List<Tag> newTags = tagService.findAllById(tagIds);

        if (newTags.size() != tagIds.size()) {
            Set<Long> foundTagIds = newTags.stream()
                    .map(Tag::getId)
                    .collect(Collectors.toSet());
            Set<Long> missingTagIds = tagIds.stream()
                    .filter(id -> !foundTagIds.contains(id))
                    .collect(Collectors.toSet());

            throw new TagNotFoundException("Не найдены теги с ID: " + missingTagIds);
        }

        contact.getContactTags().clear();

        newTags.forEach(tag -> contact.addContactTag(ContactTag.builder()
                .contact(contact)
                .tag(tag)
                .build()));
    }

    private void updateContactNotes(Contact contact, List<NoteUpdateDto> noteDtos) {
        List<Note> oldNotes = List.copyOf(contact.getNotes());
        contact.getNotes().clear();

        noteDtos.forEach(dto -> {
            Note note = findOrCreateNote(oldNotes, dto.getId(), contact);
            note.setContent(dto.getContent());
            contact.addNote(note);
        });
    }

    private void updateContactEvents(Contact contact, List<EventUpdateDto> eventDtos) {
        List<Event> oldEvents = List.copyOf(contact.getEvents());
        contact.getEvents().clear();

        eventDtos.forEach(dto -> {
            Event event = findOrCreateEvent(oldEvents, dto.getId(), contact);
            event.setEventType(dto.getEventType());
            event.setCustomEventName(dto.getCustomEventName());
            event.setEventDate(dto.getEventDate());
            event.setNotes(dto.getNotes());
            event.setYearlyRecurrence(dto.getYearlyRecurrence());
            contact.addEvent(event);
        });
    }

    private ContactCompany findOrCreateContactCompany(List<ContactCompany> oldCompanies, Long id, Contact contact) {
        return oldCompanies.stream()
                .filter(cc -> Objects.equals(cc.getId(), id))
                .findFirst()
                .orElse(ContactCompany.builder().id(id).contact(contact).build());
    }

    private ContactDetail findOrCreateContactDetail(List<ContactDetail> oldDetails, Long id, Contact contact) {
        return oldDetails.stream()
                .filter(cd -> Objects.equals(cd.getId(), id))
                .findFirst()
                .orElse(ContactDetail.builder().id(id).contact(contact).build());
    }

    private SocialProfile findOrCreateSocialProfile(List<SocialProfile> oldProfiles, Long id, Contact contact) {
        return oldProfiles.stream()
                .filter(sp -> Objects.equals(sp.getId(), id))
                .findFirst()
                .orElse(SocialProfile.builder().id(id).contact(contact).build());
    }

    private Note findOrCreateNote(List<Note> oldNotes, Long id, Contact contact) {
        return oldNotes.stream()
                .filter(n -> Objects.equals(n.getId(), id))
                .findFirst()
                .orElse(Note.builder().id(id).contact(contact).build());
    }

    private Event findOrCreateEvent(List<Event> oldEvents, Long id, Contact contact) {
        return oldEvents.stream()
                .filter(e -> Objects.equals(e.getId(), id))
                .findFirst()
                .orElse(Event.builder().id(id).contact(contact).build());
    }

    private String getDuplicateFirstName(String newFirstName, Contact original) {
        String result = newFirstName != null ? newFirstName : original.getFirstName() + " (копия)";
        log.trace("Имя для дубликата: '{}' (оригинал: '{}')", result, original.getFirstName());
        return result;
    }

    private String getDuplicateLastName(String newLastName, Contact original) {
        String result = newLastName != null ? newLastName : original.getLastName();
        log.trace("Фамилия для дубликата: '{}' (оригинал: '{}')", result, original.getLastName());
        return result;
    }

    private Contact updateDuplicateBasicInfo(Contact duplicate, Contact original) {
        return contactService.update(
                duplicate.getId(),
                duplicate.getFirstName(),
                duplicate.getLastName(),
                original.getDisplayName(),
                original.getAvatarUrl(),
                false
        );
    }

    private void duplicateContactDetails(ContactFullDetails originalDetails, Contact duplicate) {
        if (originalDetails.getContactDetails().isEmpty()) return;

        List<ContactDetailCreateDto> detailDtos = originalDetails.getContactDetails().stream()
                .map(detail -> ContactDetailCreateDto.builder()
                        .detailType(detail.getDetailType())
                        .label(detail.getLabel())
                        .value(detail.getValue())
                        .isPrimary(detail.getIsPrimary())
                        .build())
                .toList();
        detailFacade.addDetailsToContact(duplicate.getId(), detailDtos);
    }

    private void duplicateSocialProfiles(ContactFullDetails originalDetails, Contact duplicate) {
        if (originalDetails.getSocialProfiles().isEmpty()) return;

        originalDetails.getSocialProfiles().forEach(profile -> {
            SocialProfile newProfile = SocialProfile.builder()
                    .platform(profile.getPlatform())
                    .customPlatformName(profile.getCustomPlatformName())
                    .username(profile.getUsername())
                    .profileUrl(profile.getProfileUrl())
                    .contact(duplicate)
                    .build();
            socialProfileService.create(newProfile);
        });
    }

    private void duplicateEvents(ContactFullDetails originalDetails, Contact duplicate) {
        if (originalDetails.getEvents().isEmpty()) return;

        List<EventCreateDto> eventDtos = originalDetails.getEvents().stream()
                .filter(event -> event.getEventType() != ru.anastasia.NauJava.entity.enums.EventType.BIRTHDAY)
                .map(event -> EventCreateDto.builder()
                        .eventType(event.getEventType())
                        .customEventName(event.getCustomEventName())
                        .eventDate(event.getEventDate())
                        .notes(event.getNotes())
                        .yearlyRecurrence(event.getYearlyRecurrence())
                        .build())
                .toList();
        contactEventManagementService.addEventsToContact(duplicate.getId(), eventDtos);
    }

    private void duplicateTags(ContactFullDetails originalDetails, Contact duplicate) {
        if (originalDetails.getTags().isEmpty()) return;

        List<String> tagNames = originalDetails.getTags().stream()
                .map(Tag::getName)
                .toList();
        tagFacade.addTagsToContact(duplicate.getId(), tagNames);
    }

    private void duplicateNotes(ContactFullDetails originalDetails, Contact duplicate) {
        if (originalDetails.getNotes().isEmpty()) return;

        originalDetails.getNotes().forEach(note -> {
            Note newNote = Note.builder()
                    .content(note.getContent())
                    .contact(duplicate)
                    .build();
            noteService.create(duplicate.getId(), newNote);
        });
    }
}