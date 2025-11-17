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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContactDetailServiceTest {

    @Mock
    private ContactDetailRepository contactDetailRepository;

    @InjectMocks
    private ContactDetailServiceImpl contactDetailService;

    private ContactDetail createTestContactDetail() {
        return ContactDetail.builder()
                .id(1L)
                .detailType(DetailType.EMAIL)
                .label(DetailLabel.MAIN)
                .value("test@example.com")
                .isPrimary(true)
                .build();
    }

    private ContactDetail createAnotherTestContactDetail() {
        return ContactDetail.builder()
                .id(2L)
                .detailType(DetailType.PHONE)
                .label(DetailLabel.WORK)
                .value("+79991234567")
                .isPrimary(false)
                .build();
    }

    @Test
    void findByContactId_WhenContactDetailsExist_ShouldReturnList() {
        Long contactId = 1L;
        List<ContactDetail> expectedDetails = Arrays.asList(
                createTestContactDetail(),
                createAnotherTestContactDetail()
        );

        when(contactDetailRepository.findByContactId(contactId)).thenReturn(expectedDetails);

        List<ContactDetail> result = contactDetailService.findByContactId(contactId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(contactDetailRepository, times(1)).findByContactId(contactId);
    }

    @Test
    void findByContactId_WhenNoContactDetails_ShouldReturnEmptyList() {
        Long contactId = 999L;

        when(contactDetailRepository.findByContactId(contactId)).thenReturn(List.of());

        List<ContactDetail> result = contactDetailService.findByContactId(contactId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(contactDetailRepository, times(1)).findByContactId(contactId);
    }

    @Test
    void findByDetailType_WhenDetailsExist_ShouldReturnList() {
        DetailType detailType = DetailType.EMAIL;
        List<ContactDetail> expectedDetails = Collections.singletonList(createTestContactDetail());

        when(contactDetailRepository.findByDetailTypeAndValueContainingIgnoreCase(detailType, ""))
                .thenReturn(expectedDetails);

        List<ContactDetail> result = contactDetailService.findByDetailType(detailType);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(contactDetailRepository, times(1))
                .findByDetailTypeAndValueContainingIgnoreCase(detailType, "");
    }

    @Test
    void findPrimaryByContactId_WhenPrimaryDetailsExist_ShouldReturnList() {
        Long contactId = 1L;
        List<ContactDetail> expectedDetails = Collections.singletonList(createTestContactDetail());

        when(contactDetailRepository.findPrimaryContactDetailsByContactId(contactId))
                .thenReturn(expectedDetails);

        List<ContactDetail> result = contactDetailService.findPrimaryByContactId(contactId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.getFirst().getIsPrimary());
        verify(contactDetailRepository, times(1))
                .findPrimaryContactDetailsByContactId(contactId);
    }

    @Test
    void create_WhenValidContactDetail_ShouldReturnSavedDetail() {
        ContactDetail testDetail = createTestContactDetail();
        ContactDetail savedDetail = createTestContactDetail();

        when(contactDetailRepository.save(testDetail)).thenReturn(savedDetail);

        ContactDetail result = contactDetailService.create(testDetail);

        assertNotNull(result);
        assertEquals(savedDetail.getId(), result.getId());
        assertEquals(savedDetail.getValue(), result.getValue());
        verify(contactDetailRepository, times(1)).save(testDetail);
    }

    @Test
    void delete_WhenValidId_ShouldCallRepositoryDelete() {
        Long detailId = 1L;

        doNothing().when(contactDetailRepository).deleteById(detailId);

        contactDetailService.delete(detailId);

        verify(contactDetailRepository, times(1)).deleteById(detailId);
    }

    @Test
    void update_WhenValidContactDetail_ShouldReturnUpdatedDetail() {
        Long detailId = 1L;
        ContactDetail existingDetail = createTestContactDetail();
        ContactDetail updatedData = ContactDetail.builder()
                .id(detailId)
                .detailType(DetailType.PHONE)
                .label(DetailLabel.WORK)
                .value("+79998887766")
                .isPrimary(false)
                .build();
        ContactDetail savedDetail = ContactDetail.builder()
                .id(detailId)
                .detailType(DetailType.PHONE)
                .label(DetailLabel.WORK)
                .value("+79998887766")
                .isPrimary(false)
                .build();

        when(contactDetailRepository.findById(detailId)).thenReturn(Optional.of(existingDetail));
        when(contactDetailRepository.save(existingDetail)).thenReturn(savedDetail);

        ContactDetail result = contactDetailService.update(detailId, updatedData);

        assertNotNull(result);
        assertEquals(updatedData.getDetailType(), result.getDetailType());
        assertEquals(updatedData.getValue(), result.getValue());
        assertEquals(updatedData.getIsPrimary(), result.getIsPrimary());
        verify(contactDetailRepository, times(1)).findById(detailId);
        verify(contactDetailRepository, times(1)).save(existingDetail);
    }

    @Test
    void update_WhenContactDetailNotFound_ShouldThrowContactDetailNotFoundException() {
        Long nonExistentId = 999L;
        ContactDetail updatedData = createTestContactDetail();
        updatedData.setId(nonExistentId);

        when(contactDetailRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        ContactDetailNotFoundException exception = assertThrows(
                ContactDetailNotFoundException.class,
                () -> contactDetailService.update(nonExistentId, updatedData)
        );

        assertTrue(exception.getMessage().contains("Не найден способ связи с id: " + nonExistentId));
        verify(contactDetailRepository, times(1)).findById(nonExistentId);
        verify(contactDetailRepository, never()).save(any(ContactDetail.class));
    }

    @Test
    void findById_WhenContactDetailExists_ShouldReturnDetail() {
        Long detailId = 1L;
        ContactDetail testDetail = createTestContactDetail();

        when(contactDetailRepository.findById(detailId)).thenReturn(Optional.of(testDetail));

        ContactDetail result = contactDetailService.findById(detailId);

        assertNotNull(result);
        assertEquals(testDetail.getId(), result.getId());
        assertEquals(testDetail.getValue(), result.getValue());
        verify(contactDetailRepository, times(1)).findById(detailId);
    }

    @Test
    void findById_WhenContactDetailNotExists_ShouldThrowContactDetailNotFoundException() {
        Long nonExistentId = 999L;

        when(contactDetailRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        ContactDetailNotFoundException exception = assertThrows(
                ContactDetailNotFoundException.class,
                () -> contactDetailService.findById(nonExistentId)
        );

        assertTrue(exception.getMessage().contains("Не найден способ связи с id: " + nonExistentId));
        verify(contactDetailRepository, times(1)).findById(nonExistentId);
    }

    @Test
    void findByDetailTypeAndLabel_WithValidStringLabel_ShouldReturnList() {
        DetailType detailType = DetailType.EMAIL;
        String label = "MAIN";
        List<ContactDetail> expectedDetails = Collections.singletonList(createTestContactDetail());

        when(contactDetailRepository.findByDetailTypeAndIsPrimaryTrueOrLabel(detailType, DetailLabel.MAIN))
                .thenReturn(expectedDetails);

        List<ContactDetail> result = contactDetailService.findByDetailTypeAndLabel(detailType, label);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(contactDetailRepository, times(1))
                .findByDetailTypeAndIsPrimaryTrueOrLabel(detailType, DetailLabel.MAIN);
    }

    @Test
    void findByDetailTypeAndLabel_WithInvalidStringLabel_ShouldReturnEmptyList() {
        DetailType detailType = DetailType.EMAIL;
        String invalidLabel = "INVALID_LABEL";

        List<ContactDetail> result = contactDetailService.findByDetailTypeAndLabel(detailType, invalidLabel);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(contactDetailRepository, never())
                .findByDetailTypeAndIsPrimaryTrueOrLabel(any(), any());
    }

    @Test
    void findByDetailTypeAndLabel_WithDetailLabel_ShouldReturnList() {
        DetailType detailType = DetailType.PHONE;
        DetailLabel label = DetailLabel.WORK;
        List<ContactDetail> expectedDetails = Collections.singletonList(createAnotherTestContactDetail());

        when(contactDetailRepository.findByDetailTypeAndIsPrimaryTrueOrLabel(detailType, label))
                .thenReturn(expectedDetails);

        List<ContactDetail> result = contactDetailService.findByDetailTypeAndLabel(detailType, label);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(contactDetailRepository, times(1))
                .findByDetailTypeAndIsPrimaryTrueOrLabel(detailType, label);
    }

    @Test
    void findAll_WhenContactDetailsExist_ShouldReturnAllDetails() {
        List<ContactDetail> expectedDetails = Arrays.asList(
                createTestContactDetail(),
                createAnotherTestContactDetail()
        );

        when(contactDetailRepository.findAll()).thenReturn(expectedDetails);

        List<ContactDetail> result = contactDetailService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(contactDetailRepository, times(1)).findAll();
    }

    @Test
    void findAll_WhenNoContactDetails_ShouldReturnEmptyList() {
        when(contactDetailRepository.findAll()).thenReturn(List.of());

        List<ContactDetail> result = contactDetailService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(contactDetailRepository, times(1)).findAll();
    }
}