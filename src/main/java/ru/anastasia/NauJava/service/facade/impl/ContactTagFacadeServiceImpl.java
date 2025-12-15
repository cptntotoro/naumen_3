package ru.anastasia.NauJava.service.facade.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.entity.tag.ContactTag;
import ru.anastasia.NauJava.service.contact.ContactService;
import ru.anastasia.NauJava.service.facade.ContactTagFacadeService;
import ru.anastasia.NauJava.service.tag.TagService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
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
        log.info("Добавление {} тегов к контакту ID: {}. Теги: {}",
                tagNames.size(), contactId, String.join(", ", tagNames));

        Contact contact = contactService.findById(contactId);
        log.debug("Контакт найден: ID: {}, имя: {}", contactId, contact.getFullName());

        List<ContactTag> createdTags = new ArrayList<>();
        int successCount = 0;
        int errorCount = 0;

        for (String tagName : tagNames) {
            try {
                log.trace("Добавление тега '{}' к контакту ID: {}", tagName, contactId);

                ContactTag contactTag = tagService.addToContact(contactId, tagName);
                createdTags.add(contactTag);
                successCount++;

                log.debug("Тег '{}' успешно добавлен к контакту ID: {}. ID связи: {}",
                        tagName, contactId, contactTag.getId());

            } catch (Exception e) {
                errorCount++;
                log.warn("Не удалось добавить тег '{}' к контакту ID: {}. Причина: {}",
                        tagName, contactId, e.getMessage(), e);
            }
        }

        if (errorCount > 0) {
            log.warn("Добавление тегов завершено с ошибками. Контакт ID: {}. Успешно: {}, с ошибками: {}",
                    contactId, successCount, errorCount);
        } else {
            log.info("Успешно добавлено {} тегов к контакту ID: {}", successCount, contactId);
        }

        return createdTags;
    }
}