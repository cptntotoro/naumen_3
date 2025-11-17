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

import java.util.Arrays;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobTitleServiceTest {

    @Mock
    private JobTitleRepository jobTitleRepository;

    @InjectMocks
    private JobTitleServiceImpl jobTitleService;

    private JobTitle createTestJobTitle() {
        return JobTitle.builder()
                .id(1L)
                .title("Тестовая должность")
                .build();
    }

    private JobTitle createAnotherTestJobTitle() {
        return JobTitle.builder()
                .id(2L)
                .title("Другая тестовая должность")
                .build();
    }

    @Test
    void create_WhenValidTitle_ShouldReturnSavedJobTitle() {
        String title = "Тестовая должность";
        JobTitle savedJobTitle = createTestJobTitle();

        when(jobTitleRepository.save(any(JobTitle.class))).thenReturn(savedJobTitle);

        JobTitle result = jobTitleService.create(title);

        assertNotNull(result);
        assertEquals(savedJobTitle.getId(), result.getId());
        assertEquals(savedJobTitle.getTitle(), result.getTitle());
        verify(jobTitleRepository, times(1)).save(any(JobTitle.class));
    }

    @Test
    void create_WhenDuplicateTitleAndJobTitleExists_ShouldReturnExistingJobTitle() {
        String duplicateTitle = "Дублирующаяся должность";
        JobTitle existingJobTitle = JobTitle.builder()
                .id(1L)
                .title(duplicateTitle)
                .build();

        when(jobTitleRepository.save(any(JobTitle.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate title"));
        when(jobTitleRepository.findByTitle(duplicateTitle)).thenReturn(Optional.of(existingJobTitle));

        JobTitle result = jobTitleService.create(duplicateTitle);

        assertNotNull(result);
        assertEquals(existingJobTitle.getId(), result.getId());
        assertEquals(existingJobTitle.getTitle(), result.getTitle());
        verify(jobTitleRepository, times(1)).save(any(JobTitle.class));
        verify(jobTitleRepository, times(1)).findByTitle(duplicateTitle);
    }

    @Test
    void create_WhenDuplicateTitleAndJobTitleNotExists_ShouldThrowIllegalJobTitleStateException() {
        String duplicateTitle = "Дублирующаяся должность";

        when(jobTitleRepository.save(any(JobTitle.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate title"));
        when(jobTitleRepository.findByTitle(duplicateTitle)).thenReturn(Optional.empty());

        IllegalJobTitleStateException exception = assertThrows(
                IllegalJobTitleStateException.class,
                () -> jobTitleService.create(duplicateTitle)
        );

        assertTrue(exception.getMessage().contains("Не удалось создать название должности"));
        assertTrue(exception.getMessage().contains("Название должности с таким именем уже существует"));
        verify(jobTitleRepository, times(1)).save(any(JobTitle.class));
        verify(jobTitleRepository, times(1)).findByTitle(duplicateTitle);
    }

    @Test
    void findByName_WhenJobTitleExists_ShouldReturnJobTitle() {
        String title = "Тестовая должность";
        JobTitle testJobTitle = createTestJobTitle();

        when(jobTitleRepository.findByTitle(title)).thenReturn(Optional.of(testJobTitle));

        JobTitle result = jobTitleService.findByName(title);

        assertNotNull(result);
        assertEquals(testJobTitle.getId(), result.getId());
        assertEquals(testJobTitle.getTitle(), result.getTitle());
        verify(jobTitleRepository, times(1)).findByTitle(title);
    }

    @Test
    void findByName_WhenJobTitleNotExists_ShouldReturnNull() {
        String nonExistentTitle = "Несуществующая должность";

        when(jobTitleRepository.findByTitle(nonExistentTitle)).thenReturn(Optional.empty());

        JobTitle result = jobTitleService.findByName(nonExistentTitle);

        assertNull(result);
        verify(jobTitleRepository, times(1)).findByTitle(nonExistentTitle);
    }

    @Test
    void findAll_WhenJobTitlesExist_ShouldReturnAllJobTitles() {
        List<JobTitle> jobTitles = Arrays.asList(createTestJobTitle(), createAnotherTestJobTitle());

        when(jobTitleRepository.findAll()).thenReturn(jobTitles);

        List<JobTitle> result = jobTitleService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(jobTitleRepository, times(1)).findAll();
    }

    @Test
    void findAll_WhenNoJobTitles_ShouldReturnEmptyList() {
        when(jobTitleRepository.findAll()).thenReturn(List.of());

        List<JobTitle> result = jobTitleService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(jobTitleRepository, times(1)).findAll();
    }

    @Test
    void update_WhenValidJobTitle_ShouldReturnUpdatedJobTitle() {
        Long jobTitleId = 1L;
        JobTitle existingJobTitle = createTestJobTitle();
        JobTitle updatedData = JobTitle.builder()
                .id(jobTitleId)
                .title("Обновленное название должности")
                .build();
        JobTitle savedJobTitle = JobTitle.builder()
                .id(jobTitleId)
                .title("Обновленное название должности")
                .build();

        when(jobTitleRepository.findById(jobTitleId)).thenReturn(Optional.of(existingJobTitle));
        when(jobTitleRepository.save(existingJobTitle)).thenReturn(savedJobTitle);

        JobTitle result = jobTitleService.update(updatedData);

        assertNotNull(result);
        assertEquals(updatedData.getTitle(), result.getTitle());
        verify(jobTitleRepository, times(1)).findById(jobTitleId);
        verify(jobTitleRepository, times(1)).save(existingJobTitle);
    }

    @Test
    void update_WhenJobTitleNotFound_ShouldThrowJobTitleNotFoundException() {
        Long nonExistentId = 999L;
        JobTitle updatedData = JobTitle.builder()
                .id(nonExistentId)
                .title("Несуществующая должность")
                .build();

        when(jobTitleRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        JobTitleNotFoundException exception = assertThrows(
                JobTitleNotFoundException.class,
                () -> jobTitleService.update(updatedData)
        );

        assertTrue(exception.getMessage().contains("Не найдена должность с id: " + nonExistentId));
        verify(jobTitleRepository, times(1)).findById(nonExistentId);
        verify(jobTitleRepository, never()).save(any(JobTitle.class));
    }

    @Test
    void delete_WhenValidId_ShouldCallRepositoryDelete() {
        Long jobTitleId = 1L;

        doNothing().when(jobTitleRepository).deleteById(jobTitleId);

        jobTitleService.delete(jobTitleId);

        verify(jobTitleRepository, times(1)).deleteById(jobTitleId);
    }

    @Test
    void findById_WhenJobTitleExists_ShouldReturnJobTitle() {
        Long jobTitleId = 1L;
        JobTitle testJobTitle = createTestJobTitle();

        when(jobTitleRepository.findById(jobTitleId)).thenReturn(Optional.of(testJobTitle));

        JobTitle result = jobTitleService.findById(jobTitleId);

        assertNotNull(result);
        assertEquals(testJobTitle.getId(), result.getId());
        assertEquals(testJobTitle.getTitle(), result.getTitle());
        verify(jobTitleRepository, times(1)).findById(jobTitleId);
    }

    @Test
    void findById_WhenJobTitleNotExists_ShouldThrowJobTitleNotFoundException() {
        Long nonExistentId = 999L;

        when(jobTitleRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        JobTitleNotFoundException exception = assertThrows(
                JobTitleNotFoundException.class,
                () -> jobTitleService.findById(nonExistentId)
        );

        assertTrue(exception.getMessage().contains("Не найдена должность с id: " + nonExistentId));
        verify(jobTitleRepository, times(1)).findById(nonExistentId);
    }
}