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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private CompanyServiceImpl companyService;

    @Test
    void create_ShouldReturnCompany_WhenSuccessful() {
        Company company = Company.builder()
                .name("Test Company")
                .website("https://test.com")
                .build();
        Company savedCompany = Company.builder()
                .id(1L)
                .name("Test Company")
                .website("https://test.com")
                .createdAt(LocalDateTime.now())
                .build();

        when(companyRepository.save(any(Company.class))).thenReturn(savedCompany);

        Company result = companyService.create(company);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Company", result.getName());
        verify(companyRepository).save(company);
    }

    @Test
    void create_ShouldThrowIllegalCompanyStateException_WhenCompanyNameAlreadyExists() {
        Company company = Company.builder()
                .name("Existing Company")
                .website("https://existing.com")
                .build();

        when(companyRepository.save(any(Company.class)))
                .thenThrow(DataIntegrityViolationException.class);

        IllegalCompanyStateException exception = assertThrows(
                IllegalCompanyStateException.class,
                () -> companyService.create(company)
        );

        assertTrue(exception.getMessage().contains("Компания с таким именем уже существует"));
        verify(companyRepository).save(company);
    }

    @Test
    void findByName_ShouldReturnCompany_WhenCompanyExists() {
        String companyName = "Test Company";
        Company company = Company.builder()
                .id(1L)
                .name(companyName)
                .build();

        when(companyRepository.findByName(companyName)).thenReturn(Optional.of(company));

        Company result = companyService.findByName(companyName);

        assertNotNull(result);
        assertEquals(companyName, result.getName());
        verify(companyRepository).findByName(companyName);
    }

    @Test
    void findByName_ShouldReturnNull_WhenCompanyDoesNotExist() {
        String companyName = "Non-existent Company";
        when(companyRepository.findByName(companyName)).thenReturn(Optional.empty());

        Company result = companyService.findByName(companyName);

        assertNull(result);
        verify(companyRepository).findByName(companyName);
    }

    @Test
    void findAll_ShouldReturnListOfCompanies() {
        Company company1 = Company.builder().id(1L).name("Company 1").build();
        Company company2 = Company.builder().id(2L).name("Company 2").build();
        List<Company> companies = List.of(company1, company2);

        when(companyRepository.findAll()).thenReturn(companies);

        List<Company> result = companyService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(companyRepository).findAll();
    }

    @Test
    void update_ShouldReturnUpdatedCompany_WhenSuccessful() {
        Long companyId = 1L;
        Company existingCompany = Company.builder()
                .id(companyId)
                .name("Old Name")
                .website("https://old.com")
                .build();
        Company updatedCompany = Company.builder()
                .id(companyId)
                .name("New Name")
                .website("https://new.com")
                .build();
        Company savedCompany = Company.builder()
                .id(companyId)
                .name("New Name")
                .website("https://new.com")
                .build();

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(existingCompany));
        when(companyRepository.save(any(Company.class))).thenReturn(savedCompany);

        Company result = companyService.update(updatedCompany);

        assertNotNull(result);
        assertEquals("New Name", result.getName());
        assertEquals("https://new.com", result.getWebsite());
        verify(companyRepository).findById(companyId);
        verify(companyRepository).save(existingCompany);
    }

    @Test
    void update_ShouldThrowIllegalCompanyStateException_WhenCompanyNameAlreadyExists() {
        Long companyId = 1L;
        Company existingCompany = Company.builder()
                .id(companyId)
                .name("Old Name")
                .build();
        Company updatedCompany = Company.builder()
                .id(companyId)
                .name("Existing Name")
                .build();

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(existingCompany));
        when(companyRepository.save(any(Company.class)))
                .thenThrow(DataIntegrityViolationException.class);

        IllegalCompanyStateException exception = assertThrows(
                IllegalCompanyStateException.class,
                () -> companyService.update(updatedCompany)
        );

        assertTrue(exception.getMessage().contains("Компания с таким именем уже существует"));
        verify(companyRepository).findById(companyId);
        verify(companyRepository).save(existingCompany);
    }

    @Test
    void delete_ShouldCallRepositoryDelete() {
        Long companyId = 1L;
        doNothing().when(companyRepository).deleteById(companyId);

        companyService.delete(companyId);

        verify(companyRepository).deleteById(companyId);
    }

    @Test
    void findById_ShouldReturnCompany_WhenCompanyExists() {
        Long companyId = 1L;
        Company company = Company.builder()
                .id(companyId)
                .name("Test Company")
                .build();

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));

        Company result = companyService.findById(companyId);

        assertNotNull(result);
        assertEquals(companyId, result.getId());
        verify(companyRepository).findById(companyId);
    }

    @Test
    void findById_ShouldThrowCompanyNotFoundException_WhenCompanyDoesNotExist() {
        Long companyId = 999L;
        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

        CompanyNotFoundException exception = assertThrows(
                CompanyNotFoundException.class,
                () -> companyService.findById(companyId)
        );

        assertTrue(exception.getMessage().contains("Компания не найдена с id: " + companyId));
        verify(companyRepository).findById(companyId);
    }
}