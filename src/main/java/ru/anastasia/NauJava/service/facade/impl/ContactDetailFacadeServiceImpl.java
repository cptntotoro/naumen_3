package ru.anastasia.NauJava.service.facade.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.anastasia.NauJava.dto.contact.ContactDetailCreateDto;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.entity.contact.ContactDetail;
import ru.anastasia.NauJava.mapper.contact.ContactDetailMapper;
import ru.anastasia.NauJava.service.contact.ContactDetailService;
import ru.anastasia.NauJava.service.contact.ContactService;
import ru.anastasia.NauJava.service.facade.ContactDetailFacadeService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ContactDetailFacadeServiceImpl implements ContactDetailFacadeService {
    /**
     * Сервис контактов
     */
    private final ContactService contactService;

    /**
     * Сервис способов связи
     */
    private final ContactDetailService contactDetailService;

    /**
     * Маппер контактов
     */
    private final ContactDetailMapper contactDetailMapper;

    @Override
    public List<ContactDetail> addDetailsToContact(Long contactId, List<ContactDetailCreateDto> details) {
        Contact contact = contactService.findById(contactId);

        List<ContactDetail> createdDetails = new ArrayList<>();
        for (ContactDetailCreateDto detailDto : details) {
            ContactDetail detail = contactDetailMapper.contactDetailCreateDtoToContactDetail(detailDto);
            detail.setContact(contact);
            ContactDetail savedDetail = contactDetailService.create(detail);
            createdDetails.add(savedDetail);
        }

        return createdDetails;
    }

    @Override
    public List<ContactDetail> getPrimaryContactDetails(Long contactId) {
        contactService.findById(contactId);
        return contactDetailService.findPrimaryByContactId(contactId);
    }

    @Override
    public List<ContactDetail> updateContactDetails(Long contactId, List<ContactDetailCreateDto> details) {
        contactService.findById(contactId);

        List<ContactDetail> existingDetails = contactDetailService.findByContactId(contactId);
        for (ContactDetail existing : existingDetails) {
            contactDetailService.delete(existing.getId());
        }

        return addDetailsToContact(contactId, details);
    }
}
