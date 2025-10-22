package ru.anastasia.NauJava.service.contact;

import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.entity.contact.ContactDetail;
import ru.anastasia.NauJava.entity.contact.Event;
import ru.anastasia.NauJava.entity.contact.SocialProfile;

import java.util.List;

/**
 * Сервис управления контактами
 */
public interface ContactManagementService {

    /**
     * Создать контакт
     *
     * @param firstName      Имя
     * @param lastName       Фамилия
     * @param company        Название компании
     * @param jobTitle       Название должности
     * @param contactDetails Способы связи
     * @param socialProfiles Профили в соцсетях
     * @param events         События
     * @param tagNames       Теги
     * @param notes          Заметки
     * @return Контакт
     */
    Contact createWithDetails(
            String firstName, String lastName, String company, String jobTitle,
            List<ContactDetail> contactDetails, List<SocialProfile> socialProfiles,
            List<Event> events, List<String> tagNames, List<String> notes);

    /**
     * Удалить контакт
     *
     * @param contactId Идентификатор контакта
     */
    void delete(Long contactId);

    /**
     * Создать дубликат контакта
     *
     * @param contactId    Идентификатор контакта
     * @param newFirstName Новое значение имени
     * @param newLastName  Новое значение фамилии
     * @return Контакт
     */
    Contact duplicate(Long contactId, String newFirstName, String newLastName);

    /**
     * Получить контакты по параметрам
     *
     * @param firstName Имя
     * @param lastName  Фамилия
     * @param company   Название компании
     * @param jobTitle  Название должности
     * @return Список контактов
     */
    List<Contact> searchComplex(String firstName, String lastName, String company, String jobTitle);

    /**
     * Получить контакты с наступающими днями рождения
     *
     * @param daysAhead Дней до дня рождения
     * @return Список контактов
     */
    List<Contact> findWithUpcomingBirthdays(int daysAhead);

    /**
     * Обновить контакт и способы связи
     *
     * @param contactId      Идентификатор контакта
     * @param contact        Обновленные данные контакта
     * @param contactDetails Обновленные способы связи
     * @return Контакт
     */
    Contact updateWithDetails(Long contactId, Contact contact, List<ContactDetail> contactDetails);
}
