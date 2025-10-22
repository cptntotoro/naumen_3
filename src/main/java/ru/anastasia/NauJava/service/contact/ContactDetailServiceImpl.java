package ru.anastasia.NauJava.service.contact;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.anastasia.NauJava.entity.contact.ContactDetail;
import ru.anastasia.NauJava.entity.enums.DetailLabel;
import ru.anastasia.NauJava.entity.enums.DetailType;
import ru.anastasia.NauJava.repository.contact.ContactDetailRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ContactDetailServiceImpl implements ContactDetailService {
    /**
     * Репозиторий способов связи
     */
    private final ContactDetailRepository contactDetailRepository;

    @Autowired
    public ContactDetailServiceImpl(ContactDetailRepository contactDetailRepository) {
        this.contactDetailRepository = contactDetailRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContactDetail> findByContactId(Long contactId) {
        return contactDetailRepository.findByContactId(contactId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContactDetail> findByDetailType(DetailType detailType) {
        return contactDetailRepository.findByDetailTypeAndValueContainingIgnoreCase(detailType, "");
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContactDetail> findPrimaryByContactId(Long contactId) {
        return contactDetailRepository.findPrimaryContactDetailsByContactId(contactId);
    }

    @Override
    public ContactDetail create(ContactDetail contactDetail) {
        return contactDetailRepository.save(contactDetail);
    }

    @Override
    public void delete(Long id) {
        contactDetailRepository.deleteById(id);
    }

    @Override
    public ContactDetail update(Long id, ContactDetail contactDetail) {
        return contactDetailRepository.findById(id)
                .map(existingDetail -> {
                    existingDetail.setDetailType(contactDetail.getDetailType());
                    existingDetail.setLabel(contactDetail.getLabel());
                    existingDetail.setValue(contactDetail.getValue());
                    existingDetail.setIsPrimary(contactDetail.getIsPrimary());
                    return contactDetailRepository.save(existingDetail);
                })
                .orElseThrow(() -> new RuntimeException("Не найден способ связи с id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ContactDetail> findById(Long id) {
        return contactDetailRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContactDetail> findByDetailTypeAndLabel(DetailType detailType, String label) {
        try {
            DetailLabel detailLabel = DetailLabel.valueOf(label.toUpperCase());
            return contactDetailRepository.findByDetailTypeAndIsPrimaryTrueOrLabel(detailType, detailLabel);
        } catch (IllegalArgumentException e) {
            return List.of();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContactDetail> findByDetailTypeAndLabel(DetailType detailType, DetailLabel label) {
        return contactDetailRepository.findByDetailTypeAndIsPrimaryTrueOrLabel(detailType, label);
    }
}