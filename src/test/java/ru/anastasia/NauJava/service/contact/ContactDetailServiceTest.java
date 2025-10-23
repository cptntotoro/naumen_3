package ru.anastasia.NauJava.service.contact;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.entity.contact.ContactDetail;
import ru.anastasia.NauJava.entity.enums.DetailLabel;
import ru.anastasia.NauJava.entity.enums.DetailType;
import ru.anastasia.NauJava.repository.contact.ContactDetailRepository;
import ru.anastasia.NauJava.repository.contact.ContactRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class ContactDetailServiceTest {

    @Autowired
    private ContactDetailService contactDetailService;

    @Autowired
    private ContactDetailRepository contactDetailRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Test
    void testFindByContactId_Success() {
        Contact contact = Contact.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .build();

        contactRepository.save(contact);

        ContactDetail detail = ContactDetail.builder()
                .detailType(DetailType.EMAIL)
                .label(DetailLabel.WORK)
                .value("ivan@example.com")
                .build();

        detail.setContact(contact);
        contactDetailService.create(detail);

        List<ContactDetail> details = contactDetailService.findByContactId(contact.getId());

        assertFalse(details.isEmpty());
        assertEquals("ivan@example.com", details.getFirst().getValue());
    }

    @Test
    void testFindByContactId_NoDetails() {
        Contact contact = Contact.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .build();

        contactRepository.save(contact);

        List<ContactDetail> details = contactDetailService.findByContactId(contact.getId());

        assertTrue(details.isEmpty());
    }

    @Test
    void testFindByDetailType_Success() {
        Contact contact = Contact.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .build();

        contactRepository.save(contact);

        ContactDetail detail = ContactDetail.builder()
                .detailType(DetailType.PHONE)
                .label(DetailLabel.MOBILE)
                .value("+79991234567")
                .build();

        detail.setContact(contact);
        contactDetailService.create(detail);

        List<ContactDetail> details = contactDetailService.findByDetailType(DetailType.PHONE);

        assertFalse(details.isEmpty());
        assertEquals("+79991234567", details.getFirst().getValue());
    }

    @Test
    void testFindByDetailType_NoDetails() {
        List<ContactDetail> details = contactDetailService.findByDetailType(DetailType.EMAIL);

        assertTrue(details.isEmpty());
    }

    @Test
    void testFindPrimaryByContactId_Success() {
        Contact contact = Contact.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .build();

        contactRepository.save(contact);

        String contactDetailValue = "ivan@example.com";

        ContactDetail detail = ContactDetail.builder()
                .detailType(DetailType.EMAIL)
                .label(DetailLabel.WORK)
                .value(contactDetailValue)
                .contact(contact)
                .isPrimary(true)
                .build();

        contactDetailService.create(detail);

        List<ContactDetail> primaryDetails = contactDetailService.findPrimaryByContactId(contact.getId());

        assertFalse(primaryDetails.isEmpty());
        assertTrue(primaryDetails.getFirst().getIsPrimary());
        assertEquals(contactDetailValue, primaryDetails.getFirst().getValue());
    }

    @Test
    void testFindPrimaryByContactId_NoPrimary() {
        Contact contact = Contact.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .build();

        contactRepository.save(contact);

        ContactDetail detail = ContactDetail.builder()
                .detailType(DetailType.EMAIL)
                .label(DetailLabel.WORK)
                .value("ivan@example.com")
                .contact(contact)
                .isPrimary(false)
                .build();

        contactDetailService.create(detail);

        List<ContactDetail> primaryDetails = contactDetailService.findPrimaryByContactId(contact.getId());

        assertTrue(primaryDetails.isEmpty());
    }

    @Test
    void testCreate_Success() {
        Contact contact = Contact.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .build();

        contactRepository.save(contact);

        String contactDetailValue = "+79991234567";

        ContactDetail detail = ContactDetail.builder()
                .detailType(DetailType.PHONE)
                .label(DetailLabel.MOBILE)
                .value(contactDetailValue)
                .contact(contact)
                .build();

        ContactDetail savedDetail = contactDetailService.create(detail);

        assertNotNull(savedDetail.getId());
        assertEquals(contactDetailValue, savedDetail.getValue());
        assertTrue(contactDetailRepository.findById(savedDetail.getId()).isPresent());
    }

    @Test
    void testDelete_Success() {
        Contact contact = Contact.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .build();

        contactRepository.save(contact);

        ContactDetail detail = ContactDetail.builder()
                .detailType(DetailType.EMAIL)
                .label(DetailLabel.WORK)
                .value("ivan@example.com")
                .contact(contact)
                .build();

        ContactDetail savedDetail = contactDetailService.create(detail);

        contactDetailService.delete(savedDetail.getId());

        assertFalse(contactDetailRepository.findById(savedDetail.getId()).isPresent());
    }

    @Test
    void testUpdate_Success() {
        Contact contact = Contact.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .build();

        contactRepository.save(contact);

        ContactDetail detail = ContactDetail.builder()
                .detailType(DetailType.EMAIL)
                .label(DetailLabel.WORK)
                .value("ivan@example.com")
                .contact(contact)
                .build();

        ContactDetail savedDetail = contactDetailService.create(detail);

        String updatedContactDetailValue = "+79991234567";

        ContactDetail updatedDetail = ContactDetail.builder()
                .detailType(DetailType.PHONE)
                .label(DetailLabel.MOBILE)
                .value(updatedContactDetailValue)
                .isPrimary(true)
                .build();

        ContactDetail result = contactDetailService.update(savedDetail.getId(), updatedDetail);

        assertEquals(DetailType.PHONE, result.getDetailType());
        assertEquals(DetailLabel.MOBILE, result.getLabel());
        assertEquals(updatedContactDetailValue, result.getValue());
        assertTrue(result.getIsPrimary());
    }

    @Test
    void testUpdate_NotFound() {
        Long nonExistentId = 999L;

        ContactDetail updatedDetail = ContactDetail.builder()
                .detailType(DetailType.PHONE)
                .label(DetailLabel.MOBILE)
                .value("+79991234567")
                .build();

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                contactDetailService.update(nonExistentId, updatedDetail));

        assertEquals("Не найден способ связи с id: " + nonExistentId, exception.getMessage());
    }

    @Test
    void testFindById_Success() {
        Contact contact = Contact.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .build();

        contactRepository.save(contact);

        String contactDetailValue = "ivan@example.com";

        ContactDetail detail = ContactDetail.builder()
                .detailType(DetailType.EMAIL)
                .label(DetailLabel.WORK)
                .value(contactDetailValue)
                .contact(contact)
                .build();

        ContactDetail savedDetail = contactDetailService.create(detail);

        Optional<ContactDetail> foundDetail = contactDetailService.findById(savedDetail.getId());

        assertTrue(foundDetail.isPresent());
        assertEquals(contactDetailValue, foundDetail.get().getValue());
    }

    @Test
    void testFindById_NotFound() {
        Long nonExistentId = 999L;

        Optional<ContactDetail> foundDetail = contactDetailService.findById(nonExistentId);

        assertFalse(foundDetail.isPresent());
    }

    @Test
    void testFindByDetailTypeAndLabelString_Success() {
        Contact contact = Contact.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .build();

        contactRepository.save(contact);

        String contactDetailValue = "ivan@example.com";

        ContactDetail detail = ContactDetail.builder()
                .detailType(DetailType.EMAIL)
                .label(DetailLabel.WORK)
                .value(contactDetailValue)
                .contact(contact)
                .isPrimary(true)
                .build();

        contactDetailService.create(detail);

        List<ContactDetail> details = contactDetailService.findByDetailTypeAndLabel(DetailType.EMAIL, "WORK");

        assertFalse(details.isEmpty());
        assertEquals(contactDetailValue, details.getFirst().getValue());
    }

    @Test
    void testFindByDetailTypeAndLabelString_InvalidLabel() {
        List<ContactDetail> details = contactDetailService.findByDetailTypeAndLabel(DetailType.EMAIL, "INVALID");

        assertTrue(details.isEmpty());
    }

    @Test
    void testFindByDetailTypeAndLabelEnum_Success() {
        Contact contact = Contact.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .build();

        contactRepository.save(contact);

        String contactDetailValue = "+79991234567";

        ContactDetail detail = ContactDetail.builder()
                .detailType(DetailType.PHONE)
                .label(DetailLabel.MOBILE)
                .value(contactDetailValue)
                .contact(contact)
                .isPrimary(true)
                .build();

        contactDetailService.create(detail);

        List<ContactDetail> details = contactDetailService.findByDetailTypeAndLabel(DetailType.PHONE, DetailLabel.MOBILE);

        assertFalse(details.isEmpty());
        assertEquals(contactDetailValue, details.getFirst().getValue());
    }
}