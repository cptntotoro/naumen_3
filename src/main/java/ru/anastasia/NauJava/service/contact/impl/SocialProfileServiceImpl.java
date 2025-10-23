package ru.anastasia.NauJava.service.contact.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.anastasia.NauJava.entity.contact.SocialProfile;
import ru.anastasia.NauJava.repository.contact.SocialProfileRepository;
import ru.anastasia.NauJava.service.contact.SocialProfileService;

import java.util.List;

@Service
public class SocialProfileServiceImpl implements SocialProfileService {
    /**
     * Репозиторий профилей в соцсетях
     */
    private final SocialProfileRepository socialProfileRepository;

    @Autowired
    public SocialProfileServiceImpl(SocialProfileRepository socialProfileRepository) {
        this.socialProfileRepository = socialProfileRepository;
    }

    @Override
    public SocialProfile create(SocialProfile socialProfile) {
        return socialProfileRepository.save(socialProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SocialProfile> findByContactId(Long contactId) {
        return socialProfileRepository.findByContactId(contactId);
    }
}