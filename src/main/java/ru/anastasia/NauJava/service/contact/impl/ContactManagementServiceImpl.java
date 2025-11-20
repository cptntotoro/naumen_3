package ru.anastasia.NauJava.service.contact.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.anastasia.NauJava.dto.company.ContactCompanyCreateDto;
import ru.anastasia.NauJava.dto.company.ContactCompanyUpdateDto;
import ru.anastasia.NauJava.dto.contact.ContactCreateDto;
import ru.anastasia.NauJava.dto.contact.ContactDetailCreateDto;
import ru.anastasia.NauJava.dto.contact.ContactUpdateDto;
import ru.anastasia.NauJava.dto.event.EventCreateDto;
import ru.anastasia.NauJava.dto.event.EventUpdateDto;
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
import ru.anastasia.NauJava.service.facade.ContactEventFacadeService;
import ru.anastasia.NauJava.service.facade.ContactTagFacadeService;
import ru.anastasia.NauJava.service.facade.dto.ContactFullDetails;
import ru.anastasia.NauJava.service.note.NoteService;
import ru.anastasia.NauJava.service.socialprofile.SocialProfileService;
import ru.anastasia.NauJava.service.tag.TagService;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
     * Фасад для операций с контактами и событиями
     */
    private final ContactEventFacadeService eventFacade;

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
        Contact contact = buildContactFromCreateDto(contactCreateDto);
        contact = contactService.save(contact);

        addCompaniesToContact(contact, contactCreateDto.getContactCompanyCreateDtos());
        addContactDetailsToContact(contact, contactCreateDto.getContactDetailCreateDtos());
        addSocialProfilesToContact(contact, contactCreateDto.getSocialProfileCreateDtos());
        addTagsToContact(contact, contactCreateDto.getTagIds());

        return contactService.save(contact);
    }

    @Transactional
    @Override
    public Contact update(ContactUpdateDto contactUpdateDto) {
        Contact contact = contactService.findById(contactUpdateDto.getId());

        updateContactBasicInfo(contact, contactUpdateDto);
        updateContactCompanies(contact, contactUpdateDto.getCompanies());
        updateContactDetails(contact, contactUpdateDto.getContactDetails());
        updateSocialProfiles(contact, contactUpdateDto.getSocialProfiles());
        updateContactTags(contact, contactUpdateDto.getTagIds());
        updateContactNotes(contact, contactUpdateDto.getNotes());
        updateContactEvents(contact, contactUpdateDto.getEvents());

        return contactService.save(contact);
    }

    @Override
    @Transactional
    public void delete(Long contactId) {
        contactService.deleteById(contactId);
    }

    @Transactional(readOnly = true)
    @Override
    public ContactFullDetails getWithAllDetails(Long contactId) {
        Contact contact = contactService.findById(contactId);

        List<ContactDetail> contactDetails = contactDetailService.findByContactId(contactId);
        List<SocialProfile> socialProfiles = socialProfileService.findByContactId(contactId);
        List<Event> events = eventService.findByContactId(contactId);
        List<Note> notes = noteService.findByContactId(contactId);
        List<Tag> tags = tagService.findByContactId(contactId);
        List<ContactTag> contactTags = tagService.findContactTagsByContactId(contactId);
        Event birthday = eventService.findBirthdayByContactId(contactId);
        List<ContactDetail> primaryDetails = contactDetailService.findPrimaryByContactId(contactId);

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
        ContactFullDetails originalDetails = getWithAllDetails(contactId);
        Contact original = originalDetails.getContact();

        String duplicateFirstName = getDuplicateFirstName(newFirstName, original);
        String duplicateLastName = getDuplicateLastName(newLastName, original);

        Contact savedDuplicate = contactService.add(duplicateFirstName, duplicateLastName);
        savedDuplicate = updateDuplicateBasicInfo(savedDuplicate, original);

        duplicateContactDetails(originalDetails, savedDuplicate);
        duplicateSocialProfiles(originalDetails, savedDuplicate);
        duplicateEvents(originalDetails, savedDuplicate);
        duplicateTags(originalDetails, savedDuplicate);
        duplicateNotes(originalDetails, savedDuplicate);

        return contactService.findById(savedDuplicate.getId());
    }

    @Transactional(readOnly = true)
    @Override
    public List<ContactFullDetails> getListWithUpcomingBirthdays(int daysAhead) {
        List<Contact> contactsWithBirthdays = contactService.findBirthdaysThisMonth();
        return contactsWithBirthdays.stream()
                .map(contact -> getSummary(contact.getId()))
                .toList();
    }

    @Override
    @Transactional
    public List<ContactFullDetails> getListFavoriteWithDetails() {
        List<Contact> favoriteContacts = contactService.findFavorites();
        return favoriteContacts.stream()
                .map(contact -> getSummary(contact.getId()))
                .toList();
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

    private void addCompaniesToContact(Contact contact, List<ContactCompanyCreateDto> companyDtos) {
        if (companyDtos.isEmpty()) return;

        companyDtos.forEach(dto -> {
            Company company = companyService.findById(dto.getCompanyId());
            JobTitle jobTitle = jobTitleService.findById(dto.getJobTitleId());
            contact.addCompany(ContactCompany.builder()
                    .contact(contact)
                    .company(company)
                    .jobTitle(jobTitle)
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
        if (profileDtos.isEmpty()) return;

        profileDtos.forEach(dto -> contact.addSocialProfile(SocialProfile.builder()
                .contact(contact)
                .platform(dto.getPlatform())
                .customPlatformName(dto.getCustomPlatformName())
                .username(dto.getUsername())
                .profileUrl(dto.getProfileUrl())
                .build()));
    }

    private void addTagsToContact(Contact contact, Set<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) return;

        List<Tag> tags = tagService.findAllById(tagIds);

        if (tags.size() != tagIds.size()) {
            Set<Long> foundTagIds = tags.stream()
                    .map(Tag::getId)
                    .collect(Collectors.toSet());
            Set<Long> missingTagIds = tagIds.stream()
                    .filter(id -> !foundTagIds.contains(id))
                    .collect(Collectors.toSet());

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
        List<ContactCompany> oldCompanies = List.copyOf(contact.getCompanies());
        contact.getCompanies().clear();

        companyDtos.forEach(dto -> {
            Company company = companyService.findById(dto.getCompanyId());
            JobTitle jobTitle = jobTitleService.findById(dto.getJobTitleId());

            ContactCompany contactCompany = findOrCreateContactCompany(oldCompanies, dto.getId(), contact);
            contactCompany.setCompany(company);
            contactCompany.setJobTitle(jobTitle);
            contact.addCompany(contactCompany);
        });
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
        return newFirstName != null ? newFirstName : original.getFirstName() + " (копия)";
    }

    private String getDuplicateLastName(String newLastName, Contact original) {
        return newLastName != null ? newLastName : original.getLastName();
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
        eventFacade.addEventsToContact(duplicate.getId(), eventDtos);
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