package ru.anastasia.NauJava.service.contact;

import ru.anastasia.NauJava.entity.contact.ContactDetail;
import ru.anastasia.NauJava.entity.enums.DetailLabel;
import ru.anastasia.NauJava.entity.enums.DetailType;

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
     * Получить способы связи по типу
     *
     * @param detailType Тип способа связи
     * @return Список способов связи
     */
    List<ContactDetail> findByDetailType(DetailType detailType);

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
     * Получить способы связи по типу и строковому значению лейбла
     *
     * @param detailType Тип способа связи
     * @param label      Строковое значение лейбла
     * @return Список способов связи
     */
    List<ContactDetail> findByDetailTypeAndLabel(DetailType detailType, String label);

    /**
     * Получить способы связи по типу и лейблу
     *
     * @param detailType Тип способа связи
     * @param label      Лейбл способа связи
     * @return Список способов связи
     */
    List<ContactDetail> findByDetailTypeAndLabel(DetailType detailType, DetailLabel label);

    /**
     * Получить все способы связи
     *
     * @return Список способов связи
     */
    List<ContactDetail> findAll();
}