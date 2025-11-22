package ru.anastasia.NauJava.service.contact;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.anastasia.NauJava.entity.contact.Contact;

import java.util.List;

/**
 * Сервис контактов
 */
public interface ContactService {

    /**
     * Добавить контакт
     *
     * @param firstName Имя
     * @param lastName  Фамилия
     * @return Контакт
     */
    Contact add(String firstName, String lastName);

    /**
     * Найти контакт по идентификатору
     *
     * @param id Идентификатор
     * @return Контакт
     */
    Contact findById(Long id);

    /**
     * Удаить контакт по идентификатору
     *
     * @param id Идентификатор
     */
    void deleteById(Long id);

    /**
     * Обновить контакт
     *
     * @param id        Идентификатор
     * @param firstName Имя
     * @param lastName  Фамилия
     * @return Контакт
     */
    Contact update(Long id, String firstName, String lastName);

    /**
     * Обновить контакт
     *
     * @param id          Идентификатор контакта
     * @param firstName   Имя
     * @param lastName    Фамилия
     * @param displayName Отображаемое имя
     * @param avatarUrl   URL аватара
     * @param isFavorite  Флаг избранного
     * @return Обновленный контакт
     */
    Contact update(Long id, String firstName, String lastName, String displayName,
                   String avatarUrl, Boolean isFavorite);

    /**
     * Получить все контакты
     *
     * @return Список контактов
     */
    List<Contact> findAll();

    /**
     * Найти контакты по имени
     *
     * @param name Имя
     * @return Список контактов
     */
    List<Contact> findByName(String name);

    /**
     * Добавить контакт в избранное
     *
     * @param contactId Идентификатор контакта
     */
    void addToFavorites(Long contactId);

    /**
     * Удалить контакт из избранного
     *
     * @param contactId Идентификатор контакта
     */
    void removeFromFavorites(Long contactId);

    /**
     * Получить избранные контакты
     *
     * @return Список контактов
     */
    List<Contact> findFavorites();

    /**
     * Получить контакты по имени и фамилии
     *
     * @param firstName Имя
     * @param lastName  Фамилия
     * @return Список контактов
     */
    List<Contact> findAllByFullName(String firstName, String lastName);

    /**
     * Получить контакты по тегу
     *
     * @param tagName Название тега
     * @return Список контактов
     */
    List<Contact> findByTag(String tagName);

    /**
     * Обновить аватар контакта
     *
     * @param contactId Идентификатор контакта
     * @param avatarUrl URL картинки
     * @return Контакт
     */
    Contact updateAvatar(Long contactId, String avatarUrl);

    /**
     * Получить контакты с днями рождения в этом месяце
     *
     * @return Список контактов
     */
    List<Contact> findBirthdaysThisMonth();

    /**
     * Поиск контактов по строке поиска
     *
     * @param searchTerm Строка поиска (имя или фамилия)
     * @return Список контактов
     */
    List<Contact> search(String searchTerm);

    /**
     * Сохранить контакт
     *
     * @param contact Контакт
     * @return Контакт
     */
    Contact save(Contact contact);

    /**
     * Получить число всех контактов
     *
     * @return Количество контактов
     */
    Long countTotal();

    /**
     * Получить число избранных контактов
     *
     * @return Количество избранных контактов
     */
    Long countFavorites();

    /**
     * Получить страницу контактов по имени, фамилии, псевдониму с опциональными фильтрами по компании и тегу
     *
     * @param searchTerm  Поисковый запрос
     * @param companyName Название компании
     * @param tagName     Название тега
     * @param pageable    Страница
     * @return Страница контактов
     */
    Page<Contact> searchContacts(String searchTerm, String companyName, String tagName, Pageable pageable);

    /**
     * Получить страницу контактов
     *
     * @param pageable Страница
     * @return Страница контактов
     */
    Page<Contact> findAll(Pageable pageable);

    /**
     * Получить страницу избранных контактов
     *
     * @param pageable Страница
     * @return Страница избранных контактов
     */
    Page<Contact> findFavorites(Pageable pageable);

    /**
     * Получить список контактов с грядущими днями рождения
     *
     * @param daysAhead Число дней, включая сегодняшнюю дату
     * @return Список контактов
     */
    List<Contact> findWithUpcomingBirthdays(int daysAhead);
}