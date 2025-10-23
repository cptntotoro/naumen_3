package ru.anastasia.NauJava.repository.company;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.anastasia.NauJava.entity.company.Company;

import java.util.Optional;
import java.util.UUID;

@SpringBootTest
@Transactional
class CompanyRepositoryTest {

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    void testFindByName() {
        String companyName = "TestCompany" + UUID.randomUUID();

        Company company = new Company();
        company.setName(companyName);
        companyRepository.save(company);

        Optional<Company> foundCompany = companyRepository.findByName(companyName);

        Assertions.assertTrue(foundCompany.isPresent());
        Assertions.assertEquals(companyName, foundCompany.get().getName());
    }
}
