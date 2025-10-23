package ru.anastasia.NauJava.service.company;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.anastasia.NauJava.entity.company.Company;
import ru.anastasia.NauJava.repository.company.CompanyRepository;

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
class CompanyServiceTest {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    void testCreate_Success() {
        String companyName = "ООО Ромашка" + UUID.randomUUID();
        Company company = companyService.create(companyName);

        assertNotNull(company.getId());
        assertEquals(companyName, company.getName());
        assertTrue(companyRepository.findByName(companyName).isPresent());
    }

    @Test
    void testCreate_ExistingCompany() {
        String companyName = "ООО Ромашка" + UUID.randomUUID();
        Company company1 = companyService.create(companyName);
        Company company2 = companyService.create(companyName);

        assertEquals(company1.getId(), company2.getId());
        assertEquals(companyName, company2.getName());
    }

    @Test
    void testFindByName_Found() {
        String companyName = "ООО Лютик" + UUID.randomUUID();
        companyService.create(companyName);

        Company foundCompany = companyService.findByName(companyName);

        assertNotNull(foundCompany);
        assertEquals(companyName, foundCompany.getName());
    }

    @Test
    void testFindByName_NotFound() {
        String companyName = "ООО НеСуществует" + UUID.randomUUID();

        Company foundCompany = companyService.findByName(companyName);

        assertNull(foundCompany);
    }

    @Test
    void testFindAll() {
        String companyName1 = "ООО Ромашка" + UUID.randomUUID();
        String companyName2 = "ООО Лютик" + UUID.randomUUID();
        companyService.create(companyName1);
        companyService.create(companyName2);

        List<Company> companies = companyService.findAll();

        assertTrue(companies.size() >= 2);
        assertTrue(companies.stream().anyMatch(c -> c.getName().equals(companyName1)));
        assertTrue(companies.stream().anyMatch(c -> c.getName().equals(companyName2)));
    }

    @Test
    void testUpdate_Success() {
        String companyName = "ООО Ромашка" + UUID.randomUUID();
        String newName = "ООО Лютик" + UUID.randomUUID();
        String website = "http://lutik.ru";
        Company company = companyService.create(companyName);

        Company updatedCompany = companyService.update(company.getId(), newName, website);

        assertEquals(newName, updatedCompany.getName());
        assertEquals(website, updatedCompany.getWebsite());
        assertTrue(companyRepository.findByName(newName).isPresent());
    }

    @Test
    void testUpdate_NotFound() {
        Long nonExistentId = 999L;
        String newName = "ООО Лютик" + UUID.randomUUID();
        String website = "http://lutik.ru";

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                companyService.update(nonExistentId, newName, website));

        assertEquals("Не найдена компания с id: " + nonExistentId, exception.getMessage());
    }

    @Test
    void testDelete_Success() {
        String companyName = "ООО Ромашка" + UUID.randomUUID();
        Company company = companyService.create(companyName);

        companyService.delete(company.getId());

        assertFalse(companyRepository.findById(company.getId()).isPresent());
    }
}
