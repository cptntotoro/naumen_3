package ru.anastasia.NauJava.service.company.impl;

import lombok.RequiredArgsConstructor;
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
        try {
            JobTitle jobTitle = JobTitle.builder()
                    .title(title)
                    .build();
            return jobTitleRepository.save(jobTitle);
        } catch (DataIntegrityViolationException e) {
            JobTitle jobTitle = findByName(title);
            if (jobTitle != null) {
                return jobTitle;
            }
            throw new IllegalJobTitleStateException("Не удалось создать название должности: " + title + ". " +
                    "Название должности с таким именем уже существует");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public JobTitle findByName(String title) {
        return jobTitleRepository.findByTitle(title).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobTitle> findAll() {
        return StreamSupport.stream(jobTitleRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public JobTitle update(JobTitle jobTitle) {
        Long id = jobTitle.getId();

        return jobTitleRepository.findById(id)
                .map(jt -> {
                    jt.setTitle(jobTitle.getTitle());
                    return jobTitleRepository.save(jt);
                })
                .orElseThrow(() -> new JobTitleNotFoundException("Не найдена должность с id: " + id));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        jobTitleRepository.deleteById(id);
    }

    @Override
    public JobTitle findById(Long id) {
        return jobTitleRepository.findById(id)
                .orElseThrow(() -> new JobTitleNotFoundException("Не найдена должность с id: " + id));
    }
}
