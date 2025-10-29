package ru.anastasia.NauJava.repository.tag;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.anastasia.NauJava.entity.tag.ContactTag;

import java.util.List;

/**
 * Репозиторий связи контактов и тегов
 */
@Repository
public interface ContactTagRepository extends CrudRepository<ContactTag, Long> {
    /**
     * Получить связи тегов по идентификатору контакта
     *
     * @param contactId Идентификатор контакта
     * @return Список связей тегов
     */
    List<ContactTag> findByContactId(Long contactId);

    /**
     * Проверить, привязан ли тег к контакту
     *
     * @param contactId Идентификатор контакта
     * @param id        Идентификатор тега
     * @return Да / Нет
     */
    boolean existsByContactIdAndTagId(Long contactId, Long id);
}