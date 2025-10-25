package ru.anastasia.NauJava.service.company.impl;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {
    /**
     * Репозиторий компаний
     */
    private final CompanyRepository companyRepository;

    @Override
    @Transactional
    public Company create(Company company) {
        String companyName = company.getName();

        try {
            Company companyToSave = Company.builder()
                    .name(companyName)
                    .build();
            return companyRepository.save(companyToSave);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Не удалось создать компанию: " + companyName + ". " +
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

        return companyRepository.findById(companyId)
                .map(comp -> {
                    comp.setName(company.getName());
                    comp.setWebsite(company.getWebsite());
                    return companyRepository.save(comp);
                })
                .orElseThrow(() -> new RuntimeException("Не найдена компания с id: " + companyId));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        companyRepository.deleteById(id);
    }

    @Override
    public Company findById(Long id) {
        return companyRepository.findById(id).orElse(null);
    }
}