package ru.anastasia.NauJava.service.contact;

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
import ru.anastasia.NauJava.service.socialprofile.SocialProfileServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
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

    @Test
    void create_ShouldReturnSavedProfile() {
        SocialProfile profile = SocialProfile.builder()
                .platform(SocialPlatform.VK)
                .username("johndoe")
                .profileUrl("https://facebook.com/johndoe")
                .build();
        SocialProfile savedProfile = SocialProfile.builder()
                .id(1L)
                .platform(SocialPlatform.VK)
                .username("johndoe")
                .profileUrl("https://facebook.com/johndoe")
                .build();

        when(socialProfileRepository.save(profile)).thenReturn(savedProfile);

        SocialProfile result = socialProfileService.create(profile);

        assertNotNull(result.getId());
        assertEquals(savedProfile, result);
        verify(socialProfileRepository).save(profile);
    }

    @Test
    void create_ShouldThrowIllegalSocialProfileStateException_WhenCustomPlatformWithoutName() {
        SocialProfile profile = SocialProfile.builder()
                .platform(SocialPlatform.CUSTOM)
                .username("user")
                .build();

        IllegalSocialProfileStateException exception = assertThrows(
                IllegalSocialProfileStateException.class,
                () -> socialProfileService.create(profile)
        );

        assertTrue(exception.getMessage().contains("Для кастомной платформы должно быть указано название"));
        verify(socialProfileRepository, never()).save(any(SocialProfile.class));
    }

    @Test
    void create_ShouldThrowIllegalSocialProfileStateException_WhenNonCustomPlatformWithName() {
        SocialProfile profile = SocialProfile.builder()
                .platform(SocialPlatform.VK)
                .customPlatformName("Custom Platform")
                .username("user")
                .build();

        IllegalSocialProfileStateException exception = assertThrows(
                IllegalSocialProfileStateException.class,
                () -> socialProfileService.create(profile)
        );

        assertTrue(exception.getMessage().contains("Название кастомной платформы должно быть пустым для стандартных платформ"));
        verify(socialProfileRepository, never()).save(any(SocialProfile.class));
    }

    @Test
    void createForContact_ShouldReturnSavedProfile() {
        Long contactId = 1L;
        Contact contact = Contact.builder().id(contactId).build();
        SocialProfile profile = SocialProfile.builder()
                .platform(SocialPlatform.VK)
                .username("johndoe")
                .build();
        SocialProfile savedProfile = SocialProfile.builder()
                .id(1L)
                .platform(SocialPlatform.VK)
                .username("johndoe")
                .contact(contact)
                .build();

        when(contactService.findById(contactId)).thenReturn(contact);
        when(socialProfileRepository.save(profile)).thenReturn(savedProfile);

        SocialProfile result = socialProfileService.createForContact(contactId, profile);

        assertNotNull(result.getId());
        assertEquals(contact, result.getContact());
        verify(contactService).findById(contactId);
        verify(socialProfileRepository).save(profile);
    }

    @Test
    void createForContact_ShouldThrowIllegalSocialProfileStateException_WhenCustomPlatformWithoutName() {
        Long contactId = 1L;
        Contact contact = Contact.builder().id(contactId).build();
        SocialProfile profile = SocialProfile.builder()
                .platform(SocialPlatform.CUSTOM)
                .username("user")
                .build();

        when(contactService.findById(contactId)).thenReturn(contact);

        IllegalSocialProfileStateException exception = assertThrows(
                IllegalSocialProfileStateException.class,
                () -> socialProfileService.createForContact(contactId, profile)
        );

        assertTrue(exception.getMessage().contains("Для кастомной платформы должно быть указано название"));
        verify(socialProfileRepository, never()).save(any(SocialProfile.class));
    }

    @Test
    void findByContactId_ShouldReturnProfiles() {
        Long contactId = 1L;
        SocialProfile profile1 = SocialProfile.builder().id(1L).platform(SocialPlatform.VK).build();
        SocialProfile profile2 = SocialProfile.builder().id(2L).platform(SocialPlatform.TELEGRAM).build();
        List<SocialProfile> expectedProfiles = List.of(profile1, profile2);

        when(socialProfileRepository.findByContactId(contactId)).thenReturn(expectedProfiles);

        List<SocialProfile> result = socialProfileService.findByContactId(contactId);

        assertEquals(expectedProfiles, result);
        verify(socialProfileRepository).findByContactId(contactId);
    }

    @Test
    void findById_ShouldReturnProfile_WhenExists() {
        Long id = 1L;
        SocialProfile profile = SocialProfile.builder().id(id).platform(SocialPlatform.VK).build();

        when(socialProfileRepository.findById(id)).thenReturn(Optional.of(profile));

        SocialProfile result = socialProfileService.findById(id);

        assertEquals(profile, result);
        verify(socialProfileRepository).findById(id);
    }

    @Test
    void findById_ShouldThrowSocialProfileNotFoundException_WhenNotExists() {
        Long id = 999L;

        when(socialProfileRepository.findById(id)).thenReturn(Optional.empty());

        SocialProfileNotFoundException exception = assertThrows(
                SocialProfileNotFoundException.class,
                () -> socialProfileService.findById(id)
        );

        assertTrue(exception.getMessage().contains("Профиль в соцсети не найден с id: " + id));
        verify(socialProfileRepository).findById(id);
    }

    @Test
    void update_ShouldReturnUpdatedProfile_WhenExists() {
        Long id = 1L;
        SocialPlatform newPlatform = SocialPlatform.VK;
        String newUsername = "janedoe";
        String newProfileUrl = "https://instagram.com/janedoe";

        SocialProfile existingProfile = SocialProfile.builder()
                .id(id)
                .platform(SocialPlatform.TELEGRAM)
                .username("johndoe")
                .profileUrl("https://facebook.com/johndoe")
                .build();
        SocialProfile updateProfile = SocialProfile.builder()
                .id(id)
                .platform(newPlatform)
                .username(newUsername)
                .profileUrl(newProfileUrl)
                .build();
        SocialProfile updatedProfile = SocialProfile.builder()
                .id(id)
                .platform(newPlatform)
                .username(newUsername)
                .profileUrl(newProfileUrl)
                .build();

        when(socialProfileRepository.findById(id)).thenReturn(Optional.of(existingProfile));
        when(socialProfileRepository.save(existingProfile)).thenReturn(updatedProfile);

        SocialProfile result = socialProfileService.update(updateProfile);

        assertEquals(newPlatform, result.getPlatform());
        assertEquals(newUsername, result.getUsername());
        assertEquals(newProfileUrl, result.getProfileUrl());
        verify(socialProfileRepository).findById(id);
        verify(socialProfileRepository).save(existingProfile);
    }

    @Test
    void update_ShouldThrowSocialProfileNotFoundException_WhenNotExists() {
        SocialProfile profile = SocialProfile.builder().id(999L).build();

        when(socialProfileRepository.findById(999L)).thenReturn(Optional.empty());

        SocialProfileNotFoundException exception = assertThrows(
                SocialProfileNotFoundException.class,
                () -> socialProfileService.update(profile)
        );

        assertTrue(exception.getMessage().contains("Профиль в соцсети не найден с id: " + profile.getId()));
        verify(socialProfileRepository).findById(999L);
        verify(socialProfileRepository, never()).save(any(SocialProfile.class));
    }

    @Test
    void update_ShouldUpdateCustomPlatformName_WhenCustomPlatform() {
        Long id = 1L;
        SocialProfile existingProfile = SocialProfile.builder()
                .id(id)
                .platform(SocialPlatform.TELEGRAM)
                .customPlatformName(null)
                .build();
        SocialProfile updateProfile = SocialProfile.builder()
                .id(id)
                .platform(SocialPlatform.CUSTOM)
                .customPlatformName("My Custom Platform")
                .username("user")
                .build();
        SocialProfile updatedProfile = SocialProfile.builder()
                .id(id)
                .platform(SocialPlatform.CUSTOM)
                .customPlatformName("My Custom Platform")
                .username("user")
                .build();

        when(socialProfileRepository.findById(id)).thenReturn(Optional.of(existingProfile));
        when(socialProfileRepository.save(existingProfile)).thenReturn(updatedProfile);

        SocialProfile result = socialProfileService.update(updateProfile);

        assertEquals(SocialPlatform.CUSTOM, result.getPlatform());
        assertEquals("My Custom Platform", result.getCustomPlatformName());
        verify(socialProfileRepository).findById(id);
        verify(socialProfileRepository).save(existingProfile);
    }

    @Test
    void delete_ShouldCallRepositoryDelete() {
        Long id = 1L;
        doNothing().when(socialProfileRepository).deleteById(id);

        socialProfileService.delete(id);

        verify(socialProfileRepository).deleteById(id);
    }
}