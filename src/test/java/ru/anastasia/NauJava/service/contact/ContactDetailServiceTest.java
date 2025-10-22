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
        Contact contact = new Contact("Иван", "Иванов");
        contactRepository.save(contact);

        ContactDetail detail = new ContactDetail(DetailType.EMAIL, DetailLabel.WORK, "ivan@example.com");
        detail.setContact(contact);
        contactDetailService.create(detail);

        List<ContactDetail> details = contactDetailService.findByContactId(contact.getId());

        assertFalse(details.isEmpty());
        assertEquals("ivan@example.com", details.getFirst().getValue());
    }

    @Test
    void testFindByContactId_NoDetails() {
        Contact contact = new Contact("Иван", "Иванов");
        contactRepository.save(contact);

        List<ContactDetail> details = contactDetailService.findByContactId(contact.getId());

        assertTrue(details.isEmpty());
    }

    @Test
    void testFindByDetailType_Success() {
        Contact contact = new Contact("Иван", "Иванов");
        contactRepository.save(contact);

        ContactDetail detail = new ContactDetail(DetailType.PHONE, DetailLabel.MOBILE, "+79991234567");
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
        Contact contact = new Contact("Иван", "Иванов");
        contactRepository.save(contact);

        ContactDetail detail = new ContactDetail(DetailType.EMAIL, DetailLabel.WORK, "ivan@example.com");
        detail.setContact(contact);
        detail.setIsPrimary(true);
        contactDetailService.create(detail);

        List<ContactDetail> primaryDetails = contactDetailService.findPrimaryByContactId(contact.getId());

        assertFalse(primaryDetails.isEmpty());
        assertTrue(primaryDetails.getFirst().getIsPrimary());
        assertEquals("ivan@example.com", primaryDetails.getFirst().getValue());
    }

    @Test
    void testFindPrimaryByContactId_NoPrimary() {
        Contact contact = new Contact("Иван", "Иванов");
        contactRepository.save(contact);

        ContactDetail detail = new ContactDetail(DetailType.EMAIL, DetailLabel.WORK, "ivan@example.com");
        detail.setContact(contact);
        detail.setIsPrimary(false);
        contactDetailService.create(detail);

        List<ContactDetail> primaryDetails = contactDetailService.findPrimaryByContactId(contact.getId());

        assertTrue(primaryDetails.isEmpty());
    }

    @Test
    void testCreate_Success() {
        Contact contact = new Contact("Иван", "Иванов");
        contactRepository.save(contact);

        ContactDetail detail = new ContactDetail(DetailType.PHONE, DetailLabel.MOBILE, "+79991234567");
        detail.setContact(contact);
        ContactDetail savedDetail = contactDetailService.create(detail);

        assertNotNull(savedDetail.getId());
        assertEquals("+79991234567", savedDetail.getValue());
        assertTrue(contactDetailRepository.findById(savedDetail.getId()).isPresent());
    }

    @Test
    void testDelete_Success() {
        Contact contact = new Contact("Иван", "Иванов");
        contactRepository.save(contact);

        ContactDetail detail = new ContactDetail(DetailType.EMAIL, DetailLabel.WORK, "ivan@example.com");
        detail.setContact(contact);
        ContactDetail savedDetail = contactDetailService.create(detail);

        contactDetailService.delete(savedDetail.getId());

        assertFalse(contactDetailRepository.findById(savedDetail.getId()).isPresent());
    }

    @Test
    void testUpdate_Success() {
        Contact contact = new Contact("Иван", "Иванов");
        contactRepository.save(contact);

        ContactDetail detail = new ContactDetail(DetailType.EMAIL, DetailLabel.WORK, "ivan@example.com");
        detail.setContact(contact);
        ContactDetail savedDetail = contactDetailService.create(detail);

        ContactDetail updatedDetail = new ContactDetail(DetailType.PHONE, DetailLabel.MOBILE, "+79991234567");
        updatedDetail.setIsPrimary(true);
        ContactDetail result = contactDetailService.update(savedDetail.getId(), updatedDetail);

        assertEquals(DetailType.PHONE, result.getDetailType());
        assertEquals(DetailLabel.MOBILE, result.getLabel());
        assertEquals("+79991234567", result.getValue());
        assertTrue(result.getIsPrimary());
    }

    @Test
    void testUpdate_NotFound() {
        Long nonExistentId = 999L;
        ContactDetail updatedDetail = new ContactDetail(DetailType.PHONE, DetailLabel.MOBILE, "+79991234567");

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                contactDetailService.update(nonExistentId, updatedDetail));

        assertEquals("Не найден способ связи с id: " + nonExistentId, exception.getMessage());
    }

    @Test
    void testFindById_Success() {
        Contact contact = new Contact("Иван", "Иванов");
        contactRepository.save(contact);

        ContactDetail detail = new ContactDetail(DetailType.EMAIL, DetailLabel.WORK, "ivan@example.com");
        detail.setContact(contact);
        ContactDetail savedDetail = contactDetailService.create(detail);

        Optional<ContactDetail> foundDetail = contactDetailService.findById(savedDetail.getId());

        assertTrue(foundDetail.isPresent());
        assertEquals("ivan@example.com", foundDetail.get().getValue());
    }

    @Test
    void testFindById_NotFound() {
        Long nonExistentId = 999L;

        Optional<ContactDetail> foundDetail = contactDetailService.findById(nonExistentId);

        assertFalse(foundDetail.isPresent());
    }

    @Test
    void testFindByDetailTypeAndLabelString_Success() {
        Contact contact = new Contact("Иван", "Иванов");
        contactRepository.save(contact);

        ContactDetail detail = new ContactDetail(DetailType.EMAIL, DetailLabel.WORK, "ivan@example.com");
        detail.setContact(contact);
        detail.setIsPrimary(true);
        contactDetailService.create(detail);

        List<ContactDetail> details = contactDetailService.findByDetailTypeAndLabel(DetailType.EMAIL, "WORK");

        assertFalse(details.isEmpty());
        assertEquals("ivan@example.com", details.getFirst().getValue());
    }

    @Test
    void testFindByDetailTypeAndLabelString_InvalidLabel() {
        List<ContactDetail> details = contactDetailService.findByDetailTypeAndLabel(DetailType.EMAIL, "INVALID");

        assertTrue(details.isEmpty());
    }

    @Test
    void testFindByDetailTypeAndLabelEnum_Success() {
        Contact contact = new Contact("Иван", "Иванов");
        contactRepository.save(contact);

        ContactDetail detail = new ContactDetail(DetailType.PHONE, DetailLabel.MOBILE, "+79991234567");
        detail.setContact(contact);
        detail.setIsPrimary(true);
        contactDetailService.create(detail);

        List<ContactDetail> details = contactDetailService.findByDetailTypeAndLabel(DetailType.PHONE, DetailLabel.MOBILE);

        assertFalse(details.isEmpty());
        assertEquals("+79991234567", details.getFirst().getValue());
    }
}