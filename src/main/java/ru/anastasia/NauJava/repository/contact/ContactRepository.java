package ru.anastasia.NauJava.repository.contact;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.repository.contact.custom.ContactRepositoryCustom;

import java.util.List;

/**
 * Репозиторий контактов
 */
@Repository
public interface ContactRepository extends JpaRepository<Contact, Long>, ContactRepositoryCustom {

    /**
     * Получить контакты по имени или фамилии (без учёта регистра)
     *
     * @param firstName Имя
     * @param lastName  Фамилия
     * @return Список контактов
     */
    List<Contact> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);

    /**
     * Получить избранные контакты
     *
     * @return Список контактов
     */
    List<Contact> findByIsFavoriteTrue();

    /**
     * Получить контакты по названию тега
     *
     * @param tagName Название тега
     * @return Список контактов
     */
    @Query("SELECT c FROM Contact c JOIN c.contactTags ct JOIN ct.tag t WHERE t.name = :tagName")
    List<Contact> findContactsByTagName(@Param("tagName") String tagName);

    /**
     * Получить контакты по имени и фамилии
     *
     * @param firstName Имя
     * @param lastName  Фамилия
     * @return Список контактов
     */
    List<Contact> findByFirstNameAndLastName(String firstName, String lastName);

    /**
     * Получить коллег по названию компании
     *
     * @param companyName Название компании
     * @return Список контактов
     */
    @Query("SELECT cc.contact FROM ContactCompany cc WHERE cc.company.name = :companyName")
    List<Contact> findColleaguesByCompany(@Param("companyName") String companyName);

    /**
     * Получить коллег по названию компании и должности
     *
     * @param companyName Название компании
     * @param jobTitle    Название должности
     * @return Список контактов
     */
    @Query("SELECT cc.contact FROM ContactCompany cc WHERE cc.company.name = :companyName AND cc.jobTitle.title = :jobTitle")
    List<Contact> findColleaguesByCompanyAndJobTitle(@Param("companyName") String companyName,
                                                     @Param("jobTitle") String jobTitle);

    /**
     * Получить контакты по должности
     *
     * @param jobTitle Название должности
     * @return Список контактов
     */
    @Query("SELECT cc.contact FROM ContactCompany cc WHERE cc.jobTitle.title = :jobTitle")
    List<Contact> findByJobTitle(@Param("jobTitle") String jobTitle);

    /**
     * Получить контакты по имени, фамилии или отображаемому имени (без учёта регистра)
     *
     * @param firstName   Имя
     * @param lastName    Фамилия
     * @param displayName Отображаемое имя
     * @return Список контактов
     */
    List<Contact> findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCaseOrDisplayNameContainingIgnoreCase(
            String firstName, String lastName, String displayName);

    /**
     * Получить контакты по названию компании
     *
     * @param companyName Название компании
     * @return Список контактов
     */
    @Query("SELECT c FROM Contact c JOIN c.companies cc JOIN cc.company comp WHERE comp.name = :companyName")
    List<Contact> findByCompanyName(@Param("companyName") String companyName);

    /**
     * Получить количество избранных контактов
     *
     * @return Количество избранных контактов
     */
    Long countByIsFavoriteTrue();

    /**
     * Получить страницу контактов по имени, фамилии, псевдониму с фильтрами по компании и тегу
     *
     * @param searchTerm  Поисковый запрос
     * @param companyName Название компании
     * @param tagName     Название тега
     * @param pageable    Страница
     * @return Страница контактов
     */
    @Query("SELECT DISTINCT c FROM Contact c " +
            "LEFT JOIN c.companies cc " +
            "LEFT JOIN cc.company comp " +
            "LEFT JOIN c.contactTags ct " +
            "LEFT JOIN ct.tag t " +
            "WHERE (" +
            "  (:searchTerm IS NULL OR " +
            "   c.firstName LIKE '%' || CAST(:searchTerm AS text) || '%' OR " +
            "   c.lastName LIKE '%' || CAST(:searchTerm AS text) || '%' OR " +
            "   (c.displayName IS NOT NULL AND c.displayName LIKE '%' || CAST(:searchTerm AS text) || '%'))" +
            ") AND " +
            "(:companyName IS NULL OR comp.name LIKE '%' || CAST(:companyName AS text) || '%') AND " +
            "(:tagName IS NULL OR t.name LIKE '%' || CAST(:tagName AS text) || '%')")
    Page<Contact> searchWithFilters(
            @Param("searchTerm") String searchTerm,
            @Param("companyName") String companyName,
            @Param("tagName") String tagName,
            Pageable pageable);

    /**
     * Получить страницу избранных контактов
     *
     * @param pageable Страница
     * @return Страница избранных контактов
     */
    Page<Contact> findByIsFavoriteTrue(Pageable pageable);

    /**
     * Получить контакты, у которых день рождения попадает в указанный диапазон дат
     * <p>
     * Метод ищет дни рождения, у которых месяц и день находятся между заданными границами.
     * Например, для поиска дней рождения с 25 декабря по 15 января:
     * - startMonth = 12 (декабрь), endMonth = 1 (январь)
     * - startDay = 25, endDay = 15
     *
     * @param startMonth начальный месяц диапазона (1-12)
     * @param endMonth   конечный месяц диапазона (1-12)
     * @param startDay   начальный день диапазона (1-31)
     * @param endDay     конечный день диапазона (1-31)
     * @return Список контактов
     *
     * @example - Найти дни рождения с 15 декабря по 10 января
     * findContactsWithUpcomingBirthdays(12, 1, 15, 10)
     * <p>
     * - Найти дни рождения с 1 по 31 марта
     * findContactsWithUpcomingBirthdays(3, 3, 1, 31)
     */
    @Query(value = "SELECT DISTINCT c.* FROM contacts c " +
            "JOIN events e ON c.id = e.contact_id " +
            "WHERE e.event_type = 'BIRTHDAY' " +
            "AND EXTRACT(MONTH FROM e.event_date) BETWEEN :startMonth AND :endMonth " +
            "AND EXTRACT(DAY FROM e.event_date) BETWEEN :startDay AND :endDay",
            nativeQuery = true)
    List<Contact> findContactsWithUpcomingBirthdays(
            @Param("startMonth") int startMonth,
            @Param("endMonth") int endMonth,
            @Param("startDay") int startDay,
            @Param("endDay") int endDay);
}