package ru.anastasia.NauJava.repository.tag;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.anastasia.NauJava.entity.tag.Tag;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий тегов
 */
@Repository
public interface TagRepository extends CrudRepository<Tag, Long> {
    /**
     * Получить тег по названию
     *
     * @param name Название
     * @return Тег
     */
    Optional<Tag> findByName(String name);

    /**
     * Получить теги по названию
     *
     * @param name Название
     * @return Список тегов
     */
    List<Tag> findByNameContainingIgnoreCase(String name);

    /**
     * Получить теги по идентификатору контакта
     *
     * @param contactId Идентификатор контакта
     * @return Список тегов
     */
    @Query("SELECT t FROM Tag t JOIN t.contactTags ct WHERE ct.contact.id = :contactId")
    List<Tag> findByContactId(@Param("contactId") Long contactId);

    /**
     * Получить теги по названию или цвету
     *
     * @param name  Название
     * @param color Код цвета
     * @return Список тегов
     */
    List<Tag> findByNameContainingIgnoreCaseOrColorContainingIgnoreCase(String name, String color);
}
