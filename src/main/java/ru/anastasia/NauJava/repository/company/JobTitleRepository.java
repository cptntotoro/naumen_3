package ru.anastasia.NauJava.repository.company;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.anastasia.NauJava.entity.company.JobTitle;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий должностей
 */
@Repository
public interface JobTitleRepository extends CrudRepository<JobTitle, Long> {
    /**
     * Получить должность по названию
     *
     * @param title Название должности
     * @return Должность
     */
    Optional<JobTitle> findByTitle(String title);

    /**
     * Получить должности по названию
     *
     * @param titlePart Фрагмент названия должности
     * @return Список должностей
     */
    List<JobTitle> findByTitleContainingIgnoreCase(String titlePart);
}
