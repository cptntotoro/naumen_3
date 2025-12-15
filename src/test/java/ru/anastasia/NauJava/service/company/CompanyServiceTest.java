package ru.anastasia.NauJava.service.company;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.anastasia.NauJava.entity.company.Company;
import ru.anastasia.NauJava.exception.company.CompanyNotFoundException;
import ru.anastasia.NauJava.exception.company.IllegalCompanyStateException;
import ru.anastasia.NauJava.repository.company.CompanyRepository;
import ru.anastasia.NauJava.service.company.impl.CompanyServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private CompanyServiceImpl companyService;

    private Company createTestCompany() {
        return Company.builder()
                .id(1L)
                .name("Тестовая компания")
                .website("https://test-company.ru")
                .build();
    }

    private Company createAnotherTestCompany() {
        return Company.builder()
                .id(2L)
                .name("Другая тестовая компания")
                .website("https://another-company.ru")
                .build();
    }

    @Test
    void create_WhenValidCompany_ShouldReturnSavedCompany() {
        Company testCompany = createTestCompany();
        Company savedCompany = createTestCompany();

        when(companyRepository.save(testCompany)).thenReturn(savedCompany);

        Company result = companyService.create(testCompany);

        assertNotNull(result);
        assertEquals(savedCompany.getId(), result.getId());
        assertEquals(savedCompany.getName(), result.getName());
        assertEquals(savedCompany.getWebsite(), result.getWebsite());
        verify(companyRepository, times(1)).save(testCompany);
    }

    @Test
    void create_WhenDuplicateCompanyName_ShouldThrowIllegalCompanyStateException() {
        Company testCompany = createTestCompany();

        when(companyRepository.save(testCompany))
                .thenThrow(new DataIntegrityViolationException("Duplicate company name"));

        IllegalCompanyStateException exception = assertThrows(
                IllegalCompanyStateException.class,
                () -> companyService.create(testCompany)
        );

        assertTrue(exception.getMessage().contains("Не удалось создать компанию"));
        assertTrue(exception.getMessage().contains("Компания с таким именем уже существует"));
        verify(companyRepository, times(1)).save(testCompany);
    }

    @Test
    void findAll_WhenCompaniesExist_ShouldReturnAllCompanies() {
        List<Company> companies = Arrays.asList(createTestCompany(), createAnotherTestCompany());

        when(companyRepository.findAll()).thenReturn(companies);

        List<Company> result = companyService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(companyRepository, times(1)).findAll();
    }

    @Test
    void findAll_WhenNoCompanies_ShouldReturnEmptyList() {
        when(companyRepository.findAll()).thenReturn(List.of());

        List<Company> result = companyService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(companyRepository, times(1)).findAll();
    }

    @Test
    void update_WhenValidCompany_ShouldReturnUpdatedCompany() {
        Long companyId = 1L;
        Company existingCompany = createTestCompany();
        Company updatedData = Company.builder()
                .id(companyId)
                .name("Обновленное название")
                .website("https://updated-website.ru")
                .build();
        Company savedCompany = Company.builder()
                .id(companyId)
                .name("Обновленное название")
                .website("https://updated-website.ru")
                .build();

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(existingCompany));
        when(companyRepository.save(existingCompany)).thenReturn(savedCompany);

        Company result = companyService.update(updatedData);

        assertNotNull(result);
        assertEquals(updatedData.getName(), result.getName());
        assertEquals(updatedData.getWebsite(), result.getWebsite());
        verify(companyRepository, times(1)).findById(companyId);
        verify(companyRepository, times(1)).save(existingCompany);
    }

    @Test
    void update_WhenCompanyNotFound_ShouldThrowCompanyNotFoundException() {
        Long nonExistentId = 999L;
        Company updatedData = Company.builder()
                .id(nonExistentId)
                .name("Несуществующая компания")
                .build();

        when(companyRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        CompanyNotFoundException exception = assertThrows(
                CompanyNotFoundException.class,
                () -> companyService.update(updatedData)
        );

        assertTrue(exception.getMessage().contains("Компания не найдена с id: " + nonExistentId));
        verify(companyRepository, times(1)).findById(nonExistentId);
        verify(companyRepository, never()).save(any(Company.class));
    }

    @Test
    void update_WhenDuplicateCompanyName_ShouldThrowIllegalCompanyStateException() {
        Long companyId = 1L;
        Company existingCompany = createTestCompany();
        Company updatedData = Company.builder()
                .id(companyId)
                .name("Дублирующееся название")
                .website("https://test-company.ru")
                .build();

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(existingCompany));
        when(companyRepository.save(existingCompany))
                .thenThrow(new DataIntegrityViolationException("Duplicate company name"));

        IllegalCompanyStateException exception = assertThrows(
                IllegalCompanyStateException.class,
                () -> companyService.update(updatedData)
        );

        assertTrue(exception.getMessage().contains("Компания с таким именем уже существует"));
        verify(companyRepository, times(1)).findById(companyId);
        verify(companyRepository, times(1)).save(existingCompany);
    }

    @Test
    void delete_WhenValidId_ShouldCallRepositoryDelete() {
        Long companyId = 1L;

        doNothing().when(companyRepository).deleteById(companyId);

        companyService.delete(companyId);

        verify(companyRepository, times(1)).deleteById(companyId);
    }

    @Test
    void findById_WhenCompanyExists_ShouldReturnCompany() {
        Long companyId = 1L;
        Company testCompany = createTestCompany();

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(testCompany));

        Company result = companyService.findById(companyId);

        assertNotNull(result);
        assertEquals(testCompany.getId(), result.getId());
        assertEquals(testCompany.getName(), result.getName());
        verify(companyRepository, times(1)).findById(companyId);
    }

    @Test
    void findById_WhenCompanyNotExists_ShouldThrowCompanyNotFoundException() {
        Long nonExistentId = 999L;

        when(companyRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        CompanyNotFoundException exception = assertThrows(
                CompanyNotFoundException.class,
                () -> companyService.findById(nonExistentId)
        );

        assertTrue(exception.getMessage().contains("Компания не найдена с id: " + nonExistentId));
        verify(companyRepository, times(1)).findById(nonExistentId);
    }

    @Test
    void countTotal_WhenCompaniesExist_ShouldReturnCount() {
        long expectedCount = 5L;

        when(companyRepository.count()).thenReturn(expectedCount);

        Long result = companyService.countTotal();

        assertEquals(expectedCount, result);
        verify(companyRepository, times(1)).count();
    }

    @Test
    void countTotal_WhenNoCompanies_ShouldReturnZero() {
        when(companyRepository.count()).thenReturn(0L);

        Long result = companyService.countTotal();

        assertEquals(0L, result);
        verify(companyRepository, times(1)).count();
    }

    @Test
    void findByNameContaining_WhenNamePartExists_ShouldReturnMatchingCompanies() {
        String namePart = "Тест";
        Company testCompany1 = createTestCompany();
        Company testCompany2 = createAnotherTestCompany();
        List<Company> expectedCompanies = Arrays.asList(testCompany1, testCompany2);

        when(companyRepository.findByNameContainingIgnoreCase(namePart)).thenReturn(expectedCompanies);

        List<Company> result = companyService.findByNameContaining(namePart);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedCompanies, result);
        verify(companyRepository, times(1)).findByNameContainingIgnoreCase(namePart);
    }

    @Test
    void findByNameContaining_WhenMultipleCompaniesMatch_ShouldReturnAllMatches() {
        String namePart = "компания";
        Company testCompany1 = Company.builder()
                .id(1L)
                .name("Тестовая компания")
                .build();
        Company testCompany2 = Company.builder()
                .id(2L)
                .name("Лучшая компания")
                .build();
        Company testCompany3 = Company.builder()
                .id(3L)
                .name("Другая компания")
                .build();
        List<Company> expectedCompanies = Arrays.asList(testCompany1, testCompany2, testCompany3);

        when(companyRepository.findByNameContainingIgnoreCase(namePart)).thenReturn(expectedCompanies);

        List<Company> result = companyService.findByNameContaining(namePart);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.stream().allMatch(c -> c.getName().toLowerCase().contains(namePart.toLowerCase())));
        verify(companyRepository, times(1)).findByNameContainingIgnoreCase(namePart);
    }

    @Test
    void findByNameContaining_WhenNoCompaniesMatch_ShouldReturnEmptyList() {
        String namePart = "несуществующая";
        List<Company> expectedCompanies = List.of();

        when(companyRepository.findByNameContainingIgnoreCase(namePart)).thenReturn(expectedCompanies);

        List<Company> result = companyService.findByNameContaining(namePart);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(companyRepository, times(1)).findByNameContainingIgnoreCase(namePart);
    }

    @Test
    void findByNameContaining_WhenSearchIsCaseInsensitive_ShouldFindCompanies() {
        String namePart = "ТЕСТОВАЯ";
        Company testCompany = createTestCompany();
        List<Company> expectedCompanies = List.of(testCompany);

        when(companyRepository.findByNameContainingIgnoreCase(namePart)).thenReturn(expectedCompanies);

        List<Company> result = companyService.findByNameContaining(namePart);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testCompany, result.getFirst());
        verify(companyRepository, times(1)).findByNameContainingIgnoreCase(namePart);
    }

    @Test
    void findByNameContaining_WhenNamePartIsEmpty_ShouldReturnAllCompanies() {
        String namePart = "";
        Company testCompany1 = createTestCompany();
        Company testCompany2 = createAnotherTestCompany();
        List<Company> expectedCompanies = Arrays.asList(testCompany1, testCompany2);

        when(companyRepository.findByNameContainingIgnoreCase(namePart)).thenReturn(expectedCompanies);

        List<Company> result = companyService.findByNameContaining(namePart);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(companyRepository, times(1)).findByNameContainingIgnoreCase(namePart);
    }

    @Test
    void findByNameContaining_WhenNamePartIsBlank_ShouldReturnAllCompanies() {
        String namePart = "   ";
        Company testCompany1 = createTestCompany();
        Company testCompany2 = createAnotherTestCompany();
        List<Company> expectedCompanies = Arrays.asList(testCompany1, testCompany2);

        when(companyRepository.findByNameContainingIgnoreCase(namePart)).thenReturn(expectedCompanies);

        List<Company> result = companyService.findByNameContaining(namePart);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(companyRepository, times(1)).findByNameContainingIgnoreCase(namePart);
    }

    @Test
    void findByNameContaining_WhenNamePartIsNull_ShouldHandleGracefully() {
        String namePart = null;
        Company testCompany1 = createTestCompany();
        Company testCompany2 = createAnotherTestCompany();
        List<Company> expectedCompanies = Arrays.asList(testCompany1, testCompany2);

        when(companyRepository.findByNameContainingIgnoreCase(namePart)).thenReturn(expectedCompanies);

        List<Company> result = companyService.findByNameContaining(namePart);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(companyRepository, times(1)).findByNameContainingIgnoreCase(namePart);
    }

    @Test
    void findByNameContaining_WhenPartialMatch_ShouldReturnCompanies() {
        String namePart = "тест";
        Company testCompany1 = createTestCompany();
        List<Company> expectedCompanies = List.of(testCompany1);

        when(companyRepository.findByNameContainingIgnoreCase(namePart)).thenReturn(expectedCompanies);

        List<Company> result = companyService.findByNameContaining(namePart);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testCompany1, result.getFirst());
        verify(companyRepository, times(1)).findByNameContainingIgnoreCase(namePart);
    }

    @Test
    void findByNameContaining_WhenSpecialCharactersInName_ShouldHandleCorrectly() {
        String namePart = "ООО";
        Company testCompany = Company.builder()
                .id(1L)
                .name("ООО \"Тестовая компания\"")
                .build();
        List<Company> expectedCompanies = List.of(testCompany);

        when(companyRepository.findByNameContainingIgnoreCase(namePart)).thenReturn(expectedCompanies);

        List<Company> result = companyService.findByNameContaining(namePart);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testCompany, result.getFirst());
        verify(companyRepository, times(1)).findByNameContainingIgnoreCase(namePart);
    }

    @Test
    void findByNameContaining_WhenRepositoryReturnsNull_ShouldReturnEmptyList() {
        String namePart = "тест";

        when(companyRepository.findByNameContainingIgnoreCase(namePart)).thenReturn(null);

        List<Company> result = companyService.findByNameContaining(namePart);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(companyRepository, times(1)).findByNameContainingIgnoreCase(namePart);
    }
}