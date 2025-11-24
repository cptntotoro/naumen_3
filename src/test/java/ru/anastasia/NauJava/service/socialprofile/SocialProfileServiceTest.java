package ru.anastasia.NauJava.service.socialprofile;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.entity.enums.SocialPlatform;
import ru.anastasia.NauJava.entity.socialprofile.SocialProfile;
import ru.anastasia.NauJava.exception.socialprofile.IllegalSocialProfileStateException;
import ru.anastasia.NauJava.exception.socialprofile.SocialProfileNotFoundException;
import ru.anastasia.NauJava.repository.socialprofile.SocialProfileRepository;
import ru.anastasia.NauJava.service.contact.ContactService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SocialProfileServiceTest {

    @Mock
    private SocialProfileRepository socialProfileRepository;

    @Mock
    private ContactService contactService;

    @InjectMocks
    private SocialProfileServiceImpl socialProfileService;

    private Contact createTestContact() {
        Contact contact = new Contact();
        contact.setId(1L);
        contact.setFirstName("Иван");
        contact.setLastName("Петров");
        return contact;
    }

    private SocialProfile createTestSocialProfile() {
        Contact contact = createTestContact();

        SocialProfile profile = new SocialProfile();
        profile.setId(1L);
        profile.setContact(contact);
        profile.setPlatform(SocialPlatform.VK);
        profile.setUsername("иван_петров");
        profile.setProfileUrl("https://vk.com/ivan_petrov");
        return profile;
    }

    @Test
    void createSuccess() {
        SocialProfile profile = createTestSocialProfile();
        profile.setId(null);

        when(socialProfileRepository.save(any(SocialProfile.class))).thenReturn(profile);

        SocialProfile result = socialProfileService.create(profile);

        assertNotNull(result);
        assertEquals(SocialPlatform.VK, result.getPlatform());
        assertEquals("иван_петров", result.getUsername());
        verify(socialProfileRepository, times(1)).save(profile);
    }

    @Test
    void createWithCustomPlatformSuccess() {
        Contact contact = createTestContact();

        SocialProfile profile = new SocialProfile();
        profile.setContact(contact);
        profile.setPlatform(SocialPlatform.CUSTOM);
        profile.setCustomPlatformName("МойСайт");
        profile.setUsername("анна_сидорова");
        profile.setProfileUrl("https://mysite.com/anna_sidorova");

        when(socialProfileRepository.save(any(SocialProfile.class))).thenReturn(profile);

        SocialProfile result = socialProfileService.create(profile);

        assertNotNull(result);
        assertEquals(SocialPlatform.CUSTOM, result.getPlatform());
        assertEquals("МойСайт", result.getCustomPlatformName());
        verify(socialProfileRepository, times(1)).save(profile);
    }

    @Test
    void createWithCustomPlatformWithoutNameThrowsException() {
        Contact contact = createTestContact();

        SocialProfile profile = new SocialProfile();
        profile.setContact(contact);
        profile.setPlatform(SocialPlatform.CUSTOM);
        profile.setUsername("петр_иванов");
        profile.setProfileUrl("https://custom.com/petr_ivanov");

        assertThrows(IllegalSocialProfileStateException.class, () -> socialProfileService.create(profile));
        verify(socialProfileRepository, never()).save(any(SocialProfile.class));
    }

    @Test
    void createWithStandardPlatformWithCustomNameThrowsException() {
        Contact contact = createTestContact();

        SocialProfile profile = new SocialProfile();
        profile.setContact(contact);
        profile.setPlatform(SocialPlatform.TELEGRAM);
        profile.setCustomPlatformName("Телеграмм");
        profile.setUsername("мария");
        profile.setProfileUrl("https://t.me/maria");

        assertThrows(IllegalSocialProfileStateException.class, () -> socialProfileService.create(profile));
        verify(socialProfileRepository, never()).save(any(SocialProfile.class));
    }

    @Test
    void createForContactSuccess() {
        Long contactId = 1L;
        Contact contact = createTestContact();

        SocialProfile profile = new SocialProfile();
        profile.setPlatform(SocialPlatform.TELEGRAM);
        profile.setUsername("сергей_кузнецов");
        profile.setProfileUrl("https://t.me/sergey_kuznetsov");

        when(contactService.findById(contactId)).thenReturn(contact);
        when(socialProfileRepository.save(any(SocialProfile.class))).thenReturn(profile);

        SocialProfile result = socialProfileService.createForContact(contactId, profile);

        assertNotNull(result);
        assertEquals(contact, profile.getContact());
        assertEquals(SocialPlatform.TELEGRAM, result.getPlatform());
        verify(contactService, times(1)).findById(contactId);
        verify(socialProfileRepository, times(1)).save(profile);
    }

    @Test
    void findByContactIdSuccess() {
        Long contactId = 1L;
        Contact contact = createTestContact();

        SocialProfile profile1 = new SocialProfile();
        profile1.setContact(contact);
        profile1.setPlatform(SocialPlatform.VK);
        profile1.setUsername("ольга");

        SocialProfile profile2 = new SocialProfile();
        profile2.setContact(contact);
        profile2.setPlatform(SocialPlatform.TELEGRAM);
        profile2.setUsername("николай");

        List<SocialProfile> profiles = List.of(profile1, profile2);

        when(socialProfileRepository.findByContactId(contactId)).thenReturn(profiles);

        List<SocialProfile> result = socialProfileService.findByContactId(contactId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(socialProfileRepository, times(1)).findByContactId(contactId);
    }

    @Test
    void findByIdSuccess() {
        Long id = 1L;
        SocialProfile profile = createTestSocialProfile();

        when(socialProfileRepository.findById(id)).thenReturn(Optional.of(profile));

        SocialProfile result = socialProfileService.findById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("иван_петров", result.getUsername());
        verify(socialProfileRepository, times(1)).findById(id);
    }

    @Test
    void findByIdNotFoundThrowsException() {
        Long id = 999L;

        when(socialProfileRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(SocialProfileNotFoundException.class, () -> socialProfileService.findById(id));
        verify(socialProfileRepository, times(1)).findById(id);
    }

    @Test
    void updateSuccess() {
        Long id = 1L;
        Contact contact = createTestContact();

        SocialProfile existingProfile = new SocialProfile();
        existingProfile.setId(id);
        existingProfile.setContact(contact);
        existingProfile.setPlatform(SocialPlatform.VK);
        existingProfile.setUsername("старое_имя");

        SocialProfile updatedProfile = new SocialProfile();
        updatedProfile.setId(id);
        updatedProfile.setPlatform(SocialPlatform.TELEGRAM);
        updatedProfile.setUsername("новое_имя");
        updatedProfile.setProfileUrl("https://t.me/novoe_imya");

        when(socialProfileRepository.findById(id)).thenReturn(Optional.of(existingProfile));
        when(socialProfileRepository.save(any(SocialProfile.class))).thenReturn(updatedProfile);

        SocialProfile result = socialProfileService.update(updatedProfile);

        assertNotNull(result);
        assertEquals(SocialPlatform.TELEGRAM, result.getPlatform());
        assertEquals("новое_имя", result.getUsername());
        verify(socialProfileRepository, times(1)).findById(id);
        verify(socialProfileRepository, times(1)).save(existingProfile);
    }

    @Test
    void updateWithInvalidDataThrowsException() {
        Contact contact = createTestContact();

        SocialProfile profile = new SocialProfile();
        profile.setId(1L);
        profile.setContact(contact);
        profile.setPlatform(SocialPlatform.CUSTOM);
        profile.setUsername("дима");

        assertThrows(IllegalSocialProfileStateException.class, () -> socialProfileService.update(profile));
        verify(socialProfileRepository, never()).save(any(SocialProfile.class));
    }

    @Test
    void deleteSuccess() {
        Long id = 1L;
        SocialProfile profile = createTestSocialProfile();

        when(socialProfileRepository.findById(id)).thenReturn(Optional.of(profile));

        socialProfileService.delete(id);

        verify(socialProfileRepository, times(1)).findById(id);
        verify(socialProfileRepository, times(1)).deleteById(id);
    }
}