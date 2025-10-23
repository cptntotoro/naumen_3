package ru.anastasia.NauJava.repository.company;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.anastasia.NauJava.entity.company.JobTitle;

import java.util.Optional;
import java.util.UUID;

@SpringBootTest
@Transactional
class JobTitleRepositoryTest {

    @Autowired
    private JobTitleRepository jobTitleRepository;

    @Test
    void testFindByTitle() {
        String title = "TestJobTitle" + UUID.randomUUID();

        JobTitle jobTitle = new JobTitle();
        jobTitle.setTitle(title);
        jobTitleRepository.save(jobTitle);

        Optional<JobTitle> foundJobTitle = jobTitleRepository.findByTitle(title);

        Assertions.assertTrue(foundJobTitle.isPresent());
        Assertions.assertEquals(title, foundJobTitle.get().getTitle());
    }

    @Test
    void testFindByTitleContainingIgnoreCase() {
        String titlePart = "Manager" + UUID.randomUUID();
        String fullTitle = "Senior " + titlePart;

        JobTitle jobTitle = new JobTitle();
        jobTitle.setTitle(fullTitle);
        jobTitleRepository.save(jobTitle);

        var foundTitles = jobTitleRepository.findByTitleContainingIgnoreCase(titlePart.toLowerCase());

        Assertions.assertFalse(foundTitles.isEmpty());
        Assertions.assertTrue(foundTitles.getFirst().getTitle().contains(titlePart));
    }
}