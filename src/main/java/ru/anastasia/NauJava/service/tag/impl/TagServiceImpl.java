package ru.anastasia.NauJava.service.tag.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.entity.tag.ContactTag;
import ru.anastasia.NauJava.entity.tag.Tag;
import ru.anastasia.NauJava.repository.contact.ContactRepository;
import ru.anastasia.NauJava.repository.tag.ContactTagRepository;
import ru.anastasia.NauJava.repository.tag.TagRepository;
import ru.anastasia.NauJava.service.tag.TagService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Transactional
@RequiredArgsConstructor
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

    @Override
    public Tag create(Tag tag) {
        try {
            return tagRepository.save(tag);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Не удалось создать тег: " + tag.getName() + ". " +
                    "Тег с таким именем уже существует");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Tag findByName(String name) {
        return tagRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Тег с таким именем уже существует"));
    }

    @Override
    public ContactTag addToContact(Long contactId, String tagName) {
        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new RuntimeException("Не найден контакт с id: " + contactId));

        Tag tagToCreate = Tag.builder()
                .name(tagName)
                .color("#808080")
                .build();

        Tag tag = create(tagToCreate);

        ContactTag contactTag = ContactTag.builder()
                .contact(contact)
                .tag(tag)
                .build();
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

    @Override
    public List<Tag> findAll() {
        return StreamSupport.stream(tagRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public Tag findById(Long id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Тег не найден с id: " + id));
    }

    @Override
    public Tag update(Tag tag) {
        try {
            return tagRepository.save(tag);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Тег с таким именем уже существует");
        }
    }

    @Override
    public void delete(Long id) {
        tagRepository.deleteById(id);
    }
}