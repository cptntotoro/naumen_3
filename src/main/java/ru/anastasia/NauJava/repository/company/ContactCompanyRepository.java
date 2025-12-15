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
     * Найти текущее место работы контакта
     */
    @Query("SELECT cc FROM ContactCompany cc WHERE cc.contact.id = :contactId AND cc.isCurrent = true")
    Optional<ContactCompany> findCurrentByContactId(@Param("contactId") Long contactId);

    /**
     * Получить количество контактов в компании
     */
    Long countByCompanyId(Long companyId);

    /**
     * Удалить все связи контакта
     */
    void deleteByContactId(Long contactId);
}
