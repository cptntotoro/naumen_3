package ru.anastasia.NauJava.service.company;

import ru.anastasia.NauJava.dto.company.ContactCompanyCreateDto;
import ru.anastasia.NauJava.dto.company.ContactCompanyUpdateDto;
import ru.anastasia.NauJava.entity.company.ContactCompany;

import java.util.List;

/**
 * Сервис компаний контактов
 */
public interface ContactCompanyService {

    /**
     * Добавить компанию и должность контакту
     *
     * @param contactId Идентификатор контактв
     * @param companyCreateDto DTO создания компании контакта
     * @return Компания контакта
     */
    ContactCompany create(ContactCompanyCreateDto companyCreateDto, Long contactId);

    /**
     * Получить компанию и должность контакта по идентификатору
     *
     * @param id Идентификатор записи {@link ContactCompany}
     * @return Компания контакта
     */
    ContactCompany findById(Long id);

    /**
     * Получить список компаний контакта по идентификатору контакта
     *
     * @param contactId Идентификатор контакта
     * @return Список компаний контакта
     */
    List<ContactCompany> findByContactId(Long contactId);

    /**
     * Обновить компанию контакта
     *
     * <p>
     * Если в {@code updateDto} передан {@code isCurrent = true},
     * автоматически сбрасывается флаг текущего места работы у всех остальных записей данного контакта.
     * </p>
     *
     * @param id Идентификатор записи {@link ContactCompany}
     * @param updateDto DTO обновления компании контакта
     * @return Компания контакта
     */
    ContactCompany update(Long id, ContactCompanyUpdateDto updateDto);

    /**
     * Удалить связь контакт-компания по идентификатору записи
     *
     * @param id Идентификатор записи {@link ContactCompany}
     */
    void delete(Long id);

    /**
     * Получить текущее место работы контакта
     *
     * @param contactId Идентификатор контакта
     * @return Компания контакта
     */
    ContactCompany findCurrentByContactId(Long contactId);

    /**
     * Получить число контактов в компании
     *
     * @param companyId Идентификатор компании
     * @return Число контактов в компании
     */
    Long countContactsInCompany(Long companyId);

    /**
     * Удалить все компании контакта
     *
     * @param contactId Идентификатор компании
     */
    void deleteByContactId(Long contactId);
}
