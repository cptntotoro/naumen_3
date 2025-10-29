package ru.anastasia.NauJava.service.tag;

import ru.anastasia.NauJava.entity.tag.ContactTag;
import ru.anastasia.NauJava.entity.tag.Tag;

import java.util.Collection;
import java.util.List;

/**
 * Сервис тегов
 */
public interface TagService {

    /**
     * Создать тег
     *
     * @param tag Тег
     * @return Тег
     */
    Tag create(Tag tag);

    /**
     * Найти тег по имени
     *
     * @param name Название тега
     * @return Тег
     */
    Tag findByName(String name);

    /**
     * Добавить тег к контакту
     *
     * @param contactId Идентификатор контакта
     * @param tagName   Название тега
     * @return Тег контакта
     */
    ContactTag addToContact(Long contactId, String tagName);

    /**
     * Получить теги по идентификатору контакта
     *
     * @param contactId Идентификатор контакта
     * @return Список тегов
     */
    List<Tag> findByContactId(Long contactId);

    /**
     * Получить все теги
     *
     * @return Список тегов
     */
    List<Tag> findAll();

    /**
     * Получить тег по идентификатору
     *
     * @param id Идентификатор
     * @return Тег
     */
    Tag findById(Long id);

    List<Tag> findAllById(Collection<Long> ids);

    /**
     * Обновить тег по идентификатору
     *
     * @param tag Тег
     * @return Тег
     */
    Tag update(Tag tag);

    /**
     * Удалить тег по идентификатору
     *
     * @param id Идентификатор
     */
    void delete(Long id);

    /**
     * Получить теги контакта
     *
     * @param contactId Идентификатор контакта
     * @return Список тегов контакта
     */
    List<ContactTag> findContactTagsByContactId(Long contactId);

    /**
     * Удалить связь контакта с тегом
     *
     * @param contactTagId Идентификатор связи контакт-тег
     */
    void deleteContactTag(Long contactTagId);
}