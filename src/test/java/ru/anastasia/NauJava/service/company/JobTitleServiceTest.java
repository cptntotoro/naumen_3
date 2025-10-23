package ru.anastasia.NauJava.service.company;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.anastasia.NauJava.entity.company.JobTitle;
import ru.anastasia.NauJava.repository.company.JobTitleRepository;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class JobTitleServiceTest {

    @Autowired
    private JobTitleService jobTitleService;

    @Autowired
    private JobTitleRepository jobTitleRepository;

    @Test
    void testCreate_Success() {
        String title = "Разработчик" + UUID.randomUUID();
        JobTitle jobTitle = jobTitleService.create(title);

        assertNotNull(jobTitle.getId());
        assertEquals(title, jobTitle.getTitle());
        assertTrue(jobTitleRepository.findByTitle(title).isPresent());
    }

    @Test
    void testCreate_ExistingJobTitle() {
        String title = "Разработчик" + UUID.randomUUID();
        JobTitle jobTitle1 = jobTitleService.create(title);
        JobTitle jobTitle2 = jobTitleService.create(title);

        assertEquals(jobTitle1.getId(), jobTitle2.getId());
        assertEquals(title, jobTitle2.getTitle());
    }

    @Test
    void testFindByName_Found() {
        String title = "Менеджер" + UUID.randomUUID();
        jobTitleService.create(title);

        JobTitle foundJobTitle = jobTitleService.findByName(title);

        assertNotNull(foundJobTitle);
        assertEquals(title, foundJobTitle.getTitle());
    }

    @Test
    void testFindByName_NotFound() {
        String title = "НеСуществует" + UUID.randomUUID();

        JobTitle foundJobTitle = jobTitleService.findByName(title);

        assertNull(foundJobTitle);
    }

    @Test
    void testFindAll() {
        String title1 = "Разработчик" + UUID.randomUUID();
        String title2 = "Менеджер" + UUID.randomUUID();
        jobTitleService.create(title1);
        jobTitleService.create(title2);

        List<JobTitle> jobTitles = jobTitleService.findAll();

        assertTrue(jobTitles.size() >= 2);
        assertTrue(jobTitles.stream().anyMatch(jt -> jt.getTitle().equals(title1)));
        assertTrue(jobTitles.stream().anyMatch(jt -> jt.getTitle().equals(title2)));
    }

    @Test
    void testUpdate_Success() {
        String title = "Разработчик" + UUID.randomUUID();
        String newTitle = "Старший Разработчик" + UUID.randomUUID();
        JobTitle jobTitle = jobTitleService.create(title);

        JobTitle updatedJobTitle = jobTitleService.update(jobTitle.getId(), newTitle);

        assertEquals(newTitle, updatedJobTitle.getTitle());
        assertTrue(jobTitleRepository.findByTitle(newTitle).isPresent());
    }

    @Test
    void testUpdate_NotFound() {
        Long nonExistentId = 999L;
        String newTitle = "Старший Разработчик" + UUID.randomUUID();

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                jobTitleService.update(nonExistentId, newTitle));

        assertEquals("Не найдена должность с id: " + nonExistentId, exception.getMessage());
    }

    @Test
    void testDelete_Success() {
        String title = "Разработчик" + UUID.randomUUID();
        JobTitle jobTitle = jobTitleService.create(title);

        jobTitleService.delete(jobTitle.getId());

        assertFalse(jobTitleRepository.findById(jobTitle.getId()).isPresent());
    }
}
