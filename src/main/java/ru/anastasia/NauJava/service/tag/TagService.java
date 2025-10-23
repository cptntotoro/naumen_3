package ru.anastasia.NauJava.service.tag;

import ru.anastasia.NauJava.entity.tag.ContactTag;
import ru.anastasia.NauJava.entity.tag.Tag;

import java.util.List;

/**
 * Сервис управления тегами
 */
public interface TagService {

    /**
     * Создать тег
     *
     * @param name  Название тега
     * @param color Цвет тега
     * @return Тег
     */
    Tag create(String name, String color);

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
     */
    ContactTag addToContact(Long contactId, String tagName);

    /**
     * Получить теги по идентификатору контакта
     *
     * @param contactId Идентификатор контакта
     * @return Список тегов
     */
    List<Tag> findByContactId(Long contactId);
}