package ru.anastasia.NauJava.service.contact;

import ru.anastasia.NauJava.dto.contact.ContactCreateDto;
import ru.anastasia.NauJava.dto.contact.ContactUpdateDto;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.service.facade.dto.ContactFullDetails;

import java.util.List;

/**
 * Сервис управления контактами
 */
public interface ContactManagementService {

    /**
     * Удалить контакт
     *
     * @param contactId Идентификатор контакта
     */
    void delete(Long contactId);

    /**
     * Получить контакт со всеми связанными сущностями
     *
     * @param contactId Идентификатор контакта
     * @return Контакт со всеми связанными сущностями
     */
    ContactFullDetails getWithAllDetails(Long contactId);

    /**
     * Получить контакт с основной информацией
     *
     * @param contactId Идентификатор контакта
     * @return Контакт с основной информацией
     */
    ContactFullDetails getSummary(Long contactId);

    /**
     * Дублировать контакт
     *
     * @param contactId    Идентификатор контакта
     * @param newFirstName Новое имя
     * @param newLastName  Новая фамилия
     * @return Дубликат контакта
     */
    Contact duplicate(Long contactId, String newFirstName, String newLastName);

    /**
     * Получить контакты с предстоящими днями рождения
     *
     * @param daysAhead Количество дней вперед для поиска
     * @return Список контактов с предстоящими днями рождения
     */
    List<ContactFullDetails> getListWithUpcomingBirthdays(int daysAhead);

    /**
     * Получить избранные контакты с основной информацией
     *
     * @return Список избранных контактов
     */
    List<ContactFullDetails> getListFavoriteWithDetails();

    /**
     * Создать контакт
     *
     * @param contactCreateDto DTO создания контакта
     * @return Контакт
     */
    Contact create(ContactCreateDto contactCreateDto);

    /**
     * Обновить контакт
     *
     * @param contactUpdateDto DTO обновления контакта
     * @return Контакт
     */
    Contact update(ContactUpdateDto contactUpdateDto);
}
