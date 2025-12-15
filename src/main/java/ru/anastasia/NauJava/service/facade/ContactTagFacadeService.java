package ru.anastasia.NauJava.service.facade;

import ru.anastasia.NauJava.entity.tag.ContactTag;

import java.util.List;

/**
 * Фасад для операций с контактами и тегами
 */
public interface ContactTagFacadeService {

    /**
     * Добавить теги к контакту
     *
     * @param contactId Идентификатор контакта
     * @param tagNames  Названия тегов
     * @return Список тегов контакта
     */
    List<ContactTag> addTagsToContact(Long contactId, List<String> tagNames);
}
