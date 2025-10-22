package ru.anastasia.NauJava.service.contact;

import ru.anastasia.NauJava.entity.contact.SocialProfile;

import java.util.List;

/**
 * Сервис управления профилями в соцсетях
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
     * Получить профили по идентификатору контакта
     *
     * @param contactId Идентификатор контакта
     * @return Список профилей
     */
    List<SocialProfile> findByContactId(Long contactId);
}