package ru.anastasia.NauJava.service.socialprofile;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.anastasia.NauJava.entity.socialprofile.SocialProfile;
import ru.anastasia.NauJava.repository.socialprofile.SocialProfileRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SocialProfileServiceImpl implements SocialProfileService {
    /**
     * Репозиторий профилей в соцсетях
     */
    private final SocialProfileRepository socialProfileRepository;

    @Override
    public SocialProfile create(SocialProfile socialProfile) {
        return socialProfileRepository.save(socialProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SocialProfile> findByContactId(Long contactId) {
        return socialProfileRepository.findByContactId(contactId);
    }

    @Override
    public SocialProfile findById(Long id) {
        return socialProfileRepository.findById(id).orElse(null);
    }

    @Override
    public SocialProfile update(SocialProfile profile) {
        findById(profile.getId());

        return socialProfileRepository.save(profile);
    }

    @Override
    public void delete(Long id) {
        socialProfileRepository.deleteById(id);
    }
}