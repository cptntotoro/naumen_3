package ru.anastasia.NauJava.service.contact.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.anastasia.NauJava.config.AppConfig;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.entity.enums.EventType;
import ru.anastasia.NauJava.entity.event.Event;
import ru.anastasia.NauJava.exception.contact.ContactNotFoundException;
import ru.anastasia.NauJava.repository.contact.ContactRepository;
import ru.anastasia.NauJava.service.contact.ContactService;
import ru.anastasia.NauJava.service.event.EventService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

    /**
     * Репозиторий контактов
     */
    private final ContactRepository contactRepository;

    /**
     * Сервис событий
     */
    private final EventService eventService;

    /**
     * Конфигурация приложения
     */
    private final AppConfig appConfig;

    @PostConstruct
    public void init() {
        System.out.println("Название приложения: " + appConfig.getAppName());
        System.out.println("Версия приложения: " + appConfig.getAppVersion());
    }

    @Override
    public Contact add(String firstName, String lastName) {
        Contact contact = Contact.builder()
                .firstName(firstName)
                .lastName(lastName)
                .build();
        return contactRepository.save(contact);
    }

    @Override
    public Contact findById(Long id) {
        return contactRepository.findById(id)
                .orElseThrow(() -> new ContactNotFoundException("Не найден контакт с id: " + id));
    }

    @Override
    public void deleteById(Long id) {
        contactRepository.deleteById(id);
    }

    @Override
    public Contact update(Long id, String firstName, String lastName) {
        return contactRepository.findById(id).map(contact -> {
            contact.setFirstName(firstName);
            contact.setLastName(lastName);
            return contactRepository.save(contact);
        }).orElseThrow(() -> new ContactNotFoundException("Не найден контакт с id: " + id));
    }

    @Override
    public Contact update(Long id, String firstName, String lastName, String displayName,
                          String avatarUrl, Boolean isFavorite) {
        return contactRepository.findById(id).map(contact -> {
            contact.setFirstName(firstName);
            contact.setLastName(lastName);
            contact.setDisplayName(displayName);
            contact.setAvatarUrl(avatarUrl);
            contact.setIsFavorite(isFavorite);
            return contactRepository.save(contact);
        }).orElseThrow(() -> new ContactNotFoundException("Не найден контакт с id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Contact> findAll() {
        return StreamSupport.stream(contactRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public List<Contact> findByName(String name) {
        return contactRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name, name);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Contact> findAllByFullName(String firstName, String lastName) {
        return contactRepository.findByFirstNameAndLastName(firstName, lastName);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Contact> findByTag(String tagName) {
        return contactRepository.findContactsByTagName(tagName);
    }

    @Override
    public Contact updateAvatar(Long contactId, String avatarUrl) {
        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new ContactNotFoundException("Не найден контакт с id: " + contactId));
        contact.setAvatarUrl(avatarUrl);
        return contactRepository.save(contact);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Contact> search(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return findAll();
        }
        return contactRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(searchTerm, searchTerm);
    }

    @Override
    public Contact save(Contact contact) {
        return contactRepository.save(contact);
    }

    @Override
    public Long countTotal() {
        return contactRepository.count();
    }

    @Override
    public Long countFavorites() {
        return contactRepository.countByIsFavoriteTrue();
    }

    @Override
    public void addToFavorites(Long contactId) {
        contactRepository.findById(contactId)
                .ifPresent(contact -> {
                    contact.setIsFavorite(true);
                    contactRepository.save(contact);
                });
    }

    @Override
    public void removeFromFavorites(Long contactId) {
        contactRepository.findById(contactId)
                .ifPresent(contact -> {
                    contact.setIsFavorite(false);
                    contactRepository.save(contact);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Contact> findFavorites() {
        return contactRepository.findByIsFavoriteTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Contact> findBirthdaysThisMonth() {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        List<Event> birthdayEvents = eventService.findByEventTypeAndEventDateBetween(
                EventType.BIRTHDAY, startOfMonth, endOfMonth);

        return birthdayEvents.stream()
                .map(Event::getContact)
                .distinct()
                .collect(Collectors.toList());
    }

    // TODO: Искать по названиям вместо id - очень дорого. Мб отрефакторить
    @Override
    @Transactional(readOnly = true)
    public Page<Contact> searchContacts(String searchTerm, String companyName, String tagName, Pageable pageable) {
        if (!StringUtils.hasText(searchTerm) && !StringUtils.hasText(companyName) && !StringUtils.hasText(tagName)) {
            return contactRepository.findAll(pageable);
        }

        String processedSearchTerm = StringUtils.hasText(searchTerm) ? searchTerm.trim() : null;
        String processedCompanyName = StringUtils.hasText(companyName) ? companyName.trim() : null;
        String processedTagName = StringUtils.hasText(tagName) ? tagName.trim() : null;

        return contactRepository.searchWithFilters(
                processedSearchTerm,
                processedCompanyName,
                processedTagName,
                pageable
        );
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Contact> findAll(Pageable pageable) {
        return contactRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Contact> findFavorites(Pageable pageable) {
        return contactRepository.findByIsFavoriteTrue(pageable);
    }

    @Override
    public List<Contact> findWithUpcomingBirthdays(int daysAhead) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(daysAhead);

        // Для случаев, когда период переходит через год
        if (today.getYear() == endDate.getYear()) {
            // Период в пределах одного года
            return contactRepository.findContactsWithUpcomingBirthdays(
                    today.getMonthValue(),
                    endDate.getMonthValue(),
                    today.getDayOfMonth(),
                    endDate.getDayOfMonth()
            );
        } else {
            // Период переходит через год - нужно два запроса
            List<Contact> firstPart = contactRepository.findContactsWithUpcomingBirthdays(
                    today.getMonthValue(),
                    12,
                    today.getDayOfMonth(),
                    31
            );

            List<Contact> secondPart = contactRepository.findContactsWithUpcomingBirthdays(
                    1,
                    endDate.getMonthValue(),
                    1,
                    endDate.getDayOfMonth()
            );

            List<Contact> result = new ArrayList<>();
            result.addAll(firstPart);
            result.addAll(secondPart);
            return result;
        }
    }
}
