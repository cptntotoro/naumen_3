package ru.anastasia.NauJava.service.company.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.anastasia.NauJava.entity.company.Company;
import ru.anastasia.NauJava.repository.company.CompanyRepository;
import ru.anastasia.NauJava.service.company.CompanyService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Transactional
public class CompanyServiceImpl implements CompanyService {
    /**
     * Репозиторий компаний
     */
    private final CompanyRepository companyRepository;

    @Autowired
    public CompanyServiceImpl(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    @Transactional
    public Company create(String name) {
        Company existingCompany = findByName(name);
        if (existingCompany != null) {
            return existingCompany;
        }

        try {
            Company company = Company.builder()
                    .name(name)
                    .build();
            return companyRepository.save(company);
        } catch (DataIntegrityViolationException e) {
            Company company = findByName(name);
            if (company != null) {
                return company;
            }
            throw new RuntimeException("Не удалось создать компанию: " + name);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Company findByName(String name) {
        return companyRepository.findByName(name)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Company> findAll() {
        return StreamSupport.stream(companyRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Company update(Long id, String name, String website) {
        return companyRepository.findById(id)
                .map(company -> {
                    company.setName(name);
                    company.setWebsite(website);
                    return companyRepository.save(company);
                })
                .orElseThrow(() -> new RuntimeException("Не найдена компания с id: " + id));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        companyRepository.deleteById(id);
    }
}