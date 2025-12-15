package ru.anastasia.NauJava.repository.tag;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.anastasia.NauJava.entity.tag.Tag;

import java.util.Collection;
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
     * Получить теги по идентификаторам
     *
     * @param ids Список идентификаторов тегов
     * @return Список тегов
     */
    List<Tag> getTagsByIdIsIn(Collection<Long> ids);
}
