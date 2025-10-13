package ru.anastasia.NauJava.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.anastasia.NauJava.config.AppConfig;
import ru.anastasia.NauJava.dao.ContactRepository;
import ru.anastasia.NauJava.entity.Contact;

import java.util.List;

@Service
public class ContactServiceImpl implements ContactService {
    /**
     * Репозиторий контактов
     */
    private final ContactRepository contactRepository;

    /**
     * Конфигурация приложения
     */
    private final AppConfig appConfig;


    @Autowired
    public ContactServiceImpl(ContactRepository contactRepository, AppConfig appConfig) {
        this.contactRepository = contactRepository;
        this.appConfig = appConfig;
    }

    @PostConstruct
    public void init() {
        System.out.println("Название приложения: " + appConfig.getAppName());
        System.out.println("Версия приложения: " + appConfig.getAppVersion());
    }

    @Override
    public void addContact(String name, String phone, String email) {
        Contact contact = new Contact();
        contact.setName(name);
        contact.setPhone(phone);
        contact.setEmail(email);
        contactRepository.create(contact);
    }

    @Override
    public Contact findById(Long id) {
        return contactRepository.read(id);
    }

    @Override
    public void deleteById(Long id) {
        contactRepository.delete(id);
    }

    @Override
    public void updateContact(Long id, String name, String phone, String email) {
        Contact contact = new Contact();
        contact.setId(id);
        contact.setName(name);
        contact.setPhone(phone);
        contact.setEmail(email);
        contactRepository.update(contact);
    }

    @Override
    public List<Contact> listAll() {
        return contactRepository.findAll();
    }

    @Override
    public List<Contact> searchByName(String name) {
        return contactRepository.findByName(name);
    }
}
