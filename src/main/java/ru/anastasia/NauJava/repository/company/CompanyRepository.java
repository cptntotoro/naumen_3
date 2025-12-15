package ru.anastasia.NauJava.repository.company;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.anastasia.NauJava.entity.company.Company;

import java.util.List;

/**
 * Репозиторий компаний
 */
@Repository
public interface CompanyRepository extends CrudRepository<Company, Long> {

    /**
     * Получить компании по части названия
     *
     * @param namePart Часть названия компании
     * @return Список компаний
     */
    List<Company> findByNameContainingIgnoreCase(String namePart);
}