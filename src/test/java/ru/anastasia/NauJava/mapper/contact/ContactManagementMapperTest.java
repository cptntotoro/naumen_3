package ru.anastasia.NauJava.mapper.contact;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.anastasia.NauJava.dto.company.ContactCompanyUpdateDto;
import ru.anastasia.NauJava.dto.contact.ContactDetailUpdateDto;
import ru.anastasia.NauJava.dto.contact.ContactUpdateDto;
import ru.anastasia.NauJava.dto.event.EventUpdateDto;
import ru.anastasia.NauJava.dto.note.NoteUpdateDto;
import ru.anastasia.NauJava.dto.socialprofile.SocialProfileUpdateDto;
import ru.anastasia.NauJava.entity.company.Company;
import ru.anastasia.NauJava.entity.company.ContactCompany;
import ru.anastasia.NauJava.entity.company.JobTitle;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.entity.contact.ContactDetail;
import ru.anastasia.NauJava.entity.enums.DetailLabel;
import ru.anastasia.NauJava.entity.enums.DetailType;
import ru.anastasia.NauJava.entity.enums.EventType;
import ru.anastasia.NauJava.entity.enums.SocialPlatform;
import ru.anastasia.NauJava.entity.event.Event;
import ru.anastasia.NauJava.entity.note.Note;
import ru.anastasia.NauJava.entity.socialprofile.SocialProfile;
import ru.anastasia.NauJava.entity.tag.Tag;
import ru.anastasia.NauJava.service.facade.dto.ContactFullDetails;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class ContactManagementMapperTest {

    private final ContactManagementMapper mapper = new ContactManagementMapperImpl();

    @Test
    void contactFullDetailsToContactUpdateDto_shouldMapAllFieldsCorrectly() {
        ContactFullDetails contactFullDetails = createContactFullDetails();

        ContactUpdateDto result = mapper.contactFullDetailsToContactUpdateDto(contactFullDetails);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Иван", result.getFirstName());
        assertEquals("Иванов", result.getLastName());
        assertEquals("Иван Иванов", result.getDisplayName());
        assertEquals("https://avatar.url", result.getAvatarUrl());
        assertTrue(result.getIsFavorite());

        assertEquals(2, result.getContactDetails().size());
        ContactDetailUpdateDto firstDetail = result.getContactDetails().getFirst();
        assertEquals(1L, firstDetail.getId());
        assertEquals(DetailType.EMAIL, firstDetail.getDetailType());
        assertEquals(DetailLabel.WORK, firstDetail.getLabel());
        assertEquals("ivan@example.com", firstDetail.getValue());
        assertTrue(firstDetail.getIsPrimary());

        assertEquals(1, result.getSocialProfiles().size());
        SocialProfileUpdateDto socialProfile = result.getSocialProfiles().getFirst();
        assertEquals(1L, socialProfile.getId());
        assertEquals(SocialPlatform.TELEGRAM, socialProfile.getPlatform());
        assertEquals("@ivanov", socialProfile.getUsername());
        assertEquals("https://t.me/ivanov", socialProfile.getProfileUrl());

        assertEquals(1, result.getCompanies().size());
        ContactCompanyUpdateDto company = result.getCompanies().getFirst();
        assertEquals(1L, company.getId());
        assertEquals(100L, company.getCompanyId());
        assertEquals(10L, company.getJobTitleId());
        assertTrue(company.getIsCurrent());

        assertEquals(1, result.getEvents().size());
        EventUpdateDto event = result.getEvents().getFirst();
        assertEquals(1L, event.getId());
        assertEquals(EventType.BIRTHDAY, event.getEventType());
        assertEquals(LocalDate.of(1990, 5, 15), event.getEventDate());
        assertEquals("Заметка о дне рождения", event.getNotes());
        assertTrue(event.getYearlyRecurrence());

        assertEquals(1, result.getNotes().size());
        NoteUpdateDto note = result.getNotes().getFirst();
        assertEquals(1L, note.getId());
        assertEquals("Тестовая заметка", note.getContent());

        assertEquals(2, result.getTagIds().size());
        assertTrue(result.getTagIds().contains(1L));
        assertTrue(result.getTagIds().contains(2L));
    }

    @Test
    void contactFullDetailsToContactUpdateDto_shouldHandleNullFields() {
        ContactFullDetails contactFullDetails = ContactFullDetails.builder()
                .contact(Contact.builder()
                        .id(1L)
                        .firstName("Иван")
                        .lastName("Иванов")
                        .displayName(null)
                        .avatarUrl(null)
                        .isFavorite(null)
                        .companies(null)
                        .build())
                .contactDetails(null)
                .socialProfiles(null)
                .events(null)
                .notes(null)
                .tags(null)
                .build();

        ContactUpdateDto result = mapper.contactFullDetailsToContactUpdateDto(contactFullDetails);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Иван", result.getFirstName());
        assertEquals("Иванов", result.getLastName());
        assertNull(result.getDisplayName());
        assertNull(result.getAvatarUrl());
        assertNull(result.getIsFavorite());
        assertNotNull(result.getContactDetails());
        assertTrue(result.getContactDetails().isEmpty());
        assertNotNull(result.getSocialProfiles());
        assertTrue(result.getSocialProfiles().isEmpty());
        assertNotNull(result.getEvents());
        assertTrue(result.getEvents().isEmpty());
        assertNotNull(result.getNotes());
        assertTrue(result.getNotes().isEmpty());
        assertNotNull(result.getTagIds());
        assertTrue(result.getTagIds().isEmpty());
        assertNotNull(result.getCompanies());
        assertTrue(result.getCompanies().isEmpty());
    }

    @Test
    void contactFullDetailsToContactUpdateDto_shouldHandleEmptyLists() {
        ContactFullDetails contactFullDetails = ContactFullDetails.builder()
                .contact(Contact.builder()
                        .id(1L)
                        .firstName("Иван")
                        .lastName("Иванов")
                        .displayName("Иван Иванов")
                        .avatarUrl("https://avatar.url")
                        .isFavorite(true)
                        .build())
                .contactDetails(List.of())
                .socialProfiles(List.of())
                .events(List.of())
                .notes(List.of())
                .tags(List.of())
                .contactTags(List.of())
                .build();

        ContactUpdateDto result = mapper.contactFullDetailsToContactUpdateDto(contactFullDetails);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Иван", result.getFirstName());
        assertEquals("Иванов", result.getLastName());
        assertTrue(result.getContactDetails().isEmpty());
        assertTrue(result.getSocialProfiles().isEmpty());
        assertTrue(result.getEvents().isEmpty());
        assertTrue(result.getNotes().isEmpty());
        assertTrue(result.getTagIds().isEmpty());
        assertTrue(result.getCompanies().isEmpty());
    }

    @Test
    void contactFullDetailsToContactUpdateDto_shouldMapContactDetailsWithNullIsPrimary() {
        ContactDetail contactDetail = ContactDetail.builder()
                .id(1L)
                .detailType(DetailType.EMAIL)
                .label(DetailLabel.WORK)
                .value("test@example.com")
                .isPrimary(null)
                .build();

        ContactFullDetails contactFullDetails = ContactFullDetails.builder()
                .contact(Contact.builder()
                        .id(1L)
                        .firstName("Иван")
                        .lastName("Иванов")
                        .build())
                .contactDetails(List.of(contactDetail))
                .socialProfiles(List.of())
                .events(List.of())
                .notes(List.of())
                .tags(List.of())
                .build();

        ContactUpdateDto result = mapper.contactFullDetailsToContactUpdateDto(contactFullDetails);

        assertNotNull(result);
        assertEquals(1, result.getContactDetails().size());
        ContactDetailUpdateDto detailDto = result.getContactDetails().getFirst();
        assertNull(detailDto.getIsPrimary());
    }

    @Test
    void contactFullDetailsToContactUpdateDto_shouldMapCompanyWithNullJobTitle() {
        ru.anastasia.NauJava.entity.company.ContactCompany contactCompany =
                ru.anastasia.NauJava.entity.company.ContactCompany.builder()
                        .id(1L)
                        .company(ru.anastasia.NauJava.entity.company.Company.builder()
                                .id(100L)
                                .build())
                        .jobTitle(null)
                        .isCurrent(true)
                        .build();

        ContactFullDetails contactFullDetails = ContactFullDetails.builder()
                .contact(Contact.builder()
                        .id(1L)
                        .firstName("Иван")
                        .lastName("Иванов")
                        .companies(List.of(contactCompany))
                        .build())
                .contactDetails(List.of())
                .socialProfiles(List.of())
                .events(List.of())
                .notes(List.of())
                .tags(List.of())
                .build();

        ContactUpdateDto result = mapper.contactFullDetailsToContactUpdateDto(contactFullDetails);

        assertNotNull(result);
        assertEquals(1, result.getCompanies().size());
        ContactCompanyUpdateDto companyDto = result.getCompanies().getFirst();
        assertEquals(1L, companyDto.getId());
        assertEquals(100L, companyDto.getCompanyId());
        assertNull(companyDto.getJobTitleId());
        assertTrue(companyDto.getIsCurrent());
    }

    private ContactFullDetails createContactFullDetails() {
        Tag tag1 = Tag.builder().id(1L).name("Друзья").build();
        Tag tag2 = Tag.builder().id(2L).name("Работа").build();

        ContactCompany contactCompany = ContactCompany.builder()
                .id(1L)
                .company(Company.builder()
                        .id(100L)
                        .build())
                .jobTitle(JobTitle.builder()
                        .id(10L)
                        .build())
                .isCurrent(true)
                .build();

        Contact contact = Contact.builder()
                .id(1L)
                .firstName("Иван")
                .lastName("Иванов")
                .displayName("Иван Иванов")
                .avatarUrl("https://avatar.url")
                .companies(List.of(contactCompany))
                .isFavorite(true)
                .build();

        ContactDetail contactDetail1 = ContactDetail.builder()
                .id(1L)
                .detailType(DetailType.EMAIL)
                .label(DetailLabel.WORK)
                .value("ivan@example.com")
                .isPrimary(true)
                .build();

        ContactDetail contactDetail2 = ContactDetail.builder()
                .id(2L)
                .detailType(DetailType.PHONE)
                .label(DetailLabel.WORK)
                .value("+79001234567")
                .isPrimary(false)
                .build();

        SocialProfile socialProfile = SocialProfile.builder()
                .id(1L)
                .platform(SocialPlatform.TELEGRAM)
                .username("@ivanov")
                .profileUrl("https://t.me/ivanov")
                .build();

        Event event = Event.builder()
                .id(1L)
                .eventType(EventType.BIRTHDAY)
                .eventDate(LocalDate.of(1990, 5, 15))
                .notes("Заметка о дне рождения")
                .yearlyRecurrence(true)
                .build();

        Note note = Note.builder()
                .id(1L)
                .content("Тестовая заметка")
                .build();

        return ContactFullDetails.builder()
                .contact(contact)
                .contactDetails(Arrays.asList(contactDetail1, contactDetail2))
                .socialProfiles(List.of(socialProfile))
                .events(List.of(event))
                .notes(List.of(note))
                .tags(Arrays.asList(tag1, tag2))
                .build();
    }
}
