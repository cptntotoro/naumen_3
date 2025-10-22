package ru.anastasia.NauJava.repository.company;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.anastasia.NauJava.entity.company.Company;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий компаний
 */
@Repository
public interface CompanyRepository extends CrudRepository<Company, Long> {
    /**
     * Получить компанию по названию
     *
     * @param name Название компании
     * @return Компания
     */
    Optional<Company> findByName(String name);

    /**
     * Получить компании по части названия
     *
     * @param namePart Часть названия компании
     * @return Список компаний
     */
    List<Company> findByNameContainingIgnoreCase(String namePart);
}