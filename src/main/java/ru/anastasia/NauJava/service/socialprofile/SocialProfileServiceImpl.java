package ru.anastasia.NauJava.service.socialprofile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.entity.enums.SocialPlatform;
import ru.anastasia.NauJava.entity.socialprofile.SocialProfile;
import ru.anastasia.NauJava.exception.socialprofile.IllegalSocialProfileStateException;
import ru.anastasia.NauJava.exception.socialprofile.SocialProfileNotFoundException;
import ru.anastasia.NauJava.repository.socialprofile.SocialProfileRepository;
import ru.anastasia.NauJava.service.contact.ContactService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SocialProfileServiceImpl implements SocialProfileService {
    /**
     * Репозиторий профилей в соцсетях
     */
    private final SocialProfileRepository socialProfileRepository;

    /**
     * Сервис контактов
     */
    private final ContactService contactService;

    @Override
    public SocialProfile create(SocialProfile socialProfile) {
        log.info("Создание профиля в соцсети. Платформа: {}, пользователь: {}",
                socialProfile.getPlatform(),
                maskUsername(socialProfile.getUsername()));

        validateSocialProfile(socialProfile);
        SocialProfile savedProfile = socialProfileRepository.save(socialProfile);

        log.info("Профиль в соцсети успешно создан. ID: {}, платформа: {}, контакт ID: {}",
                savedProfile.getId(), savedProfile.getPlatform(),
                savedProfile.getContact().getId());

        return savedProfile;
    }

    @Override
    public SocialProfile createForContact(Long contactId, SocialProfile profile) {
        log.info("Создание профиля в соцсети для контакта ID: {}. Платформа: {}, пользователь: {}",
                contactId, profile.getPlatform(), maskUsername(profile.getUsername()));

        Contact contact = contactService.findById(contactId);
        log.debug("Контакт найден: ID: {}, имя: {}", contactId, contact.getFullName());

        validateSocialProfile(profile);
        profile.setContact(contact);

        SocialProfile savedProfile = socialProfileRepository.save(profile);

        log.info("Профиль в соцсети успешно создан для контакта. ID профиля: {}, контакт: {}, платформа: {}",
                savedProfile.getId(), contact.getFullName(), savedProfile.getPlatform());

        return savedProfile;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SocialProfile> findByContactId(Long contactId) {
        log.debug("Поиск профилей в сецсетях для контакта ID: {}", contactId);

        List<SocialProfile> profiles = socialProfileRepository.findByContactId(contactId);

        log.debug("Найдено {} профилей в сецсетях для контакта ID: {}", profiles.size(), contactId);

        return profiles;
    }

    @Override
    public SocialProfile findById(Long id) {
        log.debug("Поиск профиля в соцсети по ID: {}", id);

        SocialProfile profile = socialProfileRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Профиль в соцсети не найден с ID: {}", id);
                    return new SocialProfileNotFoundException("Профиль в соцсети не найден с id: " + id);
                });

        log.debug("Профиль в соцсети найден: ID: {}, платформа: {}, пользователь: {}, контакт ID: {}",
                profile.getId(), profile.getPlatform(),
                maskUsername(profile.getUsername()), profile.getContact().getId());

        return profile;
    }

    @Override
    public SocialProfile update(SocialProfile profile) {
        log.info("Обновление профиля в соцсети с ID: {}", profile.getId());

        validateSocialProfile(profile);

        SocialProfile existing = findById(profile.getId());
        log.debug("Текущие данные профиля ID: {}: платформа: {}, пользователь: {}",
                existing.getId(), existing.getPlatform(),
                maskUsername(existing.getUsername()));

        existing.setPlatform(profile.getPlatform());
        existing.setCustomPlatformName(profile.getCustomPlatformName());
        existing.setUsername(profile.getUsername());
        existing.setProfileUrl(profile.getProfileUrl());

        SocialProfile updatedProfile = socialProfileRepository.save(existing);

        log.info("Профиль в соцсети успешно обновлен. ID: {}, новая платформа: {}, новый пользователь: {}",
                updatedProfile.getId(), updatedProfile.getPlatform(),
                maskUsername(updatedProfile.getUsername()));

        return updatedProfile;
    }

    @Override
    public void delete(Long id) {
        log.info("Удаление профиля в соцсети с ID: {}", id);

        // Сначала находим профиль чтобы залогировать информацию о нем
        SocialProfile profile = socialProfileRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Попытка удаления несуществующего профиля в соцсети с ID: {}", id);
                    return new SocialProfileNotFoundException("Профиль в соцсети не найден с id: " + id);
                });

        log.debug("Профиль в соцсети для удаления: ID: {}, платформа: {}, пользователь: {}, контакт ID: {}",
                profile.getId(), profile.getPlatform(),
                maskUsername(profile.getUsername()), profile.getContact().getId());

        socialProfileRepository.deleteById(id);

        log.info("Профиль в соцсети успешно удален. ID: {}, платформа: {}, контакт ID: {}",
                id, profile.getPlatform(), profile.getContact().getId());
    }

    private void validateSocialProfile(SocialProfile profile) {
        log.trace("Валидация профиля в соцсети. Платформа: {}, кастомное название: {}",
                profile.getPlatform(), profile.getCustomPlatformName());

        if (profile.getPlatform() == SocialPlatform.CUSTOM &&
                (profile.getCustomPlatformName() == null || profile.getCustomPlatformName().trim().isEmpty())) {
            log.warn("Ошибка валидации: для кастомной платформы не указано название. Профиль: {}",
                    profileToString(profile));
            throw new IllegalSocialProfileStateException("Для кастомной платформы должно быть указано название");
        }

        if (profile.getPlatform() != SocialPlatform.CUSTOM &&
                profile.getCustomPlatformName() != null && !profile.getCustomPlatformName().trim().isEmpty()) {
            log.warn("Ошибка валидации: название кастомной платформы указано для стандартной платформы. " +
                    "Профиль: {}, платформа: {}", profileToString(profile), profile.getPlatform());
            throw new IllegalSocialProfileStateException("Название кастомной платформы должно быть пустым для стандартных платформ");
        }

        log.trace("Валидация профиля в соцсети прошла успешно");
    }

    /**
     * Маскирует username для безопасности
     */
    private String maskUsername(String username) {
        if (username == null || username.length() <= 3) {
            return "***";
        }
        // Оставляем первые 3 символа, остальные маскируем
        return username.substring(0, 3) + "***";
    }

    /**
     * Создает строковое представление профиля для логов (без чувствительных данных)
     */
    private String profileToString(SocialProfile profile) {
        return String.format("SocialProfile{id=%s, platform=%s, customPlatformName=%s}",
                profile.getId(), profile.getPlatform(), profile.getCustomPlatformName());
    }
}