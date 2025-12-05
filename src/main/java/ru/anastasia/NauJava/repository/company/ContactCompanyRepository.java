package ru.anastasia.NauJava.repository.company;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.anastasia.NauJava.entity.company.ContactCompany;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий компаний контакта
 */
@Repository
public interface ContactCompanyRepository extends CrudRepository<ContactCompany, Long> {

    /**
     * Найти все компании контакта
     */
    List<ContactCompany> findByContactId(Long contactId);

    /**
     * Найти связь контакт-компания по ID контакта и компании
     */
    Optional<ContactCompany> findByContactIdAndCompanyId(Long contactId, Long companyId);

    /**
     * Проверить, существует ли связь
     */
    boolean existsByContactIdAndCompanyId(Long contactId, Long companyId);

    /**
     * Найти текущее место работы контакта
     */
    @Query("SELECT cc FROM ContactCompany cc WHERE cc.contact.id = :contactId AND cc.isCurrent = true")
    Optional<ContactCompany> findCurrentByContactId(@Param("contactId") Long contactId);

    /**
     * Подсчитать количество контактов в компании
     */
    Long countByCompanyId(Long companyId);

    /**
     * Найти все связи по ID компании
     */
    List<ContactCompany> findByCompanyId(Long companyId);

    /**
     * Удалить все связи контакта
     */
    void deleteByContactId(Long contactId);
}
