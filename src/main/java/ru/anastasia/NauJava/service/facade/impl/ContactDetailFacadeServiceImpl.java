package ru.anastasia.NauJava.service.facade.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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
        log.info("Добавление {} способов связи к контакту ID: {}", details.size(), contactId);

        Contact contact = contactService.findById(contactId);
        log.debug("Контакт найден: ID: {}, имя: {}", contactId, contact.getFirstName());

        List<ContactDetail> createdDetails = new ArrayList<>();
        for (ContactDetailCreateDto detailDto : details) {
            log.trace("Создание способа связи типа: {} для контакта ID: {}",
                    detailDto.getDetailType(), contactId);

            ContactDetail detail = contactDetailMapper.contactDetailCreateDtoToContactDetail(detailDto);
            detail.setContact(contact);
            ContactDetail savedDetail = contactDetailService.create(detail);
            createdDetails.add(savedDetail);

            log.debug("Способ связи создан: ID: {}, тип: {}, значение: {}",
                    savedDetail.getId(), savedDetail.getDetailType(),
                    maskSensitiveData(savedDetail.getValue()));
        }

        log.info("Успешно добавлено {} способов связи к контакту ID: {}",
                createdDetails.size(), contactId);

        return createdDetails;
    }

    @Override
    public List<ContactDetail> getPrimaryContactDetails(Long contactId) {
        log.debug("Получение основных способов связи для контакта ID: {}", contactId);

        contactService.findById(contactId);
        List<ContactDetail> primaryDetails = contactDetailService.findPrimaryByContactId(contactId);

        log.debug("Найдено {} основных способов связи для контакта ID: {}",
                primaryDetails.size(), contactId);

        return primaryDetails;
    }

    @Override
    public List<ContactDetail> updateContactDetails(Long contactId, List<ContactDetailCreateDto> details) {
        log.info("Полное обновление способов связи для контакта ID: {}, новых способов связи: {}",
                contactId, details.size());

        Contact contact = contactService.findById(contactId);
        log.debug("Контакт найден: ID: {}, имя: {}", contactId, contact.getFirstName());

        // Получаем и удаляем существующие способы связи
        List<ContactDetail> existingDetails = contactDetailService.findByContactId(contactId);
        log.debug("Найдено {} существующих способов связи для удаления", existingDetails.size());

        int deletedCount = 0;
        for (ContactDetail existing : existingDetails) {
            try {
                contactDetailService.delete(existing.getId());
                deletedCount++;
                log.trace("Удален способ связи ID: {}", existing.getId());
            } catch (Exception e) {
                log.warn("Не удалось удалить способ связи ID: {} для контакта ID: {}. Причина: {}",
                        existing.getId(), contactId, e.getMessage());
            }
        }

        log.debug("Успешно удалено {} способов связи для контакта ID: {}", deletedCount, contactId);

        // Добавляем новые способы связи
        List<ContactDetail> updatedDetails = addDetailsToContact(contactId, details);

        log.info("Обновление способов связи завершено для контакта ID: {}. Удалено: {}, добавлено: {}",
                contactId, deletedCount, updatedDetails.size());

        return updatedDetails;
    }

    /**
     * Маскирует чувствительные данные в логах
     */
    private String maskSensitiveData(String value) {
        if (value == null || value.length() <= 4) {
            return "***";
        }
        // Оставляем только первые 2 и последние 2 символа, остальное маскируем
        return value.substring(0, 2) + "***" + value.substring(value.length() - 2);
    }
}