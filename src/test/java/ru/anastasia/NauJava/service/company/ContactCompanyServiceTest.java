package ru.anastasia.NauJava.service.company;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.anastasia.NauJava.dto.company.ContactCompanyCreateDto;
import ru.anastasia.NauJava.dto.company.ContactCompanyUpdateDto;
import ru.anastasia.NauJava.entity.company.Company;
import ru.anastasia.NauJava.entity.company.ContactCompany;
import ru.anastasia.NauJava.entity.company.JobTitle;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.exception.company.IllegalCompanyStateException;
import ru.anastasia.NauJava.repository.company.ContactCompanyRepository;
import ru.anastasia.NauJava.service.company.impl.ContactCompanyServiceImpl;
import ru.anastasia.NauJava.service.contact.ContactService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContactCompanyServiceTest {

    @Mock
    private ContactCompanyRepository contactCompanyRepository;

    @Mock
    private ContactService contactService;

    @Mock
    private CompanyService companyService;

    @Mock
    private JobTitleService jobTitleService;

    @InjectMocks
    private ContactCompanyServiceImpl contactCompanyService;

    private Contact contact;
    private Company company;
    private JobTitle jobTitle;
    private ContactCompany contactCompany;
    private ContactCompanyCreateDto createDto;
    private ContactCompanyUpdateDto updateDto;

    @BeforeEach
    void setUp() {
        contact = Contact.builder()
                .id(1L)
                .firstName("Иван")
                .lastName("Иванов")
                .build();

        company = Company.builder()
                .id(1L)
                .name("ООО Тест")
                .build();

        jobTitle = JobTitle.builder()
                .id(1L)
                .title("Разработчик")
                .build();

        contactCompany = ContactCompany.builder()
                .id(1L)
                .contact(contact)
                .company(company)
                .jobTitle(jobTitle)
                .isCurrent(true)
                .build();

        createDto = ContactCompanyCreateDto.builder()
                .companyId(1L)
                .jobTitleId(1L)
                .isCurrent(true)
                .build();

        updateDto = ContactCompanyUpdateDto.builder()
                .companyId(1L)
                .jobTitleId(1L)
                .isCurrent(true)
                .build();
    }

    @Test
    void create_ShouldCreateContactCompanySuccessfully() {
        Long contactId = 1L;
        when(contactService.findById(contactId)).thenReturn(contact);
        when(companyService.findById(createDto.getCompanyId())).thenReturn(company);
        when(jobTitleService.findById(createDto.getJobTitleId())).thenReturn(jobTitle);
        when(contactCompanyRepository.findByContactId(contactId)).thenReturn(List.of());
        when(contactCompanyRepository.save(any(ContactCompany.class))).thenReturn(contactCompany);

        ContactCompany result = contactCompanyService.create(createDto, contactId);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertTrue(result.getIsCurrent());
        verify(contactCompanyRepository).save(any(ContactCompany.class));
        verify(contactCompanyRepository, never()).saveAll(anyList());
    }

    @Test
    void create_ShouldResetCurrentFlagWhenCreatingNewCurrent() {
        Long contactId = 1L;
        ContactCompany existingContactCompany = ContactCompany.builder()
                .id(2L)
                .contact(contact)
                .company(Company.builder().id(2L).name("Другая компания").build())
                .jobTitle(jobTitle)
                .isCurrent(true)
                .build();

        List<ContactCompany> existingCompanies = List.of(existingContactCompany);

        when(contactService.findById(contactId)).thenReturn(contact);
        when(companyService.findById(createDto.getCompanyId())).thenReturn(company);
        when(jobTitleService.findById(createDto.getJobTitleId())).thenReturn(jobTitle);
        when(contactCompanyRepository.findByContactId(contactId)).thenReturn(existingCompanies);
        when(contactCompanyRepository.save(any(ContactCompany.class))).thenReturn(contactCompany);

        contactCompanyService.create(createDto, contactId);

        verify(contactCompanyRepository).saveAll(argThat((List<ContactCompany> list) -> {
            if (list.isEmpty()) return false;
            ContactCompany cc = list.getFirst();
            return !cc.getIsCurrent();
        }));
    }

    @Test
    void findById_ShouldReturnContactCompanyWhenExists() {
        Long contactCompanyId = 1L;
        when(contactCompanyRepository.findById(contactCompanyId)).thenReturn(Optional.of(contactCompany));

        ContactCompany result = contactCompanyService.findById(contactCompanyId);

        assertNotNull(result);
        assertEquals(contactCompanyId, result.getId());
    }

    @Test
    void findById_ShouldThrowExceptionWhenNotFound() {
        Long contactCompanyId = 99L;
        when(contactCompanyRepository.findById(contactCompanyId)).thenReturn(Optional.empty());

        IllegalCompanyStateException exception = assertThrows(
                IllegalCompanyStateException.class,
                () -> contactCompanyService.findById(contactCompanyId)
        );
        assertTrue(exception.getMessage().contains("Связь не найдена с id: " + contactCompanyId));
    }

    @Test
    void findByContactId_ShouldReturnListOfContactCompanies() {
        Long contactId = 1L;
        List<ContactCompany> contactCompanies = Collections.singletonList(contactCompany);
        when(contactCompanyRepository.findByContactId(contactId)).thenReturn(contactCompanies);

        List<ContactCompany> result = contactCompanyService.findByContactId(contactId);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(contactId, result.getFirst().getContact().getId());
    }

    @Test
    void findByContactId_ShouldReturnEmptyListWhenNoCompanies() {
        Long contactId = 99L;
        when(contactCompanyRepository.findByContactId(contactId)).thenReturn(Collections.emptyList());

        List<ContactCompany> result = contactCompanyService.findByContactId(contactId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void update_ShouldUpdateContactCompanySuccessfully() {
        Long contactCompanyId = 1L;
        when(contactCompanyRepository.findById(contactCompanyId)).thenReturn(Optional.of(contactCompany));
        when(companyService.findById(updateDto.getCompanyId())).thenReturn(company);
        when(jobTitleService.findById(updateDto.getJobTitleId())).thenReturn(jobTitle);
        when(contactCompanyRepository.findByContactId(contact.getId())).thenReturn(List.of());
        when(contactCompanyRepository.save(any(ContactCompany.class))).thenReturn(contactCompany);

        ContactCompany result = contactCompanyService.update(contactCompanyId, updateDto);

        assertNotNull(result);
        verify(contactCompanyRepository).save(contactCompany);
    }

    @Test
    void update_ShouldThrowExceptionWhenNotFound() {
        Long contactCompanyId = 99L;
        when(contactCompanyRepository.findById(contactCompanyId)).thenReturn(Optional.empty());

        IllegalCompanyStateException exception = assertThrows(
                IllegalCompanyStateException.class,
                () -> contactCompanyService.update(contactCompanyId, updateDto)
        );
        assertTrue(exception.getMessage().contains("Связь не найдена с id: " + contactCompanyId));
    }

    @Test
    void update_ShouldHandlePartialUpdate() {
        Long contactCompanyId = 1L;
        ContactCompanyUpdateDto partialUpdateDto = ContactCompanyUpdateDto.builder()
                .isCurrent(false)
                .build();

        when(contactCompanyRepository.findById(contactCompanyId)).thenReturn(Optional.of(contactCompany));
        when(contactCompanyRepository.save(any(ContactCompany.class))).thenReturn(contactCompany);

        ContactCompany result = contactCompanyService.update(contactCompanyId, partialUpdateDto);

        assertNotNull(result);
        assertFalse(result.getIsCurrent());
        verify(companyService, never()).findById(anyLong());
        verify(jobTitleService, never()).findById(anyLong());
    }

    @Test
    void update_ShouldOnlyUpdateCompanyWhenCompanyIdProvided() {
        Long contactCompanyId = 1L;
        Company newCompany = Company.builder().id(2L).name("Новая компания").build();
        ContactCompanyUpdateDto companyOnlyUpdateDto = ContactCompanyUpdateDto.builder()
                .companyId(2L)
                .build();

        when(contactCompanyRepository.findById(contactCompanyId)).thenReturn(Optional.of(contactCompany));
        when(companyService.findById(2L)).thenReturn(newCompany);
        when(contactCompanyRepository.save(any(ContactCompany.class))).thenReturn(contactCompany);

        ContactCompany result = contactCompanyService.update(contactCompanyId, companyOnlyUpdateDto);

        assertNotNull(result);
        verify(companyService).findById(2L);
        verify(jobTitleService, never()).findById(anyLong());
    }

    @Test
    void delete_ShouldDeleteContactCompanyWhenExists() {
        Long contactCompanyId = 1L;
        when(contactCompanyRepository.findById(contactCompanyId)).thenReturn(Optional.of(contactCompany));
        doNothing().when(contactCompanyRepository).delete(contactCompany);

        contactCompanyService.delete(contactCompanyId);

        verify(contactCompanyRepository).delete(contactCompany);
    }

    @Test
    void delete_ShouldNotThrowExceptionWhenNotFound() {
        Long contactCompanyId = 99L;
        when(contactCompanyRepository.findById(contactCompanyId)).thenReturn(Optional.empty());

        IllegalCompanyStateException exception = assertThrows(
                IllegalCompanyStateException.class,
                () -> contactCompanyService.delete(contactCompanyId)
        );

        assertTrue(exception.getMessage().contains("Связь не найдена с id: " + contactCompanyId));

        verify(contactCompanyRepository, never()).delete(any());
    }

    @Test
    void findCurrentByContactId_ShouldReturnCurrentContactCompany() {
        Long contactId = 1L;
        when(contactCompanyRepository.findCurrentByContactId(contactId)).thenReturn(Optional.of(contactCompany));

        ContactCompany result = contactCompanyService.findCurrentByContactId(contactId);

        assertNotNull(result);
        assertTrue(result.getIsCurrent());
    }

    @Test
    void findCurrentByContactId_ShouldReturnNullWhenNoCurrent() {
        Long contactId = 1L;
        when(contactCompanyRepository.findCurrentByContactId(contactId)).thenReturn(Optional.empty());

        ContactCompany result = contactCompanyService.findCurrentByContactId(contactId);

        assertNull(result);
    }

    @Test
    void countContactsInCompany_ShouldReturnCorrectCount() {
        Long companyId = 1L;
        Long expectedCount = 5L;
        when(contactCompanyRepository.countByCompanyId(companyId)).thenReturn(expectedCount);

        Long result = contactCompanyService.countContactsInCompany(companyId);

        assertEquals(expectedCount, result);
        verify(contactCompanyRepository).countByCompanyId(companyId);
    }

    @Test
    void countContactsInCompany_ShouldReturnZeroWhenNoContacts() {
        Long companyId = 99L;
        when(contactCompanyRepository.countByCompanyId(companyId)).thenReturn(0L);

        Long result = contactCompanyService.countContactsInCompany(companyId);

        assertEquals(0L, result);
    }

    @Test
    void deleteByContactId_ShouldDeleteAllContactCompaniesForContact() {
        Long contactId = 1L;
        List<ContactCompany> contactCompanies = Collections.singletonList(contactCompany);
        when(contactCompanyRepository.findByContactId(contactId)).thenReturn(contactCompanies);
        doNothing().when(contactCompanyRepository).deleteByContactId(contactId);

        contactCompanyService.deleteByContactId(contactId);

        verify(contactCompanyRepository).deleteByContactId(contactId);
    }

    @Test
    void deleteByContactId_ShouldWorkWithEmptyList() {
        Long contactId = 99L;
        when(contactCompanyRepository.findByContactId(contactId)).thenReturn(Collections.emptyList());
        doNothing().when(contactCompanyRepository).deleteByContactId(contactId);

        contactCompanyService.deleteByContactId(contactId);

        verify(contactCompanyRepository).deleteByContactId(contactId);
    }

    @Test
    void create_ShouldHandleNullIsCurrentField() {
        Long contactId = 1L;
        ContactCompanyCreateDto dtoWithNullCurrent = ContactCompanyCreateDto.builder()
                .companyId(1L)
                .jobTitleId(1L)
                .isCurrent(null)
                .build();

        ContactCompany savedContactCompany = ContactCompany.builder()
                .id(1L)
                .contact(contact)
                .company(company)
                .jobTitle(jobTitle)
                .isCurrent(null)
                .build();

        when(contactService.findById(contactId)).thenReturn(contact);
        when(companyService.findById(dtoWithNullCurrent.getCompanyId())).thenReturn(company);
        when(jobTitleService.findById(dtoWithNullCurrent.getJobTitleId())).thenReturn(jobTitle);
        when(contactCompanyRepository.save(any(ContactCompany.class))).thenReturn(savedContactCompany);

        ContactCompany result = contactCompanyService.create(dtoWithNullCurrent, contactId);

        assertNotNull(result);
        assertNull(result.getIsCurrent());
        verify(contactCompanyRepository, never()).saveAll(anyList());
    }

    @Test
    void create_ShouldHandleFalseIsCurrentField() {
        Long contactId = 1L;
        ContactCompanyCreateDto dtoWithFalseCurrent = ContactCompanyCreateDto.builder()
                .companyId(1L)
                .jobTitleId(1L)
                .isCurrent(false)
                .build();

        ContactCompany savedContactCompany = ContactCompany.builder()
                .id(1L)
                .contact(contact)
                .company(company)
                .jobTitle(jobTitle)
                .isCurrent(false)
                .build();

        when(contactService.findById(contactId)).thenReturn(contact);
        when(companyService.findById(dtoWithFalseCurrent.getCompanyId())).thenReturn(company);
        when(jobTitleService.findById(dtoWithFalseCurrent.getJobTitleId())).thenReturn(jobTitle);
        when(contactCompanyRepository.save(any(ContactCompany.class))).thenReturn(savedContactCompany);

        ContactCompany result = contactCompanyService.create(dtoWithFalseCurrent, contactId);

        assertNotNull(result);
        assertFalse(result.getIsCurrent());
        verify(contactCompanyRepository, never()).saveAll(anyList());
    }

    @Test
    void update_ShouldNotResetCurrentFlagWhenIsCurrentIsFalse() {
        Long contactCompanyId = 1L;
        ContactCompanyUpdateDto updateDtoFalse = ContactCompanyUpdateDto.builder()
                .companyId(1L)
                .jobTitleId(1L)
                .isCurrent(false)
                .build();

        when(contactCompanyRepository.findById(contactCompanyId)).thenReturn(Optional.of(contactCompany));
        when(companyService.findById(updateDtoFalse.getCompanyId())).thenReturn(company);
        when(jobTitleService.findById(updateDtoFalse.getJobTitleId())).thenReturn(jobTitle);
        when(contactCompanyRepository.save(any(ContactCompany.class))).thenReturn(contactCompany);

        ContactCompany result = contactCompanyService.update(contactCompanyId, updateDtoFalse);

        assertNotNull(result);
        verify(contactCompanyRepository, never()).findByContactId(anyLong());
        verify(contactCompanyRepository, never()).saveAll(anyList());
    }

    @Test
    void update_ShouldNotResetCurrentFlagWhenIsCurrentIsNull() {
        Long contactCompanyId = 1L;
        ContactCompanyUpdateDto updateDtoNull = ContactCompanyUpdateDto.builder()
                .companyId(1L)
                .jobTitleId(1L)
                .isCurrent(null)
                .build();

        when(contactCompanyRepository.findById(contactCompanyId)).thenReturn(Optional.of(contactCompany));
        when(companyService.findById(updateDtoNull.getCompanyId())).thenReturn(company);
        when(jobTitleService.findById(updateDtoNull.getJobTitleId())).thenReturn(jobTitle);
        when(contactCompanyRepository.save(any(ContactCompany.class))).thenReturn(contactCompany);

        ContactCompany result = contactCompanyService.update(contactCompanyId, updateDtoNull);

        assertNotNull(result);
        verify(contactCompanyRepository, never()).findByContactId(anyLong());
        verify(contactCompanyRepository, never()).saveAll(anyList());
    }

    @Test
    void update_ShouldResetCurrentFlagForOtherCompaniesWhenSettingNewCurrent() {
        Long contactCompanyId = 1L;

        ContactCompany existingCurrent = ContactCompany.builder()
                .id(2L)
                .contact(contact)
                .company(Company.builder().id(2L).name("Старая компания").build())
                .jobTitle(jobTitle)
                .isCurrent(true)
                .build();

        List<ContactCompany> existingCompanies = List.of(existingCurrent);

        when(contactCompanyRepository.findById(contactCompanyId)).thenReturn(Optional.of(contactCompany));
        when(companyService.findById(updateDto.getCompanyId())).thenReturn(company);
        when(jobTitleService.findById(updateDto.getJobTitleId())).thenReturn(jobTitle);
        when(contactCompanyRepository.findByContactId(contact.getId())).thenReturn(existingCompanies);
        when(contactCompanyRepository.save(any(ContactCompany.class))).thenReturn(contactCompany);

        contactCompanyService.update(contactCompanyId, updateDto);

        verify(contactCompanyRepository).saveAll(argThat((List<ContactCompany> list) ->
                !list.isEmpty() && !list.getFirst().getIsCurrent()
        ));
    }

    @Test
    void create_ShouldNotResetCurrentFlagWhenIsCurrentIsFalseAndExistingCurrentExists() {
        Long contactId = 1L;

        ContactCompanyCreateDto dtoFalseCurrent = ContactCompanyCreateDto.builder()
                .companyId(1L)
                .jobTitleId(1L)
                .isCurrent(false)
                .build();

        when(contactService.findById(contactId)).thenReturn(contact);
        when(companyService.findById(dtoFalseCurrent.getCompanyId())).thenReturn(company);
        when(jobTitleService.findById(dtoFalseCurrent.getJobTitleId())).thenReturn(jobTitle);
        when(contactCompanyRepository.save(any(ContactCompany.class))).thenReturn(
                ContactCompany.builder()
                        .id(3L)
                        .contact(contact)
                        .company(company)
                        .jobTitle(jobTitle)
                        .isCurrent(false)
                        .build()
        );

        contactCompanyService.create(dtoFalseCurrent, contactId);

        verify(contactCompanyRepository, never()).saveAll(anyList());
    }
}