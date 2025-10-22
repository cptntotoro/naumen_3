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
        Contact contact = new Contact();
        contact.setFirstName("Detail");
        contact.setLastName("Test");
        Contact savedContact = contactRepository.save(contact);

        ContactDetail primaryEmail = new ContactDetail();
        primaryEmail.setContact(savedContact);
        primaryEmail.setDetailType(DetailType.EMAIL);
        primaryEmail.setLabel(DetailLabel.WORK);
        primaryEmail.setValue("primary@test.com");
        primaryEmail.setIsPrimary(true);
        contactDetailRepository.save(primaryEmail);

        ContactDetail secondaryEmail = new ContactDetail();
        secondaryEmail.setContact(savedContact);
        secondaryEmail.setDetailType(DetailType.EMAIL);
        secondaryEmail.setLabel(DetailLabel.HOME);
        secondaryEmail.setValue("secondary@test.com");
        secondaryEmail.setIsPrimary(false);
        contactDetailRepository.save(secondaryEmail);

        ContactDetail mobilePhone = new ContactDetail();
        mobilePhone.setContact(savedContact);
        mobilePhone.setDetailType(DetailType.PHONE);
        mobilePhone.setLabel(DetailLabel.MOBILE);
        mobilePhone.setValue("+1234567890");
        mobilePhone.setIsPrimary(false);
        contactDetailRepository.save(mobilePhone);

        List<ContactDetail> foundDetails = contactDetailRepository
                .findByDetailTypeAndIsPrimaryTrueOrLabel(DetailType.EMAIL, DetailLabel.MOBILE);

        Assertions.assertNotNull(foundDetails);
        Assertions.assertFalse(foundDetails.isEmpty());
    }

    @Test
    void testFindPrimaryContactDetailsByContactId() {
        Contact contact = new Contact();
        contact.setFirstName("Primary");
        contact.setLastName("Test");
        Contact savedContact = contactRepository.save(contact);

        ContactDetail primaryDetail = new ContactDetail();
        primaryDetail.setContact(savedContact);
        primaryDetail.setDetailType(DetailType.EMAIL);
        primaryDetail.setLabel(DetailLabel.WORK);
        primaryDetail.setValue("primary@test.com");
        primaryDetail.setIsPrimary(true);
        contactDetailRepository.save(primaryDetail);

        ContactDetail secondaryDetail = new ContactDetail();
        secondaryDetail.setContact(savedContact);
        secondaryDetail.setDetailType(DetailType.PHONE);
        secondaryDetail.setLabel(DetailLabel.HOME);
        secondaryDetail.setValue("+1234567890");
        secondaryDetail.setIsPrimary(false);
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