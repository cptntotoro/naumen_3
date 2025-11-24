package ru.anastasia.NauJava.service.company.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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
        log.info("Создание компании [название: {}]", company.getName());

        try {
            Company savedCompany = companyRepository.save(company);
            log.info("Компания успешно создана [ID: {}, название: {}]",
                    savedCompany.getId(), savedCompany.getName());
            return savedCompany;
        } catch (DataIntegrityViolationException e) {
            log.error("Ошибка целостности при создании компании [название: {}]", company.getName(), e);
            throw new IllegalCompanyStateException("Не удалось создать компанию: " + company.getName() + ". " +
                    "Компания с таким именем уже существует");
        } catch (Exception e) {
            log.error("Неожиданная ошибка при создании компании [название: {}]", company.getName(), e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Company findByName(String name) {
        log.debug("Поиск компании по названию: {}", name);

        return companyRepository.findByName(name)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Company> findAll() {
        log.debug("Получение списка всех компаний");

        List<Company> companies = StreamSupport.stream(companyRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
        log.debug("Найдено компаний: {}", companies.size());
        return companies;
    }

    @Override
    @Transactional
    public Company update(Company company) {
        log.info("Обновление компании [ID: {}, название: {}]", company.getId(), company.getName());

        Long companyId = company.getId();
        Company foundCompany = findById(companyId);

        if (foundCompany == null) {
            log.error("Компания не найдена при обновлении [ID: {}]", companyId);
            throw new CompanyNotFoundException("Не найдена компания с id: " + companyId);
        }

        foundCompany.setName(company.getName());
        foundCompany.setWebsite(company.getWebsite());

        try {
            Company updatedCompany = companyRepository.save(foundCompany);
            log.info("Компания успешно обновлена [ID: {}, название: {}]",
                    updatedCompany.getId(), updatedCompany.getName());
            return updatedCompany;
        } catch (DataIntegrityViolationException e) {
            log.error("Ошибка целостности при обновлении компании [ID: {}, название: {}]",
                    companyId, company.getName(), e);
            throw new IllegalCompanyStateException("Компания с таким именем уже существует");
        } catch (Exception e) {
            log.error("Неожиданная ошибка при обновлении компании [ID: {}]", companyId, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Удаление компании [ID: {}]", id);

        try {
            companyRepository.deleteById(id);
            log.info("Компания успешно удалена [ID: {}]", id);
        } catch (Exception e) {
            log.error("Ошибка при удалении компании [ID: {}]", id, e);
            throw e;
        }
    }

    @Override
    public Company findById(Long id) {
        log.debug("Поиск компании по ID: {}", id);

        return companyRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Компания не найдена [ID: {}]", id);
                    return new CompanyNotFoundException("Компания не найдена с id: " + id);
                });
    }

    @Override
    public Long countTotal() {
        log.debug("Подсчет общего количества компаний");

        Long count = companyRepository.count();
        log.debug("Общее количество компаний: {}", count);
        return count;
    }
}