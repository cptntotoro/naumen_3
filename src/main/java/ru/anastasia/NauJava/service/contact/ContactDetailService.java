package ru.anastasia.NauJava.service.contact;

import ru.anastasia.NauJava.entity.contact.ContactDetail;

import java.util.List;

/**
 * Сервис способов связи
 */
public interface ContactDetailService {

    /**
     * Получить способы связи по идентификатору контакта
     *
     * @param contactId Идентификатор контакта
     * @return Список способов связи
     */
    List<ContactDetail> findByContactId(Long contactId);

    /**
     * Получить основные способы связи по идентификатору контакта
     *
     * @param contactId Идентификатор контакта
     * @return Список способов связи
     */
    List<ContactDetail> findPrimaryByContactId(Long contactId);

    /**
     * Создать способ связи
     *
     * @param contactDetail Способ связи
     * @return Созданный способ связи
     */
    ContactDetail create(ContactDetail contactDetail);

    /**
     * Удалить способ связи
     *
     * @param id Идентификатор способа связи
     */
    void delete(Long id);

    /**
     * Обновить способ связи
     *
     * @param id            Идентификатор способа связи
     * @param contactDetail Обновленные данные способа связи
     * @return Обновленный способ связи
     */
    ContactDetail update(Long id, ContactDetail contactDetail);

    /**
     * Найти способ связи по идентификатору
     *
     * @param id Идентификатор
     * @return Способ связи
     */
    ContactDetail findById(Long id);

    /**
     * Получить все способы связи
     *
     * @return Список способов связи
     */
    List<ContactDetail> findAll();
}