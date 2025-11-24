package ru.anastasia.NauJava.service.company.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.anastasia.NauJava.entity.company.JobTitle;
import ru.anastasia.NauJava.exception.company.IllegalJobTitleStateException;
import ru.anastasia.NauJava.exception.company.JobTitleNotFoundException;
import ru.anastasia.NauJava.repository.company.JobTitleRepository;
import ru.anastasia.NauJava.service.company.JobTitleService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobTitleServiceImpl implements JobTitleService {
    /**
     * Репозиторий должностей
     */
    private final JobTitleRepository jobTitleRepository;

    @Override
    @Transactional
    public JobTitle create(String title) {
        log.info("Создание должности [название: {}]", title);

        try {
            JobTitle jobTitle = JobTitle.builder()
                    .title(title)
                    .build();
            JobTitle savedJobTitle = jobTitleRepository.save(jobTitle);
            log.info("Должность успешно создана [ID: {}, название: {}]",
                    savedJobTitle.getId(), savedJobTitle.getTitle());
            return savedJobTitle;
        } catch (DataIntegrityViolationException e) {
            log.warn("Конфликт при создании должности [название: {}], поиск существующей", title);
            JobTitle jobTitle = findByName(title);
            if (jobTitle != null) {
                log.info("Найдена существующая должность [ID: {}, название: {}]", jobTitle.getId(), jobTitle.getTitle());
                return jobTitle;
            }
            log.error("Ошибка целостности при создании должности [название: {}]", title, e);
            throw new IllegalJobTitleStateException("Не удалось создать название должности: " + title + ". " +
                    "Название должности с таким именем уже существует");
        } catch (Exception e) {
            log.error("Неожиданная ошибка при создании должности [название: {}]", title, e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public JobTitle findByName(String title) {
        log.debug("Поиск должности по названию: {}", title);

        return jobTitleRepository.findByTitle(title).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobTitle> findAll() {
        log.debug("Получение списка всех должностей");

        List<JobTitle> jobTitles = StreamSupport.stream(jobTitleRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
        log.debug("Найдено должностей: {}", jobTitles.size());
        return jobTitles;
    }

    @Override
    @Transactional
    public JobTitle update(JobTitle jobTitle) {
        log.info("Обновление должности [ID: {}, название: {}]", jobTitle.getId(), jobTitle.getTitle());

        Long id = jobTitle.getId();

        return jobTitleRepository.findById(id)
                .map(jt -> {
                    jt.setTitle(jobTitle.getTitle());
                    JobTitle updatedJobTitle = jobTitleRepository.save(jt);
                    log.info("Должность успешно обновлена [ID: {}, название: {}]",
                            updatedJobTitle.getId(), updatedJobTitle.getTitle());
                    return updatedJobTitle;
                })
                .orElseThrow(() -> {
                    log.error("Должность не найдена при обновлении [ID: {}]", id);
                    return new JobTitleNotFoundException("Не найдена должность с id: " + id);
                });
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Удаление должности [ID: {}]", id);

        try {
            jobTitleRepository.deleteById(id);
            log.info("Должность успешно удалена [ID: {}]", id);
        } catch (Exception e) {
            log.error("Ошибка при удалении должности [ID: {}]", id, e);
            throw e;
        }
    }

    @Override
    public JobTitle findById(Long id) {
        log.debug("Поиск должности по ID: {}", id);

        return jobTitleRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Должность не найдена [ID: {}]", id);
                    return new JobTitleNotFoundException("Не найдена должность с id: " + id);
                });
    }
}
