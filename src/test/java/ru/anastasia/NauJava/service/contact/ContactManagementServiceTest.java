package ru.anastasia.NauJava.service.contact;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.anastasia.NauJava.dto.company.ContactCompanyCreateDto;
import ru.anastasia.NauJava.dto.company.ContactCompanyUpdateDto;
import ru.anastasia.NauJava.dto.contact.ContactCreateDto;
import ru.anastasia.NauJava.dto.contact.ContactDetailCreateDto;
import ru.anastasia.NauJava.dto.contact.ContactDetailUpdateDto;
import ru.anastasia.NauJava.dto.contact.ContactUpdateDto;
import ru.anastasia.NauJava.dto.event.EventUpdateDto;
import ru.anastasia.NauJava.dto.note.NoteUpdateDto;
import ru.anastasia.NauJava.dto.socialprofile.SocialProfileCreateDto;
import ru.anastasia.NauJava.dto.socialprofile.SocialProfileUpdateDto;
import ru.anastasia.NauJava.entity.company.Company;
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
import ru.anastasia.NauJava.entity.tag.ContactTag;
import ru.anastasia.NauJava.entity.tag.Tag;
import ru.anastasia.NauJava.exception.contact.ContactNotFoundException;
import ru.anastasia.NauJava.service.company.CompanyService;
import ru.anastasia.NauJava.service.company.JobTitleService;
import ru.anastasia.NauJava.service.contact.impl.ContactManagementServiceImpl;
import ru.anastasia.NauJava.service.event.EventService;
import ru.anastasia.NauJava.service.facade.ContactDetailFacadeService;
import ru.anastasia.NauJava.service.facade.ContactEventFacadeService;
import ru.anastasia.NauJava.service.facade.ContactTagFacadeService;
import ru.anastasia.NauJava.service.facade.dto.ContactFullDetails;
import ru.anastasia.NauJava.service.note.NoteService;
import ru.anastasia.NauJava.service.socialprofile.SocialProfileService;
import ru.anastasia.NauJava.service.tag.TagService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ContactManagementServiceTest {
    @Mock
    private ContactDetailService contactDetailService;

    @Mock
    private TagService tagService;

    @Mock
    private EventService eventService;

    @Mock
    private SocialProfileService socialProfileService;

    @Mock
    private NoteService noteService;

    @Mock
    private ContactService contactService;

    @Mock
    private ContactEventFacadeService eventFacade;

    @Mock
    private ContactTagFacadeService tagFacade;

    @Mock
    private ContactDetailFacadeService detailFacade;

    @Mock
    private CompanyService companyService;

    @Mock
    private JobTitleService jobTitleService;

    @InjectMocks
    private ContactManagementServiceImpl contactManagementService;

    // Тестовые данные
    private Contact createTestContact() {
        return Contact.builder()
                .id(1L)
                .firstName("Иван")
                .lastName("Иванов")
                .displayName("Иван Иванов")
                .avatarUrl("https://example.com/avatar.jpg")
                .isFavorite(true)
                .build();
    }

    private ContactCreateDto createTestContactCreateDto() {
        return ContactCreateDto.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .displayName("Иван Иванов")
                .avatarUrl("https://example.com/avatar.jpg")
                .isFavorite(true)
                .contactCompanyCreateDtos(Collections.singletonList(createTestContactCompanyCreateDto()))
                .contactDetailCreateDtos(Collections.singletonList(createTestContactDetailCreateDto()))
                .socialProfileCreateDtos(Collections.singletonList(createTestSocialProfileCreateDto()))
                .tagIds(new HashSet<>(Arrays.asList(1L, 2L)))
                .build();
    }

    private ContactCompanyCreateDto createTestContactCompanyCreateDto() {
        return ContactCompanyCreateDto.builder()
                .companyId(1L)
                .jobTitleId(1L)
                .build();
    }

    private ContactDetailCreateDto createTestContactDetailCreateDto() {
        return ContactDetailCreateDto.builder()
                .detailType(DetailType.EMAIL)
                .label(DetailLabel.MAIN)
                .value("test@example.com")
                .isPrimary(true)
                .build();
    }

    private SocialProfileCreateDto createTestSocialProfileCreateDto() {
        return SocialProfileCreateDto.builder()
                .platform(SocialPlatform.VK)
                .customPlatformName("ВКонтакте")
                .username("ivanov")
                .profileUrl("https://vk.com/ivanov")
                .build();
    }

    private ContactUpdateDto createTestContactUpdateDto() {
        return ContactUpdateDto.builder()
                .id(1L)
                .firstName("ОбновленноеИмя")
                .lastName("ОбновленнаяФамилия")
                .displayName("Обновленное отображаемое имя")
                .avatarUrl("https://new-avatar.jpg")
                .isFavorite(false)
                .companies(Collections.singletonList(createTestContactCompanyUpdateDto()))
                .contactDetails(Collections.singletonList(createTestContactDetailUpdateDto()))
                .socialProfiles(Collections.singletonList(createTestSocialProfileUpdateDto()))
                .tagIds(new HashSet<>(Arrays.asList(3L, 4L)))
                .notes(Collections.singletonList(createTestNoteUpdateDto()))
                .events(Collections.singletonList(createTestEventUpdateDto()))
                .build();
    }

    private ContactCompanyUpdateDto createTestContactCompanyUpdateDto() {
        return ContactCompanyUpdateDto.builder()
                .id(1L)
                .companyId(2L)
                .jobTitleId(2L)
                .build();
    }

    private ContactDetailUpdateDto createTestContactDetailUpdateDto() {
        return ContactDetailUpdateDto.builder()
                .id(1L)
                .detailType(DetailType.PHONE)
                .label(DetailLabel.WORK)
                .value("+79991234567")
                .isPrimary(false)
                .build();
    }

    private SocialProfileUpdateDto createTestSocialProfileUpdateDto() {
        return SocialProfileUpdateDto.builder()
                .id(1L)
                .platform(SocialPlatform.TELEGRAM)
                .customPlatformName("Telegram")
                .username("newusername")
                .profileUrl("https://t.me/newusername")
                .build();
    }

    private NoteUpdateDto createTestNoteUpdateDto() {
        return ru.anastasia.NauJava.dto.note.NoteUpdateDto.builder()
                .id(1L)
                .content("Обновленное содержимое заметки")
                .build();
    }

    private EventUpdateDto createTestEventUpdateDto() {
        return EventUpdateDto.builder()
                .id(1L)
                .eventType(EventType.ANNIVERSARY)
                .customEventName("Годовщина")
                .eventDate(LocalDate.now().plusDays(10))
                .notes("Заметки о событии")
                .yearlyRecurrence(true)
                .build();
    }

    private Company createTestCompany() {
        return Company.builder()
                .id(1L)
                .name("Тестовая компания")
                .website("https://test-company.ru")
                .build();
    }

    private JobTitle createTestJobTitle() {
        return JobTitle.builder()
                .id(1L)
                .title("Тестовая должность")
                .build();
    }

    private Tag createTestTag() {
        return Tag.builder()
                .id(1L)
                .name("друзья")
                .build();
    }

    private ContactFullDetails createTestContactFullDetails() {
        return ContactFullDetails.builder()
                .contact(createTestContact())
                .contactDetails(Collections.singletonList(createTestContactDetail()))
                .primaryContactDetails(Collections.singletonList(createTestContactDetail()))
                .socialProfiles(Collections.singletonList(createTestSocialProfile()))
                .events(Collections.singletonList(createTestEvent()))
                .birthday(createTestBirthdayEvent())
                .notes(Collections.singletonList(createTestNote()))
                .tags(Collections.singletonList(createTestTag()))
                .contactTags(Collections.singletonList(createTestContactTag()))
                .build();
    }

    private ContactDetail createTestContactDetail() {
        return ContactDetail.builder()
                .id(1L)
                .detailType(DetailType.EMAIL)
                .label(DetailLabel.MAIN)
                .value("test@example.com")
                .isPrimary(true)
                .build();
    }

    private SocialProfile createTestSocialProfile() {
        return SocialProfile.builder()
                .id(1L)
                .platform(SocialPlatform.VK)
                .customPlatformName("ВКонтакте")
                .username("ivanov")
                .profileUrl("https://vk.com/ivanov")
                .build();
    }

    private Event createTestEvent() {
        return Event.builder()
                .id(1L)
                .eventType(EventType.ANNIVERSARY)
                .customEventName("Встреча")
                .eventDate(LocalDate.now().plusDays(5))
                .notes("Заметки о встрече")
                .yearlyRecurrence(false)
                .build();
    }

    private Event createTestBirthdayEvent() {
        return Event.builder()
                .id(2L)
                .eventType(EventType.BIRTHDAY)
                .eventDate(LocalDate.now())
                .yearlyRecurrence(true)
                .build();
    }

    private Note createTestNote() {
        return Note.builder()
                .id(1L)
                .content("Тестовая заметка")
                .build();
    }

    private ContactTag createTestContactTag() {
        return ContactTag.builder()
                .id(1L)
                .tag(createTestTag())
                .contact(createTestContact())
                .build();
    }

    @Test
    void create_WhenValidContactCreateDto_ShouldReturnCreatedContact() {
        ContactCreateDto createDto = createTestContactCreateDto();
        Contact contact = createTestContact();
        Company company = createTestCompany();
        JobTitle jobTitle = createTestJobTitle();
        Tag tag = createTestTag();

        when(contactService.save(any(Contact.class))).thenReturn(contact);
        when(companyService.findById(1L)).thenReturn(company);
        when(jobTitleService.findById(1L)).thenReturn(jobTitle);
        when(tagService.findAllById(any())).thenReturn(Collections.singletonList(tag));

        Contact result = contactManagementService.create(createDto);

        assertNotNull(result);
        assertEquals(contact.getId(), result.getId());
        verify(contactService, times(2)).save(any(Contact.class));
        verify(companyService, times(1)).findById(1L);
        verify(jobTitleService, times(1)).findById(1L);
        verify(tagService, times(1)).findAllById(any());
    }

    @Test
    void create_WhenEmptyCollectionsInDto_ShouldReturnContactWithoutRelatedEntities() {
        ContactCreateDto createDto = ContactCreateDto.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .contactCompanyCreateDtos(List.of())
                .contactDetailCreateDtos(List.of())
                .socialProfileCreateDtos(List.of())
                .tagIds(new HashSet<>())
                .build();
        Contact contact = createTestContact();

        when(contactService.save(any(Contact.class))).thenReturn(contact);

        Contact result = contactManagementService.create(createDto);

        assertNotNull(result);
        verify(contactService, times(2)).save(any(Contact.class));
        verify(companyService, never()).findById(anyLong());
        verify(jobTitleService, never()).findById(anyLong());
        verify(tagService, never()).findAllById(any());
    }

    @Test
    void update_WhenValidContactUpdateDto_ShouldReturnUpdatedContact() {
        ContactUpdateDto updateDto = createTestContactUpdateDto();
        Contact contact = createTestContact();
        Company company = createTestCompany();
        JobTitle jobTitle = createTestJobTitle();
        Tag tag = createTestTag();

        when(contactService.findById(1L)).thenReturn(contact);
        when(companyService.findById(2L)).thenReturn(company);
        when(jobTitleService.findById(2L)).thenReturn(jobTitle);
        when(tagService.findAllById(any())).thenReturn(Collections.singletonList(tag));
        when(contactService.save(contact)).thenReturn(contact);

        Contact result = contactManagementService.update(updateDto);

        assertNotNull(result);
        assertEquals(updateDto.getFirstName(), contact.getFirstName());
        assertEquals(updateDto.getLastName(), contact.getLastName());
        verify(contactService, times(1)).findById(1L);
        verify(contactService, times(1)).save(contact);
    }

    @Test
    void update_WhenContactNotFound_ShouldThrowContactNotFoundException() {
        ContactUpdateDto updateDto = createTestContactUpdateDto();

        when(contactService.findById(1L))
                .thenThrow(new ContactNotFoundException("Не найден контакт с id: 1"));

        ContactNotFoundException exception = assertThrows(
                ContactNotFoundException.class,
                () -> contactManagementService.update(updateDto)
        );

        assertTrue(exception.getMessage().contains("Не найден контакт с id: 1"));
        verify(contactService, times(1)).findById(1L);
        verify(contactService, never()).save(any(Contact.class));
    }

    @Test
    void delete_WhenValidContactId_ShouldCallServiceDelete() {
        Long contactId = 1L;

        doNothing().when(contactService).deleteById(contactId);

        contactManagementService.delete(contactId);

        verify(contactService, times(1)).deleteById(contactId);
    }

    @Test
    void getWithAllDetails_WhenContactExists_ShouldReturnFullDetails() {
        Long contactId = 1L;
        Contact contact = createTestContact();
        List<ContactDetail> contactDetails = Collections.singletonList(createTestContactDetail());
        List<SocialProfile> socialProfiles = Collections.singletonList(createTestSocialProfile());
        List<Event> events = Collections.singletonList(createTestEvent());
        List<Note> notes = Collections.singletonList(createTestNote());
        List<Tag> tags = Collections.singletonList(createTestTag());
        List<ContactTag> contactTags = Collections.singletonList(createTestContactTag());
        Event birthday = createTestBirthdayEvent();
        List<ContactDetail> primaryDetails = Collections.singletonList(createTestContactDetail());

        when(contactService.findById(contactId)).thenReturn(contact);
        when(contactDetailService.findByContactId(contactId)).thenReturn(contactDetails);
        when(socialProfileService.findByContactId(contactId)).thenReturn(socialProfiles);
        when(eventService.findByContactId(contactId)).thenReturn(events);
        when(noteService.findByContactId(contactId)).thenReturn(notes);
        when(tagService.findByContactId(contactId)).thenReturn(tags);
        when(tagService.findContactTagsByContactId(contactId)).thenReturn(contactTags);
        when(eventService.findBirthdayByContactId(contactId)).thenReturn(birthday);
        when(contactDetailService.findPrimaryByContactId(contactId)).thenReturn(primaryDetails);

        ContactFullDetails result = contactManagementService.getWithAllDetails(contactId);

        assertNotNull(result);
        assertEquals(contact, result.getContact());
        assertEquals(contactDetails, result.getContactDetails());
        assertEquals(socialProfiles, result.getSocialProfiles());
        assertEquals(events, result.getEvents());
        assertEquals(birthday, result.getBirthday());
        assertEquals(notes, result.getNotes());
        assertEquals(tags, result.getTags());
        assertEquals(contactTags, result.getContactTags());
        assertEquals(primaryDetails, result.getPrimaryContactDetails());

        verify(contactService, times(1)).findById(contactId);
        verify(contactDetailService, times(1)).findByContactId(contactId);
        verify(socialProfileService, times(1)).findByContactId(contactId);
        verify(eventService, times(1)).findByContactId(contactId);
        verify(noteService, times(1)).findByContactId(contactId);
        verify(tagService, times(1)).findByContactId(contactId);
        verify(tagService, times(1)).findContactTagsByContactId(contactId);
        verify(eventService, times(1)).findBirthdayByContactId(contactId);
        verify(contactDetailService, times(1)).findPrimaryByContactId(contactId);
    }

    @Test
    void getSummary_WhenContactExists_ShouldReturnSummaryDetails() {
        Long contactId = 1L;
        Contact contact = createTestContact();
        List<ContactDetail> primaryDetails = Collections.singletonList(createTestContactDetail());
        List<SocialProfile> mainSocialProfiles = Collections.singletonList(createTestSocialProfile());
        Event birthday = createTestBirthdayEvent();
        List<Tag> tags = Collections.singletonList(createTestTag());

        when(contactService.findById(contactId)).thenReturn(contact);
        when(contactDetailService.findPrimaryByContactId(contactId)).thenReturn(primaryDetails);
        when(socialProfileService.findByContactId(contactId)).thenReturn(mainSocialProfiles);
        when(eventService.findBirthdayByContactId(contactId)).thenReturn(birthday);
        when(tagService.findByContactId(contactId)).thenReturn(tags);

        ContactFullDetails result = contactManagementService.getSummary(contactId);

        assertNotNull(result);
        assertEquals(contact, result.getContact());
        assertEquals(primaryDetails, result.getPrimaryContactDetails());
        assertEquals(mainSocialProfiles, result.getSocialProfiles());
        assertEquals(birthday, result.getBirthday());
        assertEquals(tags, result.getTags());

        verify(contactDetailService, never()).findByContactId(contactId);
        verify(eventService, never()).findByContactId(contactId);
        verify(noteService, never()).findByContactId(contactId);
        verify(tagService, never()).findContactTagsByContactId(contactId);
    }

    @Test
    void duplicate_WhenValidContactId_ShouldReturnDuplicatedContact() {
        Long contactId = 1L;
        String newFirstName = "ДублированноеИмя";
        String newLastName = "ДублированнаяФамилия";
        ContactFullDetails originalDetails = createTestContactFullDetails();
        Contact duplicateContact = createTestContact();
        duplicateContact.setId(2L);
        duplicateContact.setFirstName(newFirstName);
        duplicateContact.setLastName(newLastName);

        when(contactService.findById(contactId)).thenReturn(originalDetails.getContact());
        when(contactService.add(newFirstName, newLastName)).thenReturn(duplicateContact);
        when(contactService.update(anyLong(), anyString(), anyString(), anyString(), anyString(), anyBoolean()))
                .thenReturn(duplicateContact);
        when(contactService.findById(duplicateContact.getId())).thenReturn(duplicateContact);

        Contact result = contactManagementService.duplicate(contactId, newFirstName, newLastName);

        assertNotNull(result);
        assertEquals(duplicateContact.getId(), result.getId());
        verify(contactService, times(1)).add(newFirstName, newLastName);
        verify(contactService, times(1)).update(anyLong(), anyString(), anyString(), anyString(), anyString(), anyBoolean());
        verify(contactService, times(1)).findById(duplicateContact.getId());
    }

    @Test
    void duplicate_WhenNullNames_ShouldUseDefaultNames() {
        Long contactId = 1L;
        ContactFullDetails originalDetails = createTestContactFullDetails();
        Contact originalContact = originalDetails.getContact();
        Contact duplicateContact = createTestContact();
        duplicateContact.setId(2L);
        duplicateContact.setFirstName(originalContact.getFirstName() + " (копия)");
        duplicateContact.setLastName(originalContact.getLastName());

        when(contactService.findById(contactId)).thenReturn(originalContact);
        when(contactService.add(originalContact.getFirstName() + " (копия)", originalContact.getLastName()))
                .thenReturn(duplicateContact);
        when(contactService.update(anyLong(), anyString(), anyString(), anyString(), anyString(), anyBoolean()))
                .thenReturn(duplicateContact);
        when(contactService.findById(duplicateContact.getId())).thenReturn(duplicateContact);

        Contact result = contactManagementService.duplicate(contactId, null, null);

        assertNotNull(result);
        assertTrue(result.getFirstName().contains("(копия)"));
        assertEquals(originalContact.getLastName(), result.getLastName());
    }

    @Test
    void getListWithUpcomingBirthdays_WhenContactsWithBirthdaysExist_ShouldReturnList() {
        int daysAhead = 7;
        List<Contact> contactsWithBirthdays = Collections.singletonList(createTestContact());
        ContactFullDetails summaryDetails = createTestContactFullDetails();

        when(contactService.findBirthdaysThisMonth()).thenReturn(contactsWithBirthdays);

        ContactManagementServiceImpl serviceSpy = spy(contactManagementService);
        doReturn(summaryDetails).when(serviceSpy).getSummary(anyLong());

        List<ContactFullDetails> result = serviceSpy.getListWithUpcomingBirthdays(daysAhead);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(contactService, times(1)).findBirthdaysThisMonth();
    }

    @Test
    void getListFavoriteWithDetails_WhenFavoriteContactsExist_ShouldReturnList() {
        List<Contact> favoriteContacts = Collections.singletonList(createTestContact());
        ContactFullDetails summaryDetails = createTestContactFullDetails();

        when(contactService.findFavorites()).thenReturn(favoriteContacts);

        ContactManagementServiceImpl serviceSpy = spy(contactManagementService);
        doReturn(summaryDetails).when(serviceSpy).getSummary(anyLong());

        List<ContactFullDetails> result = serviceSpy.getListFavoriteWithDetails();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(contactService, times(1)).findFavorites();
    }

    @Test
    void getListFavoriteWithDetails_WhenNoFavoriteContacts_ShouldReturnEmptyList() {
        when(contactService.findFavorites()).thenReturn(List.of());

        List<ContactFullDetails> result = contactManagementService.getListFavoriteWithDetails();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(contactService, times(1)).findFavorites();
    }
}
