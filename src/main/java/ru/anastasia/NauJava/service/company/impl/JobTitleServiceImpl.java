package ru.anastasia.NauJava.service.company.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.anastasia.NauJava.entity.company.JobTitle;
import ru.anastasia.NauJava.repository.company.JobTitleRepository;
import ru.anastasia.NauJava.service.company.JobTitleService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class JobTitleServiceImpl implements JobTitleService {
    /**
     * Репозиторий должностей
     */
    private final JobTitleRepository jobTitleRepository;

    @Autowired
    public JobTitleServiceImpl(JobTitleRepository jobTitleRepository) {
        this.jobTitleRepository = jobTitleRepository;
    }

    @Override
    @Transactional
    public JobTitle create(String title) {
        JobTitle existingJobTitle = findByName(title);
        if (existingJobTitle != null) {
            return existingJobTitle;
        }

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
            throw new RuntimeException("Не удалось создать название должности: " + title);
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
    public JobTitle update(Long id, String title) {
        return jobTitleRepository.findById(id)
                .map(jobTitle -> {
                    jobTitle.setTitle(title);
                    return jobTitleRepository.save(jobTitle);
                })
                .orElseThrow(() -> new RuntimeException("Не найдена должность с id: " + id));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        jobTitleRepository.deleteById(id);
    }
}
