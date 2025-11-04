package ru.anastasia.NauJava.service.facade;

import ru.anastasia.NauJava.dto.contact.ContactDetailCreateDto;
import ru.anastasia.NauJava.entity.contact.ContactDetail;

import java.util.List;

/**
 * Фасад для операций с контактами и способами связи
 */
public interface ContactDetailFacadeService {

    /**
     * Добавить способы связи к контакту
     *
     * @param contactId Идентификатор контакта
     * @param details   Список DTO создания способов связи
     * @return Список способов связи
     */
    List<ContactDetail> addDetailsToContact(Long contactId, List<ContactDetailCreateDto> details);

    /**
     * Получить основные способы связи контакта
     *
     * @param contactId Идентификатор контакта
     * @return Список способов связи
     */
    List<ContactDetail> getPrimaryContactDetails(Long contactId);

    /**
     * Обновить способы связи контакта
     *
     * @param contactId Идентификатор контакта
     * @param details   Список DTO создания способов связи
     * @return Список способов связи
     */
    List<ContactDetail> updateContactDetails(Long contactId, List<ContactDetailCreateDto> details);

}
