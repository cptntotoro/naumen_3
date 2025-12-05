package ru.anastasia.NauJava.service.contact.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
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
        log.info("Инициализация ContactServiceImpl для приложения: {} версия: {}",
                appConfig.getAppName(), appConfig.getAppVersion());
    }

    @Override
    public Contact add(String firstName, String lastName) {
        log.info("Создание нового контакта: {} {}", firstName, lastName);
        Contact contact = Contact.builder()
                .firstName(firstName)
                .lastName(lastName)
                .build();
        Contact savedContact = contactRepository.save(contact);
        log.info("Контакт создан с ID: {}", savedContact.getId());
        return savedContact;
    }

    @Override
    public Contact findById(Long id) {
        log.debug("Поиск контакта по ID: {}", id);
        return contactRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Контакт с ID: {} не найден", id);
                    return new ContactNotFoundException("Не найден контакт с id: " + id);
                });
    }

    @Override
    public void deleteById(Long id) {
        log.info("Удаление контакта с ID: {}", id);
        if (!contactRepository.existsById(id)) {
            log.warn("Попытка удаления несуществующего контакта с ID: {}", id);
            throw new ContactNotFoundException("Не найден контакт с id: " + id);
        }
        contactRepository.deleteById(id);
        log.info("Контакт с ID: {} успешно удален", id);
    }

    @Override
    public Contact update(Long id, String firstName, String lastName) {
        log.info("Обновление базовой информации контакта с ID: {}", id);
        return contactRepository.findById(id).map(contact -> {
            contact.setFirstName(firstName);
            contact.setLastName(lastName);
            Contact updatedContact = contactRepository.save(contact);
            log.info("Контакт с ID: {} обновлен: {} {}", id, firstName, lastName);
            return updatedContact;
        }).orElseThrow(() -> {
            log.error("Не удалось обновить контакт с ID: {} - контакт не найден", id);
            return new ContactNotFoundException("Не найден контакт с id: " + id);
        });
    }

    @Override
    public Contact update(Long id, String firstName, String lastName, String displayName,
                          String avatarUrl, Boolean isFavorite) {
        log.info("Полное обновление контакта с ID: {}", id);
        return contactRepository.findById(id).map(contact -> {
            contact.setFirstName(firstName);
            contact.setLastName(lastName);
            contact.setDisplayName(displayName);
            contact.setAvatarUrl(avatarUrl);
            contact.setIsFavorite(isFavorite);
            Contact updatedContact = contactRepository.save(contact);
            log.info("Контакт с ID: {} полностью обновлен", id);
            return updatedContact;
        }).orElseThrow(() -> {
            log.error("Не удалось обновить контакт с ID: {} - контакт не найден", id);
            return new ContactNotFoundException("Не найден контакт с id: " + id);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Contact> findAll() {
        log.debug("Получение всех контактов");
        List<Contact> contacts = new ArrayList<>(contactRepository.findAll());
        log.debug("Загружено {} контактов", contacts.size());
        return contacts;
    }

    @Override
    public List<Contact> findByName(String name) {
        log.debug("Поиск контактов по имени: '{}'", name);
        List<Contact> contacts = contactRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name, name);
        log.debug("Найдено {} контактов по имени: '{}'", contacts.size(), name);
        return contacts;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Contact> findAllByFullName(String firstName, String lastName) {
        log.debug("Поиск контактов по полному имени: {} {}", firstName, lastName);
        List<Contact> contacts = contactRepository.findByFirstNameAndLastName(firstName, lastName);
        log.debug("Найдено {} контактов с именем {} {}", contacts.size(), firstName, lastName);
        return contacts;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Contact> findByTag(String tagName) {
        log.debug("Поиск контактов по тегу: '{}'", tagName);
        List<Contact> contacts = contactRepository.findContactsByTagName(tagName);
        log.debug("Найдено {} контактов с тегом '{}'", contacts.size(), tagName);
        return contacts;
    }

    @Override
    public Contact updateAvatar(Long contactId, String avatarUrl) {
        log.info("Обновление аватара контакта с ID: {}", contactId);
        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> {
                    log.warn("Контакт с ID: {} не найден для обновления аватара", contactId);
                    return new ContactNotFoundException("Не найден контакт с id: " + contactId);
                });
        contact.setAvatarUrl(avatarUrl);
        Contact updatedContact = contactRepository.save(contact);
        log.info("Аватар контакта с ID: {} обновлен", contactId);
        return updatedContact;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Contact> search(String searchTerm) {
        log.debug("Поиск контактов по строке: '{}'", searchTerm);
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            log.debug("Пустой поисковый запрос, возвращены все контакты");
            return findAll();
        }
        List<Contact> contacts = contactRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(searchTerm, searchTerm);
        log.debug("Поиск по '{}' вернул {} контактов", searchTerm, contacts.size());
        return contacts;
    }

    @Override
    public Contact save(Contact contact) {
        log.debug("Сохранение контакта: {} {}", contact.getFirstName(), contact.getLastName());
        Contact savedContact = contactRepository.save(contact);
        log.trace("Контакт сохранен с ID: {}", savedContact.getId());
        return savedContact;
    }

    @Override
    public Long countTotal() {
        log.debug("Подсчет общего количества контактов");
        Long count = contactRepository.count();
        log.debug("Общее количество контактов: {}", count);
        return count;
    }

    @Override
    public Long countFavorites() {
        log.debug("Подсчет количества избранных контактов");
        Long count = contactRepository.countByIsFavoriteTrue();
        log.debug("Количество избранных контактов: {}", count);
        return count;
    }

    @Override
    public void addToFavorites(Long contactId) {
        log.info("Добавление контакта с ID: {} в избранное", contactId);
        contactRepository.findById(contactId)
                .ifPresentOrElse(contact -> {
                    contact.setIsFavorite(true);
                    contactRepository.save(contact);
                    log.info("Контакт с ID: {} добавлен в избранное", contactId);
                }, () -> log.warn("Попытка добавить в избранное несуществующий контакт с ID: {}", contactId));
    }

    @Override
    public void removeFromFavorites(Long contactId) {
        log.info("Удаление контакта с ID: {} из избранного", contactId);
        contactRepository.findById(contactId)
                .ifPresentOrElse(contact -> {
                    contact.setIsFavorite(false);
                    contactRepository.save(contact);
                    log.info("Контакт с ID: {} удален из избранного", contactId);
                }, () -> log.warn("Попытка удалить из избранного несуществующий контакт с ID: {}", contactId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Contact> findBirthdaysThisMonth() {
        log.debug("Поиск контактов с днями рождения в текущем месяце");
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        List<Event> birthdayEvents = eventService.findByEventTypeAndEventDateBetween(
                EventType.BIRTHDAY, startOfMonth, endOfMonth);

        List<Contact> contacts = birthdayEvents.stream()
                .map(Event::getContact)
                .distinct()
                .collect(Collectors.toList());

        log.debug("Найдено {} контактов с днями рождения в текущем месяце", contacts.size());
        return contacts;
    }

    // TODO: Искать по названиям вместо id - очень дорого. Мб отрефакторить
    @Override
    @Transactional(readOnly = true)
    public Page<Contact> searchContacts(String searchTerm, String companyName, String tagName, Pageable pageable) {
        log.debug("Расширенный поиск контактов. Поиск: '{}', компания: '{}', тег: '{}', страница: {}",
                searchTerm, companyName, tagName, pageable.getPageNumber());

        if (!StringUtils.hasText(searchTerm) && !StringUtils.hasText(companyName) && !StringUtils.hasText(tagName)) {
            log.debug("Параметры поиска не указаны, возвращена полная страница контактов");
            return contactRepository.findAll(pageable);
        }

        String processedSearchTerm = StringUtils.hasText(searchTerm) ? searchTerm.trim() : null;
        String processedCompanyName = StringUtils.hasText(companyName) ? companyName.trim() : null;
        String processedTagName = StringUtils.hasText(tagName) ? tagName.trim() : null;

        Page<Contact> result = contactRepository.searchWithFilters(
                processedSearchTerm,
                processedCompanyName,
                processedTagName,
                pageable
        );

        log.debug("Расширенный поиск вернул {} контактов на странице {}", result.getContent().size(), pageable.getPageNumber());
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Contact> findAll(Pageable pageable) {
        log.debug("Получение страницы контактов: {}", pageable.getPageNumber());
        Page<Contact> page = contactRepository.findAll(pageable);
        log.debug("Загружена страница {} с {} контактами (всего: {})",
                pageable.getPageNumber(), page.getContent().size(), page.getTotalElements());
        return page;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Contact> findFavorites(Pageable pageable) {
        log.debug("Получение страницы избранных контактов: {}", pageable.getPageNumber());
        Page<Contact> page = contactRepository.findByIsFavoriteTrue(pageable);
        log.debug("Загружена страница {} с {} избранными контактами (всего: {})",
                pageable.getPageNumber(), page.getContent().size(), page.getTotalElements());
        return page;
    }

    @Override
    public List<Contact> findWithUpcomingBirthdays(int daysAhead) {
        log.debug("Поиск контактов с предстоящими днями рождения в течение {} дней", daysAhead);
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(daysAhead);

        String todayStr = today.format(DateTimeFormatter.ofPattern("MM-dd"));
        String endDateStr = endDate.format(DateTimeFormatter.ofPattern("MM-dd"));

        List<Contact> result;

        if (todayStr.compareTo(endDateStr) <= 0) {
            // Диапазон в пределах одного года (не пересекает новый год)
            result = contactRepository.findBirthdaysInRange(todayStr, endDateStr);
        } else {
            // Диапазон пересекает новый год
            result = contactRepository.findBirthdaysCrossingYear(todayStr, endDateStr);
        }

        log.debug("Найдено {} контактов с предстоящими днями рождения в течение {} дней", result.size(), daysAhead);
        return result;
    }

    @Override
    public boolean existsById(Long contactId) {
        return contactRepository.existsById(contactId);
    }
}
