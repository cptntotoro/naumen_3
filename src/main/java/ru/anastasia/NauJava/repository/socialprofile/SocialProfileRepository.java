package ru.anastasia.NauJava.repository.socialprofile;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.anastasia.NauJava.entity.socialprofile.SocialProfile;

import java.util.List;

/**
 * Репозиторий профилей в соцсетях
 */
@Repository
public interface SocialProfileRepository extends CrudRepository<SocialProfile, Long> {
    /**
     * Получить профили в соцсетях по идентификатору контакта
     *
     * @param contactId Идентификатор контакта
     * @return Список профилей в соцсетях
     */
    List<SocialProfile> findByContactId(Long contactId);
}
