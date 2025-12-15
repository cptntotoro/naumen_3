package ru.anastasia.NauJava.service.contact.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.anastasia.NauJava.entity.contact.ContactDetail;
import ru.anastasia.NauJava.exception.contact.ContactDetailNotFoundException;
import ru.anastasia.NauJava.repository.contact.ContactDetailRepository;
import ru.anastasia.NauJava.service.contact.ContactDetailService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ContactDetailServiceImpl implements ContactDetailService {
    /**
     * Репозиторий способов связи
     */
    private final ContactDetailRepository contactDetailRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ContactDetail> findByContactId(Long contactId) {
        log.debug("Поиск способов связи для контакта с ID: {}", contactId);
        List<ContactDetail> details = contactDetailRepository.findByContactId(contactId);
        log.debug("Найдено {} способов связи для контакта с ID: {}", details.size(), contactId);
        return details;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContactDetail> findPrimaryByContactId(Long contactId) {
        log.debug("Поиск основных способов связи для контакта с ID: {}", contactId);
        List<ContactDetail> primaryDetails = contactDetailRepository.findPrimaryContactDetailsByContactId(contactId);
        log.debug("Найдено {} основных способов связи для контакта с ID: {}", primaryDetails.size(), contactId);
        return primaryDetails;
    }

    @Override
    public ContactDetail create(ContactDetail contactDetail) {
        log.info("Создание нового способа связи для контакта с ID: {}", contactDetail.getContact().getId());
        ContactDetail savedDetail = contactDetailRepository.save(contactDetail);
        log.info("Создан способ связи с ID: {} для контакта с ID: {}", savedDetail.getId(), contactDetail.getContact().getId());
        return savedDetail;
    }

    @Override
    public void delete(Long id) {
        log.info("Удаление способа связи с ID: {}", id);
        if (!contactDetailRepository.existsById(id)) {
            log.warn("Попытка удаления несуществующего способа связи с ID: {}", id);
            throw new ContactDetailNotFoundException("Не найден способ связи с id: " + id);
        }
        contactDetailRepository.deleteById(id);
        log.info("Способ связи с ID: {} успешно удален", id);
    }

    @Override
    public ContactDetail update(Long id, ContactDetail contactDetail) {
        log.info("Обновление способа связи с ID: {}", id);
        return contactDetailRepository.findById(id)
                .map(existingDetail -> {
                    log.debug("Обновление полей способа связи с ID: {}", id);
                    existingDetail.setDetailType(contactDetail.getDetailType());
                    existingDetail.setLabel(contactDetail.getLabel());
                    existingDetail.setValue(contactDetail.getValue());
                    existingDetail.setIsPrimary(contactDetail.getIsPrimary());
                    ContactDetail updatedDetail = contactDetailRepository.save(existingDetail);
                    log.info("Способ связи с ID: {} успешно обновлен", id);
                    return updatedDetail;
                })
                .orElseThrow(() -> {
                    log.error("Не найден способ связи с ID: {} для обновления", id);
                    return new ContactDetailNotFoundException("Не найден способ связи с id: " + id);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public ContactDetail findById(Long id) {
        log.debug("Поиск способа связи по ID: {}", id);
        return contactDetailRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Способ связи с ID: {} не найден", id);
                    return new ContactDetailNotFoundException("Не найден способ связи с id: " + id);
                });
    }

    @Override
    public List<ContactDetail> findAll() {
        log.debug("Получение всех способов связи");
        List<ContactDetail> allDetails = StreamSupport.stream(contactDetailRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
        log.debug("Загружено {} способов связи", allDetails.size());
        return allDetails;
    }
}