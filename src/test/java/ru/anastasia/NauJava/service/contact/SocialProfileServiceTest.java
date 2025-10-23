package ru.anastasia.NauJava.service.contact;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.entity.contact.SocialProfile;
import ru.anastasia.NauJava.entity.enums.SocialPlatform;
import ru.anastasia.NauJava.repository.contact.ContactRepository;
import ru.anastasia.NauJava.repository.contact.SocialProfileRepository;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class SocialProfileServiceTest {

    @Autowired
    private SocialProfileService socialProfileService;

    @Autowired
    private SocialProfileRepository socialProfileRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Test
    void testCreate_Success() {
        Contact contact = Contact.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .build();

        contactRepository.save(contact);

        SocialProfile socialProfile = SocialProfile.builder()
                .contact(contact)
                .platform(SocialPlatform.TELEGRAM)
                .username("@ivanov" + UUID.randomUUID())
                .build();

        SocialProfile savedProfile = socialProfileService.create(socialProfile);

        assertNotNull(savedProfile.getId());
        assertEquals(SocialPlatform.TELEGRAM, savedProfile.getPlatform());
        assertEquals(socialProfile.getUsername(), savedProfile.getUsername());
        assertEquals(contact.getId(), savedProfile.getContact().getId());
        assertTrue(socialProfileRepository.findById(savedProfile.getId()).isPresent());
    }

    @Test
    void testFindByContactId_Success() {
        Contact contact = Contact.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .build();

        contactRepository.save(contact);

        SocialProfile socialProfile = SocialProfile.builder()
                .contact(contact)
                .platform(SocialPlatform.TELEGRAM)
                .username("@ivanov" + UUID.randomUUID())
                .build();

        socialProfileService.create(socialProfile);

        List<SocialProfile> profiles = socialProfileService.findByContactId(contact.getId());

        assertFalse(profiles.isEmpty());
        assertEquals(socialProfile.getUsername(), profiles.getFirst().getUsername());
        assertEquals(contact.getId(), profiles.getFirst().getContact().getId());
    }

    @Test
    void testFindByContactId_NoProfiles() {
        Contact contact = Contact.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .build();

        contactRepository.save(contact);

        List<SocialProfile> profiles = socialProfileService.findByContactId(contact.getId());

        assertTrue(profiles.isEmpty());
    }
}
