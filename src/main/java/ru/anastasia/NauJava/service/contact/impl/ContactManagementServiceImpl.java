package ru.anastasia.NauJava.service.contact.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.entity.contact.ContactDetail;
import ru.anastasia.NauJava.entity.contact.Event;
import ru.anastasia.NauJava.entity.contact.SocialProfile;
import ru.anastasia.NauJava.entity.tag.Tag;
import ru.anastasia.NauJava.repository.contact.ContactRepository;
import ru.anastasia.NauJava.service.contact.ContactDetailService;
import ru.anastasia.NauJava.service.contact.ContactManagementService;
import ru.anastasia.NauJava.service.contact.EventService;
import ru.anastasia.NauJava.service.contact.NoteService;
import ru.anastasia.NauJava.service.contact.SocialProfileService;
import ru.anastasia.NauJava.service.tag.TagService;

import java.util.List;

@Service
public class ContactManagementServiceImpl implements ContactManagementService {
    /**
     * Репозиторий контактов
     */
    private final ContactRepository contactRepository;

    /**
     * Сервис способов связи
     */
    private final ContactDetailService contactDetailService;

    /**
     * Сервис управления тегами
     */
    private final TagService tagService;

    /**
     * Сервис управления событиями контактов
     */
    private final EventService eventService;

    /**
     * Сервис управления профилями в соцсетях
     */
    private final SocialProfileService socialProfileService;

    /**
     * Сервис управления заметками
     */
    private final NoteService noteService;

    @Autowired
    public ContactManagementServiceImpl(ContactRepository contactRepository,
                                        ContactDetailService contactDetailService,
                                        TagService tagService,
                                        EventService eventService,
                                        SocialProfileService socialProfileService,
                                        NoteService noteService) {
        this.contactRepository = contactRepository;
        this.contactDetailService = contactDetailService;
        this.tagService = tagService;
        this.eventService = eventService;
        this.socialProfileService = socialProfileService;
        this.noteService = noteService;
    }

    @Transactional
    @Override
    public Contact createWithDetails(String firstName, String lastName, String company, String jobTitle,
            List<ContactDetail> contactDetails, List<SocialProfile> socialProfiles,
            List<Event> events, List<String> tagNames, List<String> notes) {

        Contact contact = new Contact();
        contact.setFirstName(firstName);
        contact.setLastName(lastName);

        Contact savedContact = contactRepository.save(contact);

        if (contactDetails != null) {
            for (ContactDetail detail : contactDetails) {
                detail.setContact(savedContact);
                contact.getContactDetails().add(contactDetailService.create(detail));
            }
        }

        if (socialProfiles != null) {
            for (SocialProfile profile : socialProfiles) {
                profile.setContact(savedContact);
                contact.getSocialProfiles().add(socialProfileService.create(profile));
            }
        }

        if (events != null) {
            for (Event event : events) {
                event.setContact(savedContact);
                contact.getEvents().add(eventService.createEvent(event));
            }
        }

        if (tagNames != null) {
            for (String tagName : tagNames) {
                contact.getContactTags().add(tagService.addToContact(savedContact.getId(), tagName));
            }
        }

        if (notes != null) {
            for (String noteContent : notes) {
                contact.getNotes().add(noteService.create(savedContact.getId(), noteContent));
            }
        }

        return contactRepository.save(contact);
    }

    @Transactional
    @Override
    public void delete(Long contactId) {
        if (!contactRepository.existsById(contactId)) {
            throw new RuntimeException("Не найден контакт с id: " + contactId);
        }
        contactRepository.deleteById(contactId);
    }

    @Transactional
    @Override
    public Contact duplicate(Long contactId, String newFirstName, String newLastName) {
        Contact original = contactRepository.findById(contactId)
                .orElseThrow(() -> new RuntimeException("Не найден контакт с id: " + contactId));

        Contact duplicate = new Contact();
        duplicate.setFirstName(newFirstName != null ? newFirstName : original.getFirstName());
        duplicate.setLastName(newLastName != null ? newLastName : original.getLastName());

        duplicate = contactRepository.save(duplicate);

        for (ContactDetail originalDetail : contactDetailService.findByContactId(contactId)) {
            ContactDetail duplicateDetail = new ContactDetail();
            duplicateDetail.setContact(duplicate);
            duplicateDetail.setDetailType(originalDetail.getDetailType());
            duplicateDetail.setLabel(originalDetail.getLabel());
            duplicateDetail.setValue(originalDetail.getValue());
            duplicateDetail.setIsPrimary(originalDetail.getIsPrimary());
            duplicate.getContactDetails().add(contactDetailService.create(duplicateDetail));
        }

        for (String tagName : tagService.findByContactId(contactId).stream()
                .map(Tag::getName)
                .toList()) {
            duplicate.getContactTags().add(tagService.addToContact(duplicate.getId(), tagName));
        }

        return contactRepository.save(duplicate);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Contact> searchComplex(String firstName, String lastName, String company, String jobTitle) {
        return contactRepository.findContactsByComplexCriteria(firstName, lastName, company, jobTitle);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Contact> findWithUpcomingBirthdays(int daysAhead) {
        return eventService.getBirthdaysThisWeek();
    }

    @Override
    @Transactional
    public Contact updateWithDetails(Long contactId, Contact contact, List<ContactDetail> contactDetails) {
        Contact existingContact = contactRepository.findById(contactId)
                .orElseThrow(() -> new RuntimeException("Не найден контакт с id: " + contactId));

        existingContact.setFirstName(contact.getFirstName());
        existingContact.setLastName(contact.getLastName());
        existingContact.setDisplayName(contact.getDisplayName());
        existingContact.setAvatarUrl(contact.getAvatarUrl());
        existingContact.setIsFavorite(contact.getIsFavorite());

        List<ContactDetail> existingDetails = contactDetailService.findByContactId(contactId);
        existingDetails.forEach(detail -> contactDetailService.delete(detail.getId()));

        if (contactDetails != null) {
            for (ContactDetail detail : contactDetails) {
                detail.setContact(existingContact);
                existingContact.getContactDetails().add(contactDetailService.create(detail));
            }
        }

        return contactRepository.save(existingContact);
    }
}
