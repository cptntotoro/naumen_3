package ru.anastasia.NauJava.service.socialprofile;

import ru.anastasia.NauJava.entity.socialprofile.SocialProfile;

import java.util.List;

/**
 * Сервис профилей в соцсетях
 */
public interface SocialProfileService {

    /**
     * Создать профиль в соцсети
     *
     * @param socialProfile Профиль
     * @return Созданный профиль
     */
    SocialProfile create(SocialProfile socialProfile);

    /**
     * Создать социальный профиль для контакта
     *
     * @param contactId Идентификатор контакта
     * @param profile   Социальный профиль
     * @return Созданный социальный профиль
     */
    SocialProfile createForContact(Long contactId, SocialProfile profile);

    /**
     * Получить профили по идентификатору контакта
     *
     * @param contactId Идентификатор контакта
     * @return Список профилей
     */
    List<SocialProfile> findByContactId(Long contactId);

    /**
     * Получить профиль в соцсети по идентификатору
     *
     * @param id Идентификатор
     * @return Профиль в соцсети
     */
    SocialProfile findById(Long id);

    /**
     * Обновить профиль в соцсети
     *
     * @param profile Профиль в соцсети
     * @return Профиль в соцсети
     */
    SocialProfile update(SocialProfile profile);

    /**
     * Обновить профиль в соцсети
     *
     * @param id Идентификатор
     */
    void delete(Long id);
}