package ru.anastasia.NauJava.service.company.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.anastasia.NauJava.dto.company.ContactCompanyCreateDto;
import ru.anastasia.NauJava.dto.company.ContactCompanyUpdateDto;
import ru.anastasia.NauJava.entity.company.Company;
import ru.anastasia.NauJava.entity.company.ContactCompany;
import ru.anastasia.NauJava.entity.company.JobTitle;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.exception.company.IllegalCompanyStateException;
import ru.anastasia.NauJava.repository.company.ContactCompanyRepository;
import ru.anastasia.NauJava.service.company.CompanyService;
import ru.anastasia.NauJava.service.company.ContactCompanyService;
import ru.anastasia.NauJava.service.company.JobTitleService;
import ru.anastasia.NauJava.service.contact.ContactService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContactCompanyServiceImpl implements ContactCompanyService {

    /**
     * Репозиторий компаний контакта
     */
    private final ContactCompanyRepository contactCompanyRepository;

    /**
     * Сервис контактов
     */
    private final ContactService contactService;

    /**
     * Сервис компаний
     */
    private final CompanyService companyService;

    /**
     * Сервис должностей
     */
    private final JobTitleService jobTitleService;

    @Transactional
    @Override
    public ContactCompany create(ContactCompanyCreateDto dto, Long contactId) {
        log.info("Создание связи контакт-компания для контакта [ID: {}]", contactId);

        Contact contact = contactService.findById(contactId);
        Company company = companyService.findById(dto.getCompanyId());
        JobTitle jobTitle = jobTitleService.findById(dto.getJobTitleId());

        // Если это новое текущее место — сбрасываем старое
        if (Boolean.TRUE.equals(dto.getIsCurrent())) {
            resetCurrentFlagForContact(contactId);
        }

        ContactCompany contactCompany = ContactCompany.builder()
                .contact(contact)
                .company(company)
                .jobTitle(jobTitle)
                .isCurrent(dto.getIsCurrent())
                .build();

        ContactCompany saved = contactCompanyRepository.save(contactCompany);
        log.info("Связь контакт-компания успешно создана [ID: {}], isCurrent: {}", saved.getId(), saved.getIsCurrent());
        return saved;
    }

    @Transactional(readOnly = true)
    @Override
    public ContactCompany findById(Long id) {
        log.debug("Поиск связи контакт-компания по ID: {}", id);

        return contactCompanyRepository.findById(id)
                .orElseThrow(() -> new IllegalCompanyStateException("Связь не найдена с id: " + id));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ContactCompany> findByContactId(Long contactId) {
        log.debug("Поиск компаний контакта по ID контакта: {}", contactId);

        List<ContactCompany> contactCompanies = contactCompanyRepository.findByContactId(contactId);
        log.debug("Найдено {} компаний для контакта {}", contactCompanies.size(), contactId);

        return contactCompanies;
    }

    @Transactional
    @Override
    public ContactCompany update(Long id, ContactCompanyUpdateDto dto) {
        log.info("Обновление связи контакт-компания [ID: {}]", id);

        ContactCompany contactCompany = contactCompanyRepository.findById(id)
                .orElseThrow(() -> new IllegalCompanyStateException("Связь не найдена с id: " + id));

        Long contactId = contactCompany.getContact().getId();

        if (Boolean.TRUE.equals(dto.getIsCurrent())) {
            resetCurrentFlagForContact(contactId);
        }

        if (dto.getCompanyId() != null) {
            Company company = companyService.findById(dto.getCompanyId());
            contactCompany.setCompany(company);
        }
        if (dto.getJobTitleId() != null) {
            JobTitle jobTitle = jobTitleService.findById(dto.getJobTitleId());
            contactCompany.setJobTitle(jobTitle);
        }

        contactCompany.setIsCurrent(dto.getIsCurrent());

        ContactCompany updated = contactCompanyRepository.save(contactCompany);
        log.info("Связь контакт-компания успешно обновлена [ID: {}], isCurrent: {}", id, updated.getIsCurrent());
        return updated;
    }

    @Transactional
    @Override
    public void delete(Long id) {
        log.info("Удаление связи контакт-компания [id={}]", id);

        ContactCompany contactCompany = findById(id);
        if (contactCompany != null) {
            contactCompanyRepository.delete(contactCompany);
            log.info("Связь контакт-компания удалена [id={}, company={}]",
                    id, contactCompany.getCompany().getName());
        } else {
            log.warn("Связь контакт-компания не найдена при удалении [id={}]", id);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ContactCompany findCurrentByContactId(Long contactId) {
        log.debug("Поиск текущего места работы для контакта: {}", contactId);

        return contactCompanyRepository.findCurrentByContactId(contactId).orElse(null);
    }

    @Transactional(readOnly = true)
    @Override
    public Long countContactsInCompany(Long companyId) {
        log.debug("Подсчет контактов в компании: {}", companyId);

        Long count = contactCompanyRepository.countByCompanyId(companyId);
        log.debug("В компании {} найдено {} контактов", companyId, count);

        return count;
    }

    @Transactional
    @Override
    public void deleteByContactId(Long contactId) {
        log.info("Удаление всех связей контакт-компания для контакта: {}", contactId);

        int deletedCount = contactCompanyRepository.findByContactId(contactId).size();
        contactCompanyRepository.deleteByContactId(contactId);
        log.info("Удалено {} связей для контакта {}", deletedCount, contactId);
    }

    /**
     * Сбрасывает флаг текущего места работы у всех записей контакта
     */
    @Transactional
    protected void resetCurrentFlagForContact(Long contactId) {
        List<ContactCompany> allForContact = contactCompanyRepository.findByContactId(contactId);
        boolean changed = false;
        for (ContactCompany cc : allForContact) {
            if (Boolean.TRUE.equals(cc.getIsCurrent())) {
                cc.setIsCurrent(false);
                changed = true;
            }
        }
        if (changed) {
            contactCompanyRepository.saveAll(allForContact);
            log.debug("Сброшены флаги isCurrent для всех мест работы контакта {}", contactId);
        }
    }
}