package ru.anastasia.NauJava.service.facade.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.anastasia.NauJava.entity.tag.ContactTag;
import ru.anastasia.NauJava.service.contact.ContactService;
import ru.anastasia.NauJava.service.facade.ContactTagFacadeService;
import ru.anastasia.NauJava.service.tag.TagService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ContactTagFacadeServiceImpl implements ContactTagFacadeService {
    /**
     * Сервис контактов
     */
    private final ContactService contactService;

    /**
     * Сервис тегов
     */
    private final TagService tagService;

    @Override
    public List<ContactTag> addTagsToContact(Long contactId, List<String> tagNames) {
        contactService.findById(contactId);

        List<ContactTag> createdTags = new ArrayList<>();
        for (String tagName : tagNames) {
            ContactTag contactTag = tagService.addToContact(contactId, tagName);
            createdTags.add(contactTag);
        }

        return createdTags;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContactTag> getContactTags(Long contactId) {
        contactService.findById(contactId);
        return tagService.findContactTagsByContactId(contactId);
    }
}
