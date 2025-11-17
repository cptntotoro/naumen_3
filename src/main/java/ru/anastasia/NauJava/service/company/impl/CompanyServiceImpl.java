package ru.anastasia.NauJava.service.company.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.anastasia.NauJava.entity.company.Company;
import ru.anastasia.NauJava.exception.company.CompanyNotFoundException;
import ru.anastasia.NauJava.exception.company.IllegalCompanyStateException;
import ru.anastasia.NauJava.repository.company.CompanyRepository;
import ru.anastasia.NauJava.service.company.CompanyService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Transactional
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {
    /**
     * Репозиторий компаний
     */
    private final CompanyRepository companyRepository;

    @Override
    @Transactional
    public Company create(Company company) {
        try {
            return companyRepository.save(company);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalCompanyStateException("Не удалось создать компанию: " + company.getName() + ". " +
                    "Компания с таким именем уже существует");
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
    public Company update(Company company) {
        Long companyId = company.getId();
        Company foundCompany = findById(companyId);

        if (foundCompany == null) {
            throw new CompanyNotFoundException("Не найдена компания с id: " + companyId);
        }

        foundCompany.setName(company.getName());
        foundCompany.setWebsite(company.getWebsite());

        try {
            return companyRepository.save(foundCompany);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalCompanyStateException("Компания с таким именем уже существует");
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        companyRepository.deleteById(id);
    }

    @Override
    public Company findById(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new CompanyNotFoundException("Компания не найдена с id: " + id));
    }

    @Override
    public Long countTotal() {
        return companyRepository.count();
    }
}