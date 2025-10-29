package ru.anastasia.NauJava.service.socialprofile;

import lombok.RequiredArgsConstructor;
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
        validateSocialProfile(socialProfile);
        return socialProfileRepository.save(socialProfile);
    }

    @Override
    public SocialProfile createForContact(Long contactId, SocialProfile profile) {
        Contact contact = contactService.findById(contactId);

        validateSocialProfile(profile);
        profile.setContact(contact);

        return socialProfileRepository.save(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SocialProfile> findByContactId(Long contactId) {
        return socialProfileRepository.findByContactId(contactId);
    }

    @Override
    public SocialProfile findById(Long id) {
        return socialProfileRepository.findById(id)
                .orElseThrow(() -> new SocialProfileNotFoundException("Профиль в соцсети не найден с id: " + id));
    }

    @Override
    public SocialProfile update(SocialProfile profile) {
        validateSocialProfile(profile);
        SocialProfile existing = findById(profile.getId());

        existing.setPlatform(profile.getPlatform());
        existing.setCustomPlatformName(profile.getCustomPlatformName());
        existing.setUsername(profile.getUsername());
        existing.setProfileUrl(profile.getProfileUrl());

        return socialProfileRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        socialProfileRepository.deleteById(id);
    }

    private void validateSocialProfile(SocialProfile profile) {
        if (profile.getPlatform() == SocialPlatform.CUSTOM &&
                (profile.getCustomPlatformName() == null || profile.getCustomPlatformName().trim().isEmpty())) {
            throw new IllegalSocialProfileStateException("Для кастомной платформы должно быть указано название");
        }

        if (profile.getPlatform() != SocialPlatform.CUSTOM &&
                profile.getCustomPlatformName() != null && !profile.getCustomPlatformName().trim().isEmpty()) {
            throw new IllegalSocialProfileStateException("Название кастомной платформы должно быть пустым для стандартных платформ");
        }
    }
}