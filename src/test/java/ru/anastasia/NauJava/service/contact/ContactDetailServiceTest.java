package ru.anastasia.NauJava.service.contact;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.anastasia.NauJava.entity.contact.ContactDetail;
import ru.anastasia.NauJava.entity.enums.DetailLabel;
import ru.anastasia.NauJava.entity.enums.DetailType;
import ru.anastasia.NauJava.exception.contact.ContactDetailNotFoundException;
import ru.anastasia.NauJava.repository.contact.ContactDetailRepository;
import ru.anastasia.NauJava.service.contact.impl.ContactDetailServiceImpl;

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
class ContactDetailServiceTest {

    @Mock
    private ContactDetailRepository contactDetailRepository;

    @InjectMocks
    private ContactDetailServiceImpl contactDetailService;

    @Test
    void findByContactId_ShouldReturnContactDetails() {
        Long contactId = 1L;
        ContactDetail detail1 = ContactDetail.builder().id(1L).build();
        ContactDetail detail2 = ContactDetail.builder().id(2L).build();
        List<ContactDetail> expectedDetails = List.of(detail1, detail2);

        when(contactDetailRepository.findByContactId(contactId)).thenReturn(expectedDetails);

        List<ContactDetail> result = contactDetailService.findByContactId(contactId);

        assertEquals(expectedDetails, result);
        verify(contactDetailRepository).findByContactId(contactId);
    }

    @Test
    void findByDetailType_ShouldReturnContactDetails() {
        DetailType detailType = DetailType.EMAIL;
        ContactDetail detail = ContactDetail.builder().id(1L).detailType(detailType).build();
        List<ContactDetail> expectedDetails = List.of(detail);

        when(contactDetailRepository.findByDetailTypeAndValueContainingIgnoreCase(detailType, ""))
                .thenReturn(expectedDetails);

        List<ContactDetail> result = contactDetailService.findByDetailType(detailType);

        assertEquals(expectedDetails, result);
        verify(contactDetailRepository).findByDetailTypeAndValueContainingIgnoreCase(detailType, "");
    }

    @Test
    void findPrimaryByContactId_ShouldReturnPrimaryContactDetails() {
        Long contactId = 1L;
        ContactDetail primaryDetail = ContactDetail.builder().id(1L).isPrimary(true).build();
        List<ContactDetail> expectedDetails = List.of(primaryDetail);

        when(contactDetailRepository.findPrimaryContactDetailsByContactId(contactId)).thenReturn(expectedDetails);

        List<ContactDetail> result = contactDetailService.findPrimaryByContactId(contactId);

        assertEquals(expectedDetails, result);
        verify(contactDetailRepository).findPrimaryContactDetailsByContactId(contactId);
    }

    @Test
    void create_ShouldReturnSavedContactDetail() {
        ContactDetail contactDetail = ContactDetail.builder()
                .detailType(DetailType.PHONE)
                .label(DetailLabel.MOBILE)
                .value("+123456789")
                .build();
        ContactDetail savedDetail = ContactDetail.builder()
                .id(1L)
                .detailType(DetailType.PHONE)
                .label(DetailLabel.MOBILE)
                .value("+123456789")
                .build();

        when(contactDetailRepository.save(contactDetail)).thenReturn(savedDetail);

        ContactDetail result = contactDetailService.create(contactDetail);

        assertNotNull(result.getId());
        assertEquals(savedDetail, result);
        verify(contactDetailRepository).save(contactDetail);
    }

    @Test
    void delete_ShouldCallRepositoryDelete() {
        Long id = 1L;
        doNothing().when(contactDetailRepository).deleteById(id);

        contactDetailService.delete(id);

        verify(contactDetailRepository).deleteById(id);
    }

    @Test
    void update_ShouldReturnUpdatedContactDetail_WhenExists() {
        Long id = 1L;
        ContactDetail existingDetail = ContactDetail.builder()
                .id(id)
                .detailType(DetailType.PHONE)
                .label(DetailLabel.MOBILE)
                .value("old value")
                .isPrimary(false)
                .build();
        ContactDetail updateDetail = ContactDetail.builder()
                .detailType(DetailType.EMAIL)
                .label(DetailLabel.WORK)
                .value("new value")
                .isPrimary(true)
                .build();
        ContactDetail updatedDetail = ContactDetail.builder()
                .id(id)
                .detailType(DetailType.EMAIL)
                .label(DetailLabel.WORK)
                .value("new value")
                .isPrimary(true)
                .build();

        when(contactDetailRepository.findById(id)).thenReturn(Optional.of(existingDetail));
        when(contactDetailRepository.save(existingDetail)).thenReturn(updatedDetail);

        ContactDetail result = contactDetailService.update(id, updateDetail);

        assertEquals(DetailType.EMAIL, result.getDetailType());
        assertEquals(DetailLabel.WORK, result.getLabel());
        assertEquals("new value", result.getValue());
        assertTrue(result.getIsPrimary());
        verify(contactDetailRepository).findById(id);
        verify(contactDetailRepository).save(existingDetail);
    }

    @Test
    void update_ShouldThrowContactDetailNotFoundException_WhenNotExists() {
        Long id = 999L;
        ContactDetail updateDetail = ContactDetail.builder().build();

        when(contactDetailRepository.findById(id)).thenReturn(Optional.empty());

        ContactDetailNotFoundException exception = assertThrows(
                ContactDetailNotFoundException.class,
                () -> contactDetailService.update(id, updateDetail)
        );

        assertTrue(exception.getMessage().contains("Не найден способ связи с id: " + id));
        verify(contactDetailRepository).findById(id);
        verify(contactDetailRepository, never()).save(any(ContactDetail.class));
    }

    @Test
    void findById_ShouldReturnContactDetail_WhenExists() {
        Long id = 1L;
        ContactDetail expectedDetail = ContactDetail.builder().id(id).build();

        when(contactDetailRepository.findById(id)).thenReturn(Optional.of(expectedDetail));

        ContactDetail result = contactDetailService.findById(id);

        assertEquals(expectedDetail, result);
        verify(contactDetailRepository).findById(id);
    }

    @Test
    void findById_ShouldThrowContactDetailNotFoundException_WhenNotExists() {
        Long id = 999L;

        when(contactDetailRepository.findById(id)).thenReturn(Optional.empty());

        ContactDetailNotFoundException exception = assertThrows(
                ContactDetailNotFoundException.class,
                () -> contactDetailService.findById(id)
        );

        assertTrue(exception.getMessage().contains("Не найден способ связи с id: " + id));
        verify(contactDetailRepository).findById(id);
    }

    @Test
    void findByDetailTypeAndLabel_WithStringLabel_ShouldReturnContactDetails_WhenValidLabel() {
        DetailType detailType = DetailType.PHONE;
        String label = "MOBILE";
        ContactDetail detail = ContactDetail.builder().id(1L).build();
        List<ContactDetail> expectedDetails = List.of(detail);

        when(contactDetailRepository.findByDetailTypeAndIsPrimaryTrueOrLabel(detailType, DetailLabel.MOBILE))
                .thenReturn(expectedDetails);

        List<ContactDetail> result = contactDetailService.findByDetailTypeAndLabel(detailType, label);

        assertEquals(expectedDetails, result);
        verify(contactDetailRepository).findByDetailTypeAndIsPrimaryTrueOrLabel(detailType, DetailLabel.MOBILE);
    }

    @Test
    void findByDetailTypeAndLabel_WithStringLabel_ShouldReturnEmptyList_WhenInvalidLabel() {
        DetailType detailType = DetailType.PHONE;
        String invalidLabel = "INVALID_LABEL";

        List<ContactDetail> result = contactDetailService.findByDetailTypeAndLabel(detailType, invalidLabel);

        assertTrue(result.isEmpty());
        verify(contactDetailRepository, never()).findByDetailTypeAndIsPrimaryTrueOrLabel(any(), any());
    }

    @Test
    void findByDetailTypeAndLabel_WithDetailLabel_ShouldReturnContactDetails() {
        DetailType detailType = DetailType.EMAIL;
        DetailLabel label = DetailLabel.WORK;
        ContactDetail detail = ContactDetail.builder().id(1L).build();
        List<ContactDetail> expectedDetails = List.of(detail);

        when(contactDetailRepository.findByDetailTypeAndIsPrimaryTrueOrLabel(detailType, label))
                .thenReturn(expectedDetails);

        List<ContactDetail> result = contactDetailService.findByDetailTypeAndLabel(detailType, label);

        assertEquals(expectedDetails, result);
        verify(contactDetailRepository).findByDetailTypeAndIsPrimaryTrueOrLabel(detailType, label);
    }

    @Test
    void findAll_ShouldReturnAllContactDetails() {
        ContactDetail detail1 = ContactDetail.builder().id(1L).build();
        ContactDetail detail2 = ContactDetail.builder().id(2L).build();
        List<ContactDetail> expectedDetails = List.of(detail1, detail2);

        when(contactDetailRepository.findAll()).thenReturn(expectedDetails);

        List<ContactDetail> result = contactDetailService.findAll();

        assertEquals(expectedDetails, result);
        verify(contactDetailRepository).findAll();
    }
}