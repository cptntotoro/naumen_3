package ru.anastasia.NauJava.service.tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.entity.tag.ContactTag;
import ru.anastasia.NauJava.entity.tag.Tag;
import ru.anastasia.NauJava.repository.contact.ContactRepository;
import ru.anastasia.NauJava.repository.tag.ContactTagRepository;
import ru.anastasia.NauJava.repository.tag.TagRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TagServiceImpl implements TagService {
    /**
     * Репозиторий тегов
     */
    private final TagRepository tagRepository;

    /**
     * Репозиторий связи контактов и тегов
     */
    private final ContactTagRepository contactTagRepository;

    /**
     * Репозиторий контактов
     */
    private final ContactRepository contactRepository;

    @Autowired
    public TagServiceImpl(TagRepository tagRepository, ContactTagRepository contactTagRepository, ContactRepository contactRepository) {
        this.tagRepository = tagRepository;
        this.contactTagRepository = contactTagRepository;
        this.contactRepository = contactRepository;
    }

    @Override
    public Tag create(String name, String color) {
        return tagRepository.findByName(name)
                .orElseGet(() -> tagRepository.save(new Tag(name, color)));
    }

    @Override
    @Transactional(readOnly = true)
    public Tag findByName(String name) {
        return tagRepository.findByName(name).orElse(null);
    }

    @Override
    public ContactTag addToContact(Long contactId, String tagName) {
        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new RuntimeException("Не найден контакт с id: " + contactId));
        Tag tag = create(tagName, "#808080");
        ContactTag contactTag = new ContactTag(contact, tag);
        return contactTagRepository.save(contactTag);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tag> findByContactId(Long contactId) {
        return contactTagRepository.findByContactId(contactId)
                .stream()
                .map(ContactTag::getTag)
                .collect(Collectors.toList());
    }
}