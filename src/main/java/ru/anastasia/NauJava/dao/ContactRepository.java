package ru.anastasia.NauJava.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.anastasia.NauJava.entity.Contact;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий контактов
 */
@Component
public class ContactRepository implements CrudRepository<Contact, Long> {

    /**
     * Хранилище контактов
     */
    private final List<Contact> contactContainer;

    /**
     * Счётчик для автоматической генерации ID
     */
    private Long idCounter = 1L;

    @Autowired
    public ContactRepository(List<Contact> contactContainer) {
        this.contactContainer = contactContainer;
    }

    @Override
    public void create(Contact contact) {
        contact.setId(idCounter++);
        contactContainer.add(contact);
    }

    @Override
    public Contact read(Long id) {
        return contactContainer.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void update(Contact contact) {
        Optional<Contact> existing = contactContainer.stream()
                .filter(c -> c.getId().equals(contact.getId()))
                .findFirst();
        existing.ifPresent(c -> {
            c.setName(contact.getName());
            c.setPhone(contact.getPhone());
            c.setEmail(contact.getEmail());
        });
    }

    @Override
    public void delete(Long id) {
        contactContainer.removeIf(c -> c.getId().equals(id));
    }

    /**
     * Получить все сущности
     *
     * @return Список сущностей
     */
    public List<Contact> findAll() {
        return contactContainer;
    }

    /**
     * Получить список сущностей по названию
     *
     * @param name Название
     * @return Список сущностей
     */
    public List<Contact> findByName(String name) {
        return contactContainer.stream()
                .filter(c -> c.getName().toLowerCase().contains(name.toLowerCase()))
                .toList();
    }
}
