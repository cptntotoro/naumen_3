package ru.anastasia.NauJava.repository.contact;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.entity.contact.SocialProfile;
import ru.anastasia.NauJava.entity.enums.SocialPlatform;

import java.util.List;
import java.util.UUID;

@SpringBootTest
@Transactional
class SocialProfileRepositoryTest {

    @Autowired
    private SocialProfileRepository socialProfileRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Test
    void testFindByPlatformAndUsernameContainingIgnoreCase() {
        Contact contact = Contact.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .build();

        Contact savedContact = contactRepository.save(contact);

        String username = "testuser" + UUID.randomUUID();

        SocialProfile profile = SocialProfile.builder()
                .contact(savedContact)
                .platform(SocialPlatform.TELEGRAM)
                .username(username)
                .build();

        socialProfileRepository.save(profile);

        List<SocialProfile> foundProfiles = socialProfileRepository
                .findByPlatformAndUsernameContainingIgnoreCase(SocialPlatform.TELEGRAM, "testuser");

        Assertions.assertFalse(foundProfiles.isEmpty());
        Assertions.assertEquals(SocialPlatform.TELEGRAM, foundProfiles.getFirst().getPlatform());
    }

    @Test
    void testFindByFavoriteContacts() {
        Contact favoriteContact = Contact.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .isFavorite(true)
                .build();

        Contact savedFavorite = contactRepository.save(favoriteContact);

        SocialProfile profile = SocialProfile.builder()
                .contact(savedFavorite)
                .platform(SocialPlatform.VK)
                .username("favorite_user")
                .build();

        socialProfileRepository.save(profile);

        List<SocialProfile> foundProfiles = socialProfileRepository.findByFavoriteContacts();

        Assertions.assertFalse(foundProfiles.isEmpty());
        Assertions.assertTrue(foundProfiles.getFirst().getContact().getIsFavorite());
    }
}