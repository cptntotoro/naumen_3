package ru.anastasia.NauJava.service.contact.impl;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.anastasia.NauJava.config.AppConfig;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.entity.contact.Event;
import ru.anastasia.NauJava.entity.enums.EventType;
import ru.anastasia.NauJava.repository.contact.ContactRepository;
import ru.anastasia.NauJava.service.contact.ContactService;
import ru.anastasia.NauJava.service.contact.EventService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
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

    @Autowired
    public ContactServiceImpl(ContactRepository contactRepository, EventService eventService, AppConfig appConfig) {
        this.contactRepository = contactRepository;
        this.eventService = eventService;
        this.appConfig = appConfig;
    }

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
        return contactRepository.findById(id).orElse(null);
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
        }).orElseThrow(() -> new RuntimeException("Не найден контакт с id: " + id));
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
                .orElseThrow(() -> new RuntimeException("Контакт не найден"));
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
}
