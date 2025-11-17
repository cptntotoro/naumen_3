package ru.anastasia.NauJava.service.facade;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.anastasia.NauJava.dto.contact.ContactDetailCreateDto;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.entity.contact.ContactDetail;
import ru.anastasia.NauJava.entity.enums.DetailLabel;
import ru.anastasia.NauJava.entity.enums.DetailType;
import ru.anastasia.NauJava.exception.contact.ContactNotFoundException;
import ru.anastasia.NauJava.mapper.contact.ContactDetailMapper;
import ru.anastasia.NauJava.service.contact.ContactDetailService;
import ru.anastasia.NauJava.service.contact.ContactService;
import ru.anastasia.NauJava.service.facade.impl.ContactDetailFacadeServiceImpl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ContactDetailFacadeServiceTest {
    @Mock
    private ContactService contactService;

    @Mock
    private ContactDetailService contactDetailService;

    @Mock
    private ContactDetailMapper contactDetailMapper;

    @InjectMocks
    private ContactDetailFacadeServiceImpl contactDetailFacadeService;

    private Contact createTestContact() {
        return Contact.builder()
                .id(1L)
                .firstName("Иван")
                .lastName("Иванов")
                .build();
    }

    private ContactDetail createTestContactDetail() {
        return ContactDetail.builder()
                .id(1L)
                .detailType(DetailType.EMAIL)
                .label(DetailLabel.MAIN)
                .value("test@example.com")
                .isPrimary(true)
                .contact(createTestContact())
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

    @Test
    void addDetailsToContact_WhenValidData_ShouldReturnCreatedDetails() {
        Long contactId = 1L;
        Contact contact = createTestContact();
        List<ContactDetailCreateDto> detailDtos = Arrays.asList(
                createTestContactDetailCreateDto(),
                createTestContactDetailCreateDto()
        );
        ContactDetail detail1 = createTestContactDetail();
        ContactDetail detail2 = createTestContactDetail();
        detail2.setId(2L);

        when(contactService.findById(contactId)).thenReturn(contact);
        when(contactDetailMapper.contactDetailCreateDtoToContactDetail(any(ContactDetailCreateDto.class)))
                .thenReturn(detail1, detail2);
        when(contactDetailService.create(any(ContactDetail.class)))
                .thenReturn(detail1, detail2);

        List<ContactDetail> result = contactDetailFacadeService.addDetailsToContact(contactId, detailDtos);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(contactService, times(1)).findById(contactId);
        verify(contactDetailMapper, times(2)).contactDetailCreateDtoToContactDetail(any(ContactDetailCreateDto.class));
        verify(contactDetailService, times(2)).create(any(ContactDetail.class));
    }

    @Test
    void addDetailsToContact_WhenContactNotFound_ShouldThrowContactNotFoundException() {
        Long contactId = 1L;
        List<ContactDetailCreateDto> detailDtos = Collections.singletonList(createTestContactDetailCreateDto());

        when(contactService.findById(contactId))
                .thenThrow(new ContactNotFoundException("Контакт не найден"));

        ContactNotFoundException exception = assertThrows(
                ContactNotFoundException.class,
                () -> contactDetailFacadeService.addDetailsToContact(contactId, detailDtos)
        );

        assertTrue(exception.getMessage().contains("Контакт не найден"));
        verify(contactService, times(1)).findById(contactId);
        verify(contactDetailMapper, never()).contactDetailCreateDtoToContactDetail(any());
        verify(contactDetailService, never()).create(any(ContactDetail.class));
    }

    @Test
    void addDetailsToContact_WhenEmptyDetailsList_ShouldReturnEmptyList() {
        Long contactId = 1L;
        Contact contact = createTestContact();
        List<ContactDetailCreateDto> emptyDetailDtos = List.of();

        when(contactService.findById(contactId)).thenReturn(contact);

        List<ContactDetail> result = contactDetailFacadeService.addDetailsToContact(contactId, emptyDetailDtos);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(contactService, times(1)).findById(contactId);
        verify(contactDetailMapper, never()).contactDetailCreateDtoToContactDetail(any());
        verify(contactDetailService, never()).create(any(ContactDetail.class));
    }

    @Test
    void getPrimaryContactDetails_WhenContactExists_ShouldReturnPrimaryDetails() {
        Long contactId = 1L;
        Contact contact = createTestContact();
        List<ContactDetail> primaryDetails = Collections.singletonList(createTestContactDetail());

        when(contactService.findById(contactId)).thenReturn(contact);
        when(contactDetailService.findPrimaryByContactId(contactId)).thenReturn(primaryDetails);

        List<ContactDetail> result = contactDetailFacadeService.getPrimaryContactDetails(contactId);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(contactService, times(1)).findById(contactId);
        verify(contactDetailService, times(1)).findPrimaryByContactId(contactId);
    }

    @Test
    void getPrimaryContactDetails_WhenContactNotFound_ShouldThrowContactNotFoundException() {
        Long contactId = 1L;

        when(contactService.findById(contactId))
                .thenThrow(new ContactNotFoundException("Контакт не найден"));

        ContactNotFoundException exception = assertThrows(
                ContactNotFoundException.class,
                () -> contactDetailFacadeService.getPrimaryContactDetails(contactId)
        );

        assertTrue(exception.getMessage().contains("Контакт не найден"));
        verify(contactService, times(1)).findById(contactId);
        verify(contactDetailService, never()).findPrimaryByContactId(anyLong());
    }

    @Test
    void updateContactDetails_WhenValidData_ShouldReturnUpdatedDetails() {
        Long contactId = 1L;
        Contact contact = createTestContact();
        List<ContactDetailCreateDto> newDetailDtos = Collections.singletonList(createTestContactDetailCreateDto());
        List<ContactDetail> existingDetails = Collections.singletonList(createTestContactDetail());
        ContactDetail newDetail = createTestContactDetail();
        newDetail.setId(2L);

        when(contactService.findById(contactId)).thenReturn(contact);
        when(contactDetailService.findByContactId(contactId)).thenReturn(existingDetails);
        doNothing().when(contactDetailService).delete(anyLong());
        when(contactDetailMapper.contactDetailCreateDtoToContactDetail(any(ContactDetailCreateDto.class)))
                .thenReturn(newDetail);
        when(contactDetailService.create(any(ContactDetail.class))).thenReturn(newDetail);

        List<ContactDetail> result = contactDetailFacadeService.updateContactDetails(contactId, newDetailDtos);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(contactService, times(2)).findById(contactId);
        verify(contactDetailService, times(1)).findByContactId(contactId);
        verify(contactDetailService, times(1)).delete(existingDetails.getFirst().getId());
        verify(contactDetailMapper, times(1)).contactDetailCreateDtoToContactDetail(any(ContactDetailCreateDto.class));
        verify(contactDetailService, times(1)).create(any(ContactDetail.class));
    }

    @Test
    void updateContactDetails_WhenNoExistingDetails_ShouldAddNewDetails() {
        Long contactId = 1L;
        Contact contact = createTestContact();
        List<ContactDetailCreateDto> newDetailDtos = Collections.singletonList(createTestContactDetailCreateDto());
        List<ContactDetail> emptyExistingDetails = List.of();
        ContactDetail newDetail = createTestContactDetail();

        when(contactService.findById(contactId)).thenReturn(contact);
        when(contactDetailService.findByContactId(contactId)).thenReturn(emptyExistingDetails);
        when(contactDetailMapper.contactDetailCreateDtoToContactDetail(any(ContactDetailCreateDto.class)))
                .thenReturn(newDetail);
        when(contactDetailService.create(any(ContactDetail.class))).thenReturn(newDetail);

        List<ContactDetail> result = contactDetailFacadeService.updateContactDetails(contactId, newDetailDtos);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(contactService, times(2)).findById(contactId);
        verify(contactDetailService, times(1)).findByContactId(contactId);
        verify(contactDetailService, never()).delete(anyLong());
        verify(contactDetailMapper, times(1)).contactDetailCreateDtoToContactDetail(any(ContactDetailCreateDto.class));
        verify(contactDetailService, times(1)).create(any(ContactDetail.class));
    }
}
