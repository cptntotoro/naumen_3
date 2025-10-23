package ru.anastasia.NauJava.repository.contact;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.entity.contact.ContactDetail;
import ru.anastasia.NauJava.entity.enums.DetailLabel;
import ru.anastasia.NauJava.entity.enums.DetailType;

import java.util.List;

@SpringBootTest
@Transactional
class ContactDetailRepositoryTest {

    @Autowired
    private ContactDetailRepository contactDetailRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Test
    void testFindByDetailTypeAndIsPrimaryTrueOrLabel() {
        Contact contact = Contact.builder()
                .firstName("Detail")
                .lastName("Test")
                .build();

        Contact savedContact = contactRepository.save(contact);

        ContactDetail primaryEmail = ContactDetail.builder()
                .contact(savedContact)
                .detailType(DetailType.EMAIL)
                .label(DetailLabel.WORK)
                .value("primary@test.com")
                .isPrimary(true)
                .build();

        contactDetailRepository.save(primaryEmail);

        ContactDetail secondaryEmail = ContactDetail.builder()
                .contact(savedContact)
                .detailType(DetailType.EMAIL)
                .label(DetailLabel.HOME)
                .value("secondary@test.com")
                .isPrimary(false)
                .build();

        contactDetailRepository.save(secondaryEmail);

        ContactDetail mobilePhone = ContactDetail.builder()
                .contact(savedContact)
                .detailType(DetailType.PHONE)
                .label(DetailLabel.MOBILE)
                .value("+1234567890")
                .isPrimary(false)
                .build();

        contactDetailRepository.save(mobilePhone);

        List<ContactDetail> foundDetails = contactDetailRepository
                .findByDetailTypeAndIsPrimaryTrueOrLabel(DetailType.EMAIL, DetailLabel.MOBILE);

        Assertions.assertNotNull(foundDetails);
        Assertions.assertFalse(foundDetails.isEmpty());
    }

    @Test
    void testFindPrimaryContactDetailsByContactId() {
        Contact contact = Contact.builder()
                .firstName("Primary")
                .lastName("Test")
                .build();

        Contact savedContact = contactRepository.save(contact);

        ContactDetail primaryDetail = ContactDetail.builder()
                .contact(savedContact)
                .detailType(DetailType.EMAIL)
                .label(DetailLabel.WORK)
                .value("primary@test.com")
                .isPrimary(true)
                .build();

        contactDetailRepository.save(primaryDetail);

        ContactDetail secondaryDetail = ContactDetail.builder()
                .contact(savedContact)
                .detailType(DetailType.PHONE)
                .label(DetailLabel.HOME)
                .value("+1234567890")
                .isPrimary(false)
                .build();

        contactDetailRepository.save(secondaryDetail);

        List<ContactDetail> primaryDetails = contactDetailRepository
                .findPrimaryContactDetailsByContactId(savedContact.getId());

        Assertions.assertNotNull(primaryDetails);
        Assertions.assertFalse(primaryDetails.isEmpty());
        primaryDetails.forEach(detail ->
                Assertions.assertTrue(detail.getIsPrimary())
        );
    }
}