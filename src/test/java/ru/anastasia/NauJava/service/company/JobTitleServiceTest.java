package ru.anastasia.NauJava.service.company;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.anastasia.NauJava.entity.company.JobTitle;
import ru.anastasia.NauJava.exception.company.IllegalJobTitleStateException;
import ru.anastasia.NauJava.exception.company.JobTitleNotFoundException;
import ru.anastasia.NauJava.repository.company.JobTitleRepository;
import ru.anastasia.NauJava.service.company.impl.JobTitleServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobTitleServiceTest {

    @Mock
    private JobTitleRepository jobTitleRepository;

    @InjectMocks
    private JobTitleServiceImpl jobTitleService;

    @Test
    void create_ShouldReturnJobTitle_WhenSuccessful() {
        String title = "Software Engineer";
        JobTitle jobTitle = JobTitle.builder()
                .id(1L)
                .title(title)
                .build();

        when(jobTitleRepository.save(any(JobTitle.class))).thenReturn(jobTitle);

        JobTitle result = jobTitleService.create(title);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(title, result.getTitle());
        verify(jobTitleRepository).save(any(JobTitle.class));
    }

    @Test
    void create_ShouldReturnExistingJobTitle_WhenTitleAlreadyExists() {
        String title = "Existing Title";
        JobTitle existingJobTitle = JobTitle.builder()
                .id(1L)
                .title(title)
                .build();

        when(jobTitleRepository.save(any(JobTitle.class)))
                .thenThrow(DataIntegrityViolationException.class);
        when(jobTitleRepository.findByTitle(title)).thenReturn(Optional.of(existingJobTitle));

        JobTitle result = jobTitleService.create(title);

        assertNotNull(result);
        assertEquals(existingJobTitle.getId(), result.getId());
        assertEquals(existingJobTitle.getTitle(), result.getTitle());
        verify(jobTitleRepository).save(any(JobTitle.class));
        verify(jobTitleRepository).findByTitle(title);
    }

    @Test
    void create_ShouldThrowIllegalJobTitleStateException_WhenTitleAlreadyExistsAndCannotBeFound() {
        String title = "Problematic Title";

        when(jobTitleRepository.save(any(JobTitle.class)))
                .thenThrow(DataIntegrityViolationException.class);
        when(jobTitleRepository.findByTitle(title)).thenReturn(Optional.empty());

        IllegalJobTitleStateException exception = assertThrows(
                IllegalJobTitleStateException.class,
                () -> jobTitleService.create(title)
        );

        assertTrue(exception.getMessage().contains("Название должности с таким именем уже существует"));
        verify(jobTitleRepository).save(any(JobTitle.class));
        verify(jobTitleRepository).findByTitle(title);
    }

    @Test
    void findByName_ShouldReturnJobTitle_WhenJobTitleExists() {
        String title = "Software Engineer";
        JobTitle jobTitle = JobTitle.builder()
                .id(1L)
                .title(title)
                .build();

        when(jobTitleRepository.findByTitle(title)).thenReturn(Optional.of(jobTitle));

        JobTitle result = jobTitleService.findByName(title);

        assertNotNull(result);
        assertEquals(title, result.getTitle());
        verify(jobTitleRepository).findByTitle(title);
    }

    @Test
    void findByName_ShouldReturnNull_WhenJobTitleDoesNotExist() {
        String title = "Non-existent Title";
        when(jobTitleRepository.findByTitle(title)).thenReturn(Optional.empty());

        JobTitle result = jobTitleService.findByName(title);

        assertNull(result);
        verify(jobTitleRepository).findByTitle(title);
    }

    @Test
    void findAll_ShouldReturnListOfJobTitles() {
        JobTitle jobTitle1 = JobTitle.builder().id(1L).title("Title 1").build();
        JobTitle jobTitle2 = JobTitle.builder().id(2L).title("Title 2").build();
        List<JobTitle> jobTitles = List.of(jobTitle1, jobTitle2);

        when(jobTitleRepository.findAll()).thenReturn(jobTitles);

        List<JobTitle> result = jobTitleService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(jobTitleRepository).findAll();
    }

    @Test
    void update_ShouldReturnUpdatedJobTitle_WhenSuccessful() {
        Long jobTitleId = 1L;
        JobTitle existingJobTitle = JobTitle.builder()
                .id(jobTitleId)
                .title("Old Title")
                .build();
        JobTitle updatedJobTitle = JobTitle.builder()
                .id(jobTitleId)
                .title("New Title")
                .build();
        JobTitle savedJobTitle = JobTitle.builder()
                .id(jobTitleId)
                .title("New Title")
                .build();

        when(jobTitleRepository.findById(jobTitleId)).thenReturn(Optional.of(existingJobTitle));
        when(jobTitleRepository.save(existingJobTitle)).thenReturn(savedJobTitle);

        JobTitle result = jobTitleService.update(updatedJobTitle);

        assertNotNull(result);
        assertEquals("New Title", result.getTitle());
        verify(jobTitleRepository).findById(jobTitleId);
        verify(jobTitleRepository).save(existingJobTitle);
    }

    @Test
    void update_ShouldThrowJobTitleNotFoundException_WhenJobTitleDoesNotExist() {
        Long jobTitleId = 999L;
        JobTitle jobTitle = JobTitle.builder()
                .id(jobTitleId)
                .title("Non-existent Title")
                .build();

        when(jobTitleRepository.findById(jobTitleId)).thenReturn(Optional.empty());

        JobTitleNotFoundException exception = assertThrows(
                JobTitleNotFoundException.class,
                () -> jobTitleService.update(jobTitle)
        );

        assertTrue(exception.getMessage().contains("Не найдена должность с id: " + jobTitleId));
        verify(jobTitleRepository).findById(jobTitleId);
        verify(jobTitleRepository, never()).save(any(JobTitle.class));
    }

    @Test
    void delete_ShouldCallRepositoryDelete() {
        Long jobTitleId = 1L;
        doNothing().when(jobTitleRepository).deleteById(jobTitleId);

        jobTitleService.delete(jobTitleId);

        verify(jobTitleRepository).deleteById(jobTitleId);
    }

    @Test
    void findById_ShouldReturnJobTitle_WhenJobTitleExists() {
        Long jobTitleId = 1L;
        JobTitle jobTitle = JobTitle.builder()
                .id(jobTitleId)
                .title("Test Title")
                .build();

        when(jobTitleRepository.findById(jobTitleId)).thenReturn(Optional.of(jobTitle));

        JobTitle result = jobTitleService.findById(jobTitleId);

        assertNotNull(result);
        assertEquals(jobTitleId, result.getId());
        verify(jobTitleRepository).findById(jobTitleId);
    }

    @Test
    void findById_ShouldThrowJobTitleNotFoundException_WhenJobTitleDoesNotExist() {
        Long jobTitleId = 999L;
        when(jobTitleRepository.findById(jobTitleId)).thenReturn(Optional.empty());

        JobTitleNotFoundException exception = assertThrows(
                JobTitleNotFoundException.class,
                () -> jobTitleService.findById(jobTitleId)
        );

        assertTrue(exception.getMessage().contains("Не найдена должность с id: " + jobTitleId));
        verify(jobTitleRepository).findById(jobTitleId);
    }
}