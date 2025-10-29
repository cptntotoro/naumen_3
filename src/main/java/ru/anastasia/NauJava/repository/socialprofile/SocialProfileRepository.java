package ru.anastasia.NauJava.repository.socialprofile;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.anastasia.NauJava.entity.enums.SocialPlatform;
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

    /**
     * Получить профили в соцсетях по названию платформы
     *
     * @param platform Тип социальной платформы
     * @return Список профилей в соцсетях
     */
    List<SocialProfile> findByPlatform(SocialPlatform platform);

    /**
     * Получить профили в соцсетях для избранных контактов
     *
     * @return Список профилей в соцсетях
     */
    @Query("SELECT sp FROM SocialProfile sp WHERE sp.contact.isFavorite = true")
    List<SocialProfile> findByFavoriteContacts();

    /**
     * Получить профили в соцсетях по платформе и имени пользователя
     *
     * @param platform Тип социальной платформы
     * @param username Имя пользователя
     * @return Список профилей в соцсетях
     */
    List<SocialProfile> findByPlatformAndUsernameContainingIgnoreCase(SocialPlatform platform, String username);
}
